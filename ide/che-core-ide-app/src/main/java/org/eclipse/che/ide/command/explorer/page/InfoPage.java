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
public class InfoPage implements CommandsExplorerPage, InfoPageView.ActionDelegate {

    private final InfoPageView view;

    private CommandImpl editedCommand;

    // initial value of the command's name
    private String commandNameInitial;

    @Inject
    public InfoPage(InfoPageView view) {
        this.view = view;

        view.setDelegate(this);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public String getTooltip() {
        return "Base command info";
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Override
    public void resetFrom(CommandImpl command) {
        editedCommand = command;
        commandNameInitial = command.getName();

        view.setName(command.getName());
    }
}
