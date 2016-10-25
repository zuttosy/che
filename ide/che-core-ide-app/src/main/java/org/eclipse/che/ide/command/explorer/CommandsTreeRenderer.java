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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.TreeStyles;
import org.eclipse.che.ide.ui.smartTree.presentation.DefaultPresentationRenderer;

/**
 * //
 *
 * @author Artem Zatsarynnyi
 */
class CommandsTreeRenderer extends DefaultPresentationRenderer<Node> {

    private final Resources coreResources;

    CommandsTreeRenderer(TreeStyles treeStyles, Resources coreResources) {
        super(treeStyles);
        this.coreResources = coreResources;
    }

    @Override
    public Element render(Node node, String domID, Tree.Joint joint, int depth) {
        Element element = super.render(node, domID, joint, depth);

        final SpanElement removeCommandButton = Document.get().createSpanElement();
        removeCommandButton.appendChild(coreResources.removeCommandButton().getSvg().getElement());
        final SpanElement buttonsPanel = Document.get().createSpanElement();
        buttonsPanel.appendChild(removeCommandButton);


        element.getFirstChildElement().appendChild(buttonsPanel);
        return element;
    }
}
