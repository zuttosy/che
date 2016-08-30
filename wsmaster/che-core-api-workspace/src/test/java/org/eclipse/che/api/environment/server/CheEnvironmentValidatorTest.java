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

import org.eclipse.che.api.machine.server.MachineInstanceProviders;
import org.eclipse.che.api.workspace.server.DtoConverter;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentRecipeImpl;
import org.eclipse.che.api.workspace.server.model.impl.ExtendedMachineImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerConf2Impl;
import org.eclipse.che.api.workspace.shared.dto.EnvironmentDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Garagatyi
 */
@Listeners(MockitoTestNGListener.class)
public class CheEnvironmentValidatorTest {
    @Mock
    MachineInstanceProviders machineInstanceProviders;
    @InjectMocks
    CheEnvironmentValidator  environmentValidator;

    @BeforeMethod
    public void prepare() throws Exception {
        when(machineInstanceProviders.hasProvider("docker")).thenReturn(true);
        when(machineInstanceProviders.getProviderTypes()).thenReturn(Arrays.asList("docker", "ssh"));
    }


    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Environment name should not be neither null nor empty")
    public void shouldFailValidationIfEnvNameIsNull() throws Exception {
        // given
        EnvironmentDto environment = createEnv();

        // when
        environmentValidator.validate(null, environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Environment name should not be neither null nor empty")
    public void shouldFailValidationIfEnvNameIsEmpty() throws Exception {
        // given
        EnvironmentDto environment = createEnv();

        // when
        environmentValidator.validate("", environment);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Type 'docker' of environment 'env' is not supported. Supported types: compose")
    public void shouldFailValidationIfEnvironmentRecipeTypeIsNotCompose() throws Exception {
        // given
        EnvironmentDto config = createEnv();
        config.getRecipe().setType("docker");

        // when
        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment '.*' should contain at least 1 machine")
    public void shouldFailValidationIfMachinesListIsEmpty() throws Exception {
        EnvironmentDto config = createEnv();
//        config.withMachineConfigs(null);


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment '.*' should contain exactly 1 dev machine, but contains '0'")
    public void shouldFailValidationIfNoDevMachineFound() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .stream()
//              .filter(MachineConfigDto::isDev)
//              .forEach(machine -> machine.withDev(false));


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment '.*' should contain exactly 1 dev machine, but contains '2'")
    public void shouldFailValidationIf2DevMachinesFound() throws Exception {
        EnvironmentDto config = createEnv();
//        final Optional<MachineConfigDto> devMachine = config.getMachineConfigs()
//                                                            .stream()
//                                                            .filter(MachineConfigDto::isDev)
//                                                            .findAny();
//        config.getMachineConfigs()
//              .add(devMachine.get().withName("other-name"));


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment .* contains machine with null or empty name")
    public void shouldFailValidationIfMachineNameIsNull() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withName(null);


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment .* contains machine with null or empty name")
    public void shouldFailValidationIfMachineNameIsEmpty() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withName("");


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine '.*' in environment '.*' doesn't have source")
    public void shouldFailValidationIfMachineSourceIsNull() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withSource(null);


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Type 'null' of machine '.*' in environment '.*' is not supported. Supported values are: docker, ssh.")
    public void shouldFailValidationIfMachineTypeIsNull() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withType(null);


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Type 'compose' of machine '.*' in environment '.*' is not supported. Supported values are: docker, ssh.")
    public void shouldFailValidationIfMachineTypeIsNotDocker() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withType("compose");


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine .* contains server conf with invalid port .*",
//          dataProvider = "invalidPortProvider")
    public void shouldFailValidationIfServerConfPortIsInvalid(String invalidPort) throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .getServers()
//              .add(newDto(ServerConfDto.class).withPort(invalidPort));


        environmentValidator.validate("env", config);
    }

    @DataProvider(name = "invalidPortProvider")
    public static Object[][] invalidPortProvider() {
        return new Object[][] {
                {"0"},
                {"0123"},
                {"012/tcp"},
                {"8080"},
                {"8080/pct"},
                {"8080/pdu"},
                {"/tcp"},
                {"tcp"},
                {""},
                {"8080/tcp1"},
                {"8080/tcpp"},
                {"8080tcp"},
                {"8080/tc"},
                {"8080/ud"},
                {"8080/udpp"},
                {"8080/udp/"},
                {"8080/tcp/"},
                {"8080/tcp/udp"},
                {"8080/tcp/tcp"},
                {"8080/tcp/8080"},
                {null}
        };
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine .* contains server conf with invalid protocol .*",
//          dataProvider = "invalidProtocolProvider")
    public void shouldFailValidationIfServerConfProtocolIsInvalid(String invalidProtocol) throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .getServers()
//              .add(newDto(ServerConfDto.class).withPort("8080/tcp")
//                                              .withProtocol(invalidProtocol));


        environmentValidator.validate("env", config);
    }

    @DataProvider(name = "invalidProtocolProvider")
    public static Object[][] invalidProtocolProvider() {
        return new Object[][] {
                {""},
                {"http!"},
                {"2http"},
                {"http:"},
                };
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine '.*' in environment '.*' contains environment variable with null or empty name")
    public void shouldFailValidationIfEnvVarNameIsNull() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .getEnvVariables()
//              .put(null, "value");


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine '.*' in environment '.*' contains environment variable with null or empty name")
    public void shouldFailValidationIfEnvVarNameIsEmpty() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .getEnvVariables()
//              .put("", "value");


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Machine '.*' in environment '.*' contains environment variable '.*' with null value")
    public void shouldFailValidationIfEnvVarValueIsNull() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .getEnvVariables()
//              .put("key", null);


        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Source of machine '.*' in environment '.*' must contain location or content")
    public void shouldFailValidationIfMissingSourceLocationAndContent() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withSource(newDto(MachineSourceDto.class).withType("dockerfile"));

        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment '.*' contains machine '.*' with invalid source location: 'localhost'")
    public void shouldFailValidationIfLocationIsInvalidUrl() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withSource(newDto(MachineSourceDto.class).withType("dockerfile").withLocation("localhost"));

        environmentValidator.validate("env", config);
    }

//    @Test(expectedExceptions = IllegalArgumentException.class,
//          expectedExceptionsMessageRegExp = "Environment '.*' contains machine '.*' with invalid source location protocol: ftp://localhost")
    public void shouldFailValidationIfLocationHasInvalidProtocol() throws Exception {
        EnvironmentDto config = createEnv();
//        config.getMachineConfigs()
//              .get(0)
//              .withSource(newDto(MachineSourceDto.class).withType("dockerfile").withLocation("ftp://localhost"));

        environmentValidator.validate("env", config);
    }

    private EnvironmentDto createEnv() {
        // singletonMap, asList are wrapped into modifiable collections to ease env modifying by tests
        EnvironmentImpl env = new EnvironmentImpl();
        Map<String, ExtendedMachineImpl> machines = new HashMap<>();
        Map<String, ServerConf2Impl> servers = new HashMap<>();

        servers.put("ref1", new ServerConf2Impl("8080/tcp",
                                                "proto1",
                                                new HashMap<>(singletonMap("prop1", "propValue"))));
        servers.put("ref2", new ServerConf2Impl("8080/udp", "proto1", null));
        servers.put("ref3", new ServerConf2Impl("9090", "proto1", null));
        machines.put("dev-machine", new ExtendedMachineImpl(new ArrayList<>(asList("ws-agent", "someAgent")), servers));
        machines.put("machine2", new ExtendedMachineImpl(new ArrayList<>(asList("someAgent2", "someAgent3")), null));
        String environmentRecipeContent =
                "services:\n  " +
                "dev-machine:\n    image: codenvy/ubuntu_jdk8\n    mem_limit: 4294967296\n  " +
                "machine2:\n    image: codenvy/ubuntu_jdk8\n    mem_limit: 100000";
        env.setRecipe(new EnvironmentRecipeImpl("compose",
                                                "application/x-yaml",
                                                environmentRecipeContent,
                                                null));
        env.setMachines(machines);

        return DtoConverter.asDto(env);
    }
}
