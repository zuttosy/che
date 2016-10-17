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
package org.eclipse.che.ide.extension.machine.client.command;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.core.model.machine.Command;
import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.api.machine.shared.dto.MachineProcessDto;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.command.CommandPage;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandTypeRegistry;
import org.eclipse.che.ide.api.machine.MachineServiceClient;
import org.eclipse.che.ide.api.macro.MacroProcessor;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.workspace.WorkspaceReadyEvent;
import org.eclipse.che.ide.api.workspace.WorkspaceServiceClient;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandOutputConsole;
import org.eclipse.che.ide.extension.machine.client.processes.panel.ProcessesPanelPresenter;
import org.eclipse.che.ide.util.UUID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;

/**
 * Implementation of {@link CommandManager}.
 *
 * @author Artem Zatsarynnyi
 */
public class CommandManagerImpl implements CommandManager {

    public static final String PREVIEW_URL_ATTR = "previewUrl";

    private final CommandTypeRegistry     commandTypeRegistry;
    private final AppContext              appContext;
    private final WorkspaceServiceClient  workspaceServiceClient;
    private final MachineServiceClient    machineServiceClient;
    private final DtoFactory              dtoFactory;
    private final MacroProcessor          macroProcessor;
    private final CommandConsoleFactory   commandConsoleFactory;
    private final ProcessesPanelPresenter processesPanelPresenter;

    private final Map<String, CommandImpl>               workspaceCommands;
    private final Map<Project, Map<String, CommandImpl>> projectCommands;

    private final Set<CommandChangedListener> commandChangedListeners;

    @Inject
    public CommandManagerImpl(CommandTypeRegistry commandTypeRegistry,
                              AppContext appContext,
                              WorkspaceServiceClient workspaceServiceClient,
                              MachineServiceClient machineServiceClient,
                              DtoFactory dtoFactory,
                              EventBus eventBus,
                              MacroProcessor macroProcessor,
                              CommandConsoleFactory commandConsoleFactory,
                              ProcessesPanelPresenter processesPanelPresenter) {
        this.commandTypeRegistry = commandTypeRegistry;
        this.appContext = appContext;
        this.workspaceServiceClient = workspaceServiceClient;
        this.machineServiceClient = machineServiceClient;
        this.dtoFactory = dtoFactory;
        this.macroProcessor = macroProcessor;
        this.commandConsoleFactory = commandConsoleFactory;
        this.processesPanelPresenter = processesPanelPresenter;

        workspaceCommands = new HashMap<>();
        projectCommands = new HashMap<>();

        commandChangedListeners = new HashSet<>();

        eventBus.addHandler(WorkspaceReadyEvent.getType(), new WorkspaceReadyEvent.WorkspaceReadyHandler() {
            @Override
            public void onWorkspaceReady(WorkspaceReadyEvent event) {
                retrieveWorkspaceCommands();
            }
        });
    }

    private void retrieveWorkspaceCommands() {
        workspaceServiceClient.getCommands(appContext.getWorkspaceId()).then(new Operation<List<CommandDto>>() {
            @Override
            public void apply(List<CommandDto> arg) throws OperationException {
                for (Command command : arg) {
                    workspaceCommands.put(command.getName(), new CommandImpl(command));
                }
            }
        });
    }

    @Override
    public List<CommandImpl> getWorkspaceCommands() {
        // return copy of the commands in order to prevent it modification directly
        List<CommandImpl> list = new ArrayList<>(workspaceCommands.size());
        for (CommandImpl command : workspaceCommands.values()) {
            list.add(new CommandImpl(command));
        }

        return list;
    }

    @Override
    public Promise<CommandImpl> create(String type) {
        CommandType commandType = commandTypeRegistry.getCommandTypeById(type);

        return create(getUniqueCommandName(type, null), commandType.getCommandLineTemplate(), type, new HashMap<String, String>());
    }

    @Override
    public Promise<CommandImpl> create(String desirableName, String commandLine, String type, Map<String, String> attributes) {
        final CommandType commandType = commandTypeRegistry.getCommandTypeById(type);

        attributes.put(PREVIEW_URL_ATTR, commandType.getPreviewUrlTemplate());

        final CommandImpl command = new CommandImpl(desirableName, commandLine, type, attributes);

        return addWorkspaceCommand(command);
    }

    private Promise<CommandImpl> addWorkspaceCommand(final CommandImpl command) {
        final CommandDto commandDto = dtoFactory.createDto(CommandDto.class)
                                                .withName(getUniqueCommandName(command.getType(), command.getName()))
                                                .withCommandLine(command.getCommandLine())
                                                .withType(command.getType())
                                                .withAttributes(command.getAttributes());

        return workspaceServiceClient.addCommand(appContext.getWorkspaceId(), commandDto).then(new Function<WorkspaceDto, CommandImpl>() {
            @Override
            public CommandImpl apply(WorkspaceDto arg) throws FunctionException {
                final CommandImpl newCommand = new CommandImpl(command);
                newCommand.setName(commandDto.getName());

                workspaceCommands.put(newCommand.getName(), newCommand);

                fireCommandAdded(newCommand);

                return newCommand;
            }
        });
    }

    @Override
    public Promise<CommandImpl> update(final String commandName, final CommandImpl command) {
        final String name;
        if (commandName.equals(command.getName())) {
            name = commandName;
        } else {
            name = getUniqueCommandName(command.getType(), command.getName());
        }

        final CommandDto commandDto = dtoFactory.createDto(CommandDto.class)
                                                .withName(name)
                                                .withCommandLine(command.getCommandLine())
                                                .withType(command.getType())
                                                .withAttributes(command.getAttributes());

        return workspaceServiceClient.updateCommand(appContext.getWorkspaceId(), commandName, commandDto)
                                     .then(new Function<WorkspaceDto, CommandImpl>() {
                                         @Override
                                         public CommandImpl apply(WorkspaceDto arg) throws FunctionException {
                                             final CommandImpl updatedCommand = new CommandImpl(command);
                                             updatedCommand.setName(commandDto.getName());

                                             workspaceCommands.remove(commandName);
                                             workspaceCommands.put(updatedCommand.getName(), updatedCommand);

                                             fireCommandUpdated(updatedCommand);

                                             return updatedCommand;
                                         }
                                     });
    }

    @Override
    public Promise<Void> remove(final String name) {
        return workspaceServiceClient.deleteCommand(appContext.getWorkspaceId(), name).then(new Function<WorkspaceDto, Void>() {
            @Override
            public Void apply(WorkspaceDto arg) throws FunctionException {
                fireCommandRemoved(workspaceCommands.remove(name));
                return null;
            }
        });
    }

    @Override
    public List<CommandImpl> getProjectCommands(Project project) {
        List<String> attributeValues = project.getAttributes("commands");
        if (attributeValues == null) {
            return emptyList();
        }

        Map<String, CommandImpl> commands = new HashMap<>(attributeValues.size());

        for (String commandJson : attributeValues) {
            Command command = dtoFactory.createDtoFromJson(commandJson, CommandDto.class);

            commands.put(command.getName(), new CommandImpl(command));
        }

        projectCommands.put(project, commands);

        return new ArrayList<>(commands.values());
    }

    @Override
    public Promise<CommandImpl> createProjectCommand(Project project, String type) {
        CommandType commandType = commandTypeRegistry.getCommandTypeById(type);

        return createProjectCommand(project,
                                    getUniqueCommandName(type, null),
                                    commandType.getCommandLineTemplate(),
                                    type,
                                    new HashMap<String, String>());
    }

    @Override
    public Promise<CommandImpl> createProjectCommand(Project project,
                                                     String desirableName,
                                                     String commandLine,
                                                     String type,
                                                     Map<String, String> attributes) {
        final CommandType commandType = commandTypeRegistry.getCommandTypeById(type);

        attributes.put(PREVIEW_URL_ATTR, commandType.getPreviewUrlTemplate());

        final CommandImpl command = new CommandImpl(desirableName, commandLine, type, attributes);

        return addProjectCommand(project, command);
    }

    private Promise<CommandImpl> addProjectCommand(Project project, final CommandImpl command) {
        Map<String, CommandImpl> commands = projectCommands.get(project);

        if (commands == null) {
            commands = new HashMap<>();
        }

        command.setName(getUniqueCommandName(command.getType(), command.getName()));

        if (commands.get(command.getName()) != null) {
            // shouldn't occur normally
            return Promises.reject(JsPromiseError.create("Command with name " + command.getName() +
                                                         " is already associated to the project " + project.getName()));
        }

        commands.put(command.getName(), command);

        return updateProject(project, new ArrayList<>(commands.values())).then(new Function<Void, CommandImpl>() {
            @Override
            public CommandImpl apply(Void arg) throws FunctionException {
                return command;
            }
        });
    }

    @Override
    public Promise<CommandImpl> updateProjectCommand(Project project, String name, CommandImpl command) {
        return null;
    }

    @Override
    public Promise<Void> removeProjectCommand(Project project, String name) {
        Map<String, CommandImpl> commands = projectCommands.get(project);

        if (commands == null) {
            return Promises.reject(JsPromiseError.create("Command " + name + " isn't associated with the project " + project.getName()));
        }

        CommandImpl command = commands.remove(name);

        if (command == null) {
            return Promises.reject(JsPromiseError.create("Command " + name + " isn't associated with the project " + project.getName()));
        }

        return updateProject(project, new ArrayList<>(commands.values()));
    }

    private Promise<Void> updateProject(Project project, List<CommandImpl> commands) {
        MutableProjectConfig config = new MutableProjectConfig(project);
        Map<String, List<String>> attributes = config.getAttributes();

        // TODO: attributes may be null

        List<String> commandsList = new ArrayList<>(attributes.size());
        for (CommandImpl command : commands) {
            CommandDto commandDto = dtoFactory.createDto(CommandDto.class)
                                              .withName(command.getName())
                                              .withType(command.getType())
                                              .withCommandLine(command.getCommandLine())
                                              .withAttributes(command.getAttributes());
            commandsList.add(dtoFactory.toJson(commandDto));
        }

        attributes.put("commands", commandsList);

        return project.update().withBody(config).send().then(new Function<Project, Void>() {
            @Override
            public Void apply(Project arg) throws FunctionException {
                return null;
            }
        });
    }

    @Override
    public List<CommandPage> getPages(String type) {
        CommandType commandType = commandTypeRegistry.getCommandTypeById(type);
        return commandType != null ? commandType.getPages() : Collections.<CommandPage>emptyList();
    }

    @Override
    public void executeCommand(final CommandImpl command, final Machine machine) {
        final String outputChannel = "process:output:" + UUID.uuid();

        final CommandOutputConsole console = commandConsoleFactory.create(command, machine);
        console.listenToOutput(outputChannel);
        processesPanelPresenter.addCommandOutput(machine.getId(), console);

        macroProcessor.expandMacros(command.getCommandLine()).then(new Operation<String>() {
            @Override
            public void apply(String arg) throws OperationException {
                final CommandImpl toExecute = new CommandImpl(command);
                toExecute.setCommandLine(arg);

                Promise<MachineProcessDto> processPromise = machineServiceClient.executeCommand(machine.getWorkspaceId(),
                                                                                                machine.getId(),
                                                                                                toExecute,
                                                                                                outputChannel);
                processPromise.then(new Operation<MachineProcessDto>() {
                    @Override
                    public void apply(MachineProcessDto process) throws OperationException {
                        console.attachToProcess(process);
                    }
                });
            }
        });
    }

    @Override
    public void addCommandChangedListener(CommandChangedListener listener) {
        commandChangedListeners.add(listener);
    }

    @Override
    public void removeCommandChangedListener(CommandChangedListener listener) {
        commandChangedListeners.remove(listener);
    }

    private void fireCommandAdded(CommandImpl command) {
        for (CommandChangedListener listener : commandChangedListeners) {
            listener.onCommandAdded(command);
        }
    }

    private void fireCommandRemoved(CommandImpl command) {
        for (CommandChangedListener listener : commandChangedListeners) {
            listener.onCommandRemoved(command);
        }
    }

    private void fireCommandUpdated(CommandImpl command) {
        for (CommandChangedListener listener : commandChangedListeners) {
            listener.onCommandUpdated(command);
        }
    }

    private String getUniqueCommandName(String customType, String customName) {
        final CommandType commandType = commandTypeRegistry.getCommandTypeById(customType);
        final Set<String> commandNames = workspaceCommands.keySet();

        final String newCommandName;

        if (isNullOrEmpty(customName)) {
            newCommandName = "new" + commandType.getDisplayName();
        } else {
            if (!commandNames.contains(customName)) {
                return customName;
            }
            newCommandName = customName + " copy";
        }

        if (!commandNames.contains(newCommandName)) {
            return newCommandName;
        }

        for (int count = 1; count < 1000; count++) {
            if (!commandNames.contains(newCommandName + "-" + count)) {
                return newCommandName + "-" + count;
            }
        }

        return newCommandName;
    }
}
