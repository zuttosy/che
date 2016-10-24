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
package org.eclipse.che.ide.command.explorer;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandTypeRegistry;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.workspace.event.WorkspaceStartedEvent;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.api.parts.PartStackType.NAVIGATION;

/**
 * Presenter for managing commands.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class CommandsExplorerPresenter extends BasePresenter implements CommandsExplorerView.ActionDelegate,
                                                                        WorkspaceStartedEvent.Handler {

    private final CommandsExplorerView view;
    private final WorkspaceAgent       workspaceAgent;
    private final CommandManager       commandManager;
    private final AppContext           appContext;
    private final CommandTypeRegistry  commandTypeRegistry;

    @Inject
    public CommandsExplorerPresenter(CommandsExplorerView view,
                                     WorkspaceAgent workspaceAgent,
                                     EventBus eventBus,
                                     CommandManager commandManager,
                                     AppContext appContext,
                                     CommandTypeRegistry commandTypeRegistry) {
        this.view = view;
        this.workspaceAgent = workspaceAgent;
        this.commandManager = commandManager;
        this.appContext = appContext;
        this.commandTypeRegistry = commandTypeRegistry;

        view.setDelegate(this);

        eventBus.addHandler(WorkspaceStartedEvent.TYPE, this);
    }

    public void open() {
        workspaceAgent.openPart(this, NAVIGATION);

        Map<Project, Map<CommandType, List<CommandImpl>>> projectsCommands = new HashMap<>();

        for (Project project : appContext.getProjects()) {
            Map<CommandType, List<CommandImpl>> projectCommands = projectsCommands.get(project);
            if (projectCommands == null) {
                projectCommands = new HashMap<>();
                projectsCommands.put(project, projectCommands);
            }

            for (CommandImpl command : commandManager.getProjectCommands(project)) {
                final CommandType commandType = commandTypeRegistry.getCommandTypeById(command.getType());

                List<CommandImpl> commands = projectCommands.get(commandType);
                if (commands == null) {
                    commands = new ArrayList<>();
                    projectCommands.put(commandType, commands);
                }

                commands.add(command);
            }
        }

        view.setCommands(commandManager.getWorkspaceCommands(), projectsCommands);
    }

    @Override
    public int getSize() {
        return 1000;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(getView());
    }

    @Override
    public String getTitle() {
        return "Commands";
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Manage commands";
    }

    @Nullable
    @Override
    public SVGResource getTitleImage() {
        return null;
    }

    @Override
    public void onWorkspaceStarted(WorkspaceStartedEvent event) {
        workspaceAgent.openPart(this, NAVIGATION);
    }

    @Override
    public void onCommandSelected(CommandImpl command) {
    }

    @Override
    public void onAddClicked() {
    }

    @Override
    public void onRemoveClicked() {
    }
}
