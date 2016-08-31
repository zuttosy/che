package org.eclipse.che.api.environment.server;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.core.model.machine.ServerConf;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;
import org.eclipse.che.api.environment.server.compose.ComposeFileParser;
import org.eclipse.che.api.environment.server.compose.model.BuildContextImpl;
import org.eclipse.che.api.environment.server.compose.model.ComposeEnvironmentImpl;
import org.eclipse.che.api.environment.server.compose.model.ComposeServiceImpl;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.shared.dto.MachineConfigDto;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.dto.server.DtoFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Alexander Garagatyi
 */
public class EnvironmentParser {
    private static final Logger LOG = getLogger(EnvironmentParser.class);
    private static final List<String> types = Arrays.asList("compose", "docker");

    private final ComposeFileParser composeFileParser;
    private final URI               apiEndpoint;

    @Inject
    public EnvironmentParser(ComposeFileParser composeFileParser,
                             @Named("api.endpoint") URI apiEndpoint) {
        this.composeFileParser = composeFileParser;
        this.apiEndpoint = apiEndpoint;
    }

    public List<String> getEnvironmentTypes() {
        return types;
    }

    public ComposeEnvironmentImpl parse(Environment environment) throws IllegalArgumentException,
                                                                        ServerException {

        checkNotNull(environment, "Environment should not be null");
        checkNotNull(environment.getRecipe(), "Environment recipe should not be null");
        checkNotNull(environment.getRecipe().getType(), "Environment recipe type should not be null");
        checkNotNull(environment.getRecipe().getContentType(), "Content type of environment recipe should not be null");
        checkArgument(environment.getRecipe().getContent() != null || environment.getRecipe().getLocation() != null,
                      "Recipe of environment must contain location or content");

        String recipeContent = getContentOfRecipe(environment.getRecipe());

        ComposeEnvironmentImpl composeEnvironment;
        String envType = environment.getRecipe().getType();
        switch (envType) {
            case "compose":
                composeEnvironment = composeFileParser.parse(recipeContent, environment.getRecipe().getContentType());
                break;
            case "docker":
                composeEnvironment = parseDocker(recipeContent, environment.getRecipe().getContentType());
                break;
            default:
                throw new IllegalArgumentException("Environment type " + envType + " is not supported");
        }

        return composeEnvironment;
    }

    private ComposeEnvironmentImpl parseDocker(String recipeContent, String contentType) throws ServerException {
        List<MachineConfigDto> configs =
                DtoFactory.getInstance().createListDtoFromJson(recipeContent, MachineConfigDto.class);

        ComposeEnvironmentImpl composeEnvironment = new ComposeEnvironmentImpl();

        for (MachineConfigDto config : configs) {
            composeEnvironment.getServices().put(config.getName(), asService(config));
        }
        return composeEnvironment;
    }

    /** Converts machine configuration to service. */
    private ComposeServiceImpl asService(MachineConfig config) throws IllegalArgumentException,
                                                                      ServerException {
        String machineName = config.getName();
        checkArgument(!isNullOrEmpty(machineName), "Machine name is null or empty");
        checkNotNull(config.getSource(), "Machine '%s' doesn't have source", machineName);
        checkArgument(config.getSource().getContent() != null || config.getSource().getLocation() != null,
                      "Source of machine '%s' must contain location or content", machineName);
        checkArgument(config.getSource().getContent() == null || config.getSource().getLocation() == null,
                      "Source of machine '%s' contains mutually exclusive fields location and content",
                      machineName);

        ComposeServiceImpl composeService = new ComposeServiceImpl();
        composeService.setMemLimit(config.getLimits().getRam() * 1024L * 1024L);
        composeService.setEnvironment(config.getEnvVariables());

        switch (config.getSource().getType()) {
            case "image":
                composeService.setImage(config.getSource().getLocation());
                break;
            case "dockerfile":
                if (config.getSource().getContent() != null) {
                    composeService.setBuild(new BuildContextImpl(null, config.getSource().getContent()));
                } else {
                    composeService.setBuild(new BuildContextImpl(config.getSource().getLocation(), null));
                }
                break;
            default:
                throw new IllegalArgumentException(format("Source type %s of machine '%s' in not supported",
                                                          config.getSource().getType(), machineName));
        }
        List<? extends ServerConf> servers = config.getServers();
        if (servers != null) {
            List<String> expose = new ArrayList<>();
            for (ServerConf server : servers) {
                expose.add(server.getPort());
            }
            composeService.setExpose(expose);
        }

        return composeService;
// TODO labels of servers, isdev
    }

    /**
     * Checks that object reference is not null, throws {@link IllegalArgumentException} otherwise.
     *
     * <p>Exception uses error message built from error message template and error message parameters.
     */
    private static void checkNotNull(Object object, String errorMessageTemplate, Object... errorMessageParams) {
        if (object == null) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageParams));
        }
    }

    private String getContentOfRecipe(EnvironmentRecipe environmentRecipe) throws ServerException {
        if (environmentRecipe.getContent() != null) {
            return environmentRecipe.getContent();
        } else {
            return getRecipe(environmentRecipe.getLocation());
        }
    }

    private String getRecipe(String location) throws ServerException {
        URL recipeUrl;
        File file = null;
        try {
            UriBuilder targetUriBuilder = UriBuilder.fromUri(location);
            // add user token to be able to download user's private recipe
            final String apiEndPointHost = apiEndpoint.getHost();
            final String host = targetUriBuilder.build().getHost();
            if (apiEndPointHost.equals(host)) {
                if (EnvironmentContext.getCurrent().getSubject() != null
                    && EnvironmentContext.getCurrent().getSubject().getToken() != null) {
                    targetUriBuilder.queryParam("token", EnvironmentContext.getCurrent().getSubject().getToken());
                }
            }
            recipeUrl = targetUriBuilder.build().toURL();
            file = IoUtil.downloadFileWithRedirect(null, "recipe", null, recipeUrl);

            return IoUtil.readAndCloseQuietly(new FileInputStream(file));
        } catch (IOException | IllegalArgumentException e) {
            throw new MachineException(format("Recipe downloading failed. Recipe url %s. Error: %s",
                                              location,
                                              e.getLocalizedMessage()));
        } finally {
            if (file != null && !file.delete()) {
                LOG.error(String.format("Removal of recipe file %s failed.", file.getAbsolutePath()));
            }
        }
    }
}
