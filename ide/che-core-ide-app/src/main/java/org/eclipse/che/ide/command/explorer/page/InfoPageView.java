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

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;

/**
 * The view of Info page.
 *
 * @author Artem Zatsarynnyi
 */
@ImplementedBy(InfoPageViewImpl.class)
public interface InfoPageView extends View<InfoPageView.ActionDelegate> {

    /** Returns the command's name value. */
    String getName();

    /** Sets the command's name value. */
    void setName(String name);

    /** The action delegate for this view. */
    interface ActionDelegate {
    }
}
