/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.environment.server;

import com.google.common.base.Joiner;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;
import org.eclipse.che.api.core.model.workspace.ExtendedMachine;
import org.eclipse.che.api.environment.server.compose.ComposeFileParser;
import org.eclipse.che.api.environment.server.compose.model.BuildContextImpl;
import org.eclipse.che.api.environment.server.compose.model.ComposeEnvironmentImpl;
import org.eclipse.che.api.environment.server.compose.model.ComposeServiceImpl;
import org.eclipse.che.api.machine.server.util.RecipeDownloader;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

/**
 * @author Alexander Garagatyi
 */
public class EnvironmentParser {
    private static final List<String> types = Arrays.asList("compose", "dockerimage", "dockerfile");

    private final ComposeFileParser composeFileParser;
    private final RecipeDownloader  recipeDownloader;

    @Inject
    public EnvironmentParser(ComposeFileParser composeFileParser,
                             RecipeDownloader recipeDownloader) {
        this.composeFileParser = composeFileParser;
        this.recipeDownloader = recipeDownloader;
    }

    public List<String> getEnvironmentTypes() {
        return types;
    }

    public ComposeEnvironmentImpl parse(Environment environment) throws IllegalArgumentException,
                                                                        ServerException {

        checkNotNull(environment, "Environment should not be null");
        checkNotNull(environment.getRecipe(), "Environment recipe should not be null");
        checkNotNull(environment.getRecipe().getType(), "Environment recipe type should not be null");
        checkArgument(environment.getRecipe().getContent() != null || environment.getRecipe().getLocation() != null,
                      "Recipe of environment must contain location or content");

        ComposeEnvironmentImpl composeEnvironment;
        String envType = environment.getRecipe().getType();
        switch (envType) {
            case "compose":
                composeEnvironment = parseCompose(environment.getRecipe());
                break;
            case "dockerimage":
            case "dockerfile":
                composeEnvironment = parseDocker(environment);
                break;
            default:
                throw new IllegalArgumentException("Environment type " + envType + " is not supported");
        }

        composeEnvironment.getServices().forEach((name, service) -> {
            ExtendedMachine extendedMachine = environment.getMachines().get(name);
            checkArgument(extendedMachine != null &&
                          extendedMachine.getResources() != null &&
                          extendedMachine.getResources().getLimits() != null &&
                          extendedMachine.getResources().getLimits().getMemoryBytes() > 0,
                         "Memory limit of machine '%s' is missing or contain invalid value",
                         name);

            service.setMemLimit(extendedMachine.getResources().getLimits().getMemoryBytes());
        });

        return composeEnvironment;
    }

    private ComposeEnvironmentImpl parseCompose(EnvironmentRecipe recipe) throws ServerException {
        String recipeContent = getContentOfRecipe(recipe);

        return composeFileParser.parse(recipeContent, recipe.getContentType());
    }

    private ComposeEnvironmentImpl parseDocker(Environment environment) {
        checkArgument(environment.getMachines().size() == 1,
                      "Environment of type '%s' doesn't support multiple machines, but contains machines: %s",
                      environment.getRecipe().getType(),
                      Joiner.on(',').join(environment.getMachines().keySet()));

        ComposeEnvironmentImpl composeEnvironment = new ComposeEnvironmentImpl();
        ComposeServiceImpl service = new ComposeServiceImpl();

        composeEnvironment.getServices().put(environment.getMachines()
                                                        .keySet()
                                                        .iterator()
                                                        .next(), service);

        EnvironmentRecipe recipe = environment.getRecipe();

        if ("dockerimage".equals(environment.getRecipe().getType())) {
            service.setImage(recipe.getLocation());
        } else {
            if (!"x-dockerfile".equals(recipe.getContentType())) {
                throw new IllegalArgumentException(
                        format("Content type of recipe of provided environment '%s' is unsupported. Supported values are: x-dockerfile, image",
                               recipe.getContentType()));
            }

            if (recipe.getLocation() != null) {
                service.setBuild(new BuildContextImpl().withContext(recipe.getLocation()));
            } else {
                // workaround: put dockerfile content into field dockerfile.
                // Service launching code must know about that workaround
                service.setBuild(new BuildContextImpl().withDockerfile(recipe.getContent()));
            }
        }

        return composeEnvironment;
    }

    private String getContentOfRecipe(EnvironmentRecipe environmentRecipe) throws ServerException {
        if (environmentRecipe.getContent() != null) {
            return environmentRecipe.getContent();
        } else {
            return recipeDownloader.getRecipe(environmentRecipe.getLocation());
        }
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
}
