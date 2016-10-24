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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandTypeRegistry;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ui.radiobuttongroup.RadioButtonGroup;
import org.eclipse.che.ide.ui.smartTree.NodeLoader;
import org.eclipse.che.ide.ui.smartTree.NodeStorage;
import org.eclipse.che.ide.ui.smartTree.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link CommandsExplorerView}.
 *
 * @author Artem Zatsarynnyi
 */
public class CommandsExplorerViewImpl extends BaseView<CommandsExplorerView.ActionDelegate> implements CommandsExplorerView {

    private static final CommandsExplorerViewImplUiBinder UI_BINDER = GWT.create(CommandsExplorerViewImplUiBinder.class);

    private final CommandTypeRegistry commandTypeRegistry;

    @UiField(provided = true)
    Tree workspaceCommandsTree;

    @UiField(provided = true)
    Tree projectCommandsTree;

    @UiField
    RadioButtonGroup pagesSwitcher;

    @UiField
    DeckPanel pagesPanel;

    private ActionDelegate delegate;

    @Inject
    public CommandsExplorerViewImpl(org.eclipse.che.ide.Resources coreResources,
                                    CommandTypeRegistry commandTypeRegistry) {
        super(coreResources);

        this.commandTypeRegistry = commandTypeRegistry;

        setTitle("Commands Explorer");

        workspaceCommandsTree = new Tree(new NodeStorage(), new NodeLoader());
        projectCommandsTree = new Tree(new NodeStorage(), new NodeLoader());

        setContentWidget(UI_BINDER.createAndBindUi(this));

        composePagesSwitcher();
        pagesSwitcher.selectButton(0);
        pagesPanel.showWidget(0);
    }

    private void composePagesSwitcher() {
        pagesSwitcher.addButton("Info", "Base command info", null, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pagesPanel.showWidget(0);
            }
        });
        pagesSwitcher.addButton("Arguments", "Command arguments", null, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pagesPanel.showWidget(1);
            }
        });
        pagesSwitcher.addButton("Preview URL", "Command preview URL", null, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pagesPanel.showWidget(2);
            }
        });
    }

    @Override
    public void setCommands(List<CommandImpl> workspaceCommands, Map<Project, CommandImpl> projectsCommands) {
        renderWorkspaceCommands(workspaceCommands);
        renderProjectCommands(projectsCommands);
    }

    private void renderWorkspaceCommands(List<CommandImpl> workspaceCommands) {
        workspaceCommandsTree.getNodeStorage().clear();

        // group commands by type
        Map<CommandType, List<CommandImpl>> commandsByType = new HashMap<>();
        for (CommandImpl command : workspaceCommands) {
            final CommandType commandType = commandTypeRegistry.getCommandTypeById(command.getType());

            List<CommandImpl> commands = commandsByType.get(commandType);
            if (commands == null) {
                commands = new ArrayList<>();
                commandsByType.put(commandType, commands);
            }
            commands.add(command);
        }

        // render tree
        for (Map.Entry<CommandType, List<CommandImpl>> entry : commandsByType.entrySet()) {
            List<CommandNode> commandNodes = new ArrayList<>(entry.getValue().size());
            for (CommandImpl command : entry.getValue()) {
                commandNodes.add(new CommandNode(command.getName()));
            }

            CommandTypeNode commandTypeNode = new CommandTypeNode(entry.getKey().getDisplayName(), commandNodes);
            workspaceCommandsTree.getNodeStorage().add(commandTypeNode);
        }
    }

    private void renderProjectCommands(Map<Project, CommandImpl> projectsCommands) {
    }

    interface CommandsExplorerViewImplUiBinder extends UiBinder<Widget, CommandsExplorerViewImpl> {
    }
}
