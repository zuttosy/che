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
package org.eclipse.che.ide.command.explorer.page;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.command.CommandImpl;

/**
 * //
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class ArgumentsPage implements CommandsExplorerPage, ArgumentsPageView.ActionDelegate {

    private final ArgumentsPageView view;

    private CommandImpl editedCommand;

    // initial value of the command's name
    private String commandLineInitial;

    @Inject
    public ArgumentsPage(ArgumentsPageView view) {
        this.view = view;

        view.setDelegate(this);
    }

    @Override
    public String getTitle() {
        return "Arguments";
    }

    @Override
    public String getTooltip() {
        return "Command arguments";
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Override
    public void resetFrom(CommandImpl command) {
        editedCommand = command;
        commandLineInitial = command.getCommandLine();

        view.setCommandLine(command.getCommandLine());
    }
}
