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

import static org.eclipse.che.api.workspace.shared.Constants.COMMAND_PREVIEW_URL_ATTRIBUTE_NAME;

/**
 * //
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class PreviewUrlPage implements CommandsExplorerPage, PreviewUrlPageView.ActionDelegate {

    private final PreviewUrlPageView view;

    private CommandImpl editedCommand;

    // initial value of the command's preview URL
    private String previewUrlInitial;

    @Inject
    public PreviewUrlPage(PreviewUrlPageView view) {
        this.view = view;

        view.setDelegate(this);
    }

    @Override
    public String getTitle() {
        return "Preview URL";
    }

    @Override
    public String getTooltip() {
        return "Command preview URL";
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Override
    public void resetFrom(CommandImpl command) {
        editedCommand = command;

        final String previewUrl = command.getAttributes().get(COMMAND_PREVIEW_URL_ATTRIBUTE_NAME);
        previewUrlInitial = previewUrl != null ? previewUrl : "";

        view.setUrl(previewUrlInitial);
    }
}
