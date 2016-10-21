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

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

import java.util.List;

/**
 * View for {@link CommandsExplorerPresenter}.
 *
 * @author Artem Zatsarynnyi
 */
@ImplementedBy(CommandsExplorerViewImpl.class)
public interface CommandsExplorerView extends View<CommandsExplorerView.ActionDelegate> {

    void setCommands(List<CommandImpl> workspaceCommands, List<CommandImpl> projectsCommands);

    interface ActionDelegate extends BaseActionDelegate {

        /**
         * Called when some command has been selected.
         *
         * @param command
         *         selected command
         */
        void onCommandSelected(CommandImpl command);

        /** Called when 'Add' button is clicked. */
        void onAddClicked();

        /** Called when 'Remove' button is clicked. */
        void onRemoveClicked();
    }
}
