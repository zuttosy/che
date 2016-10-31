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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

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

    private final CommandsExplorerResources           resources;
    private final CommandsExplorerView.ActionDelegate delegate;

    CommandsTreeRenderer(TreeStyles treeStyles, CommandsExplorerResources resources, CommandsExplorerView.ActionDelegate delegate) {
        super(treeStyles);

        this.resources = resources;
        this.delegate = delegate;
    }

    @Override
    public Element render(Node node, String domID, Tree.Joint joint, int depth) {
        Element element = super.render(node, domID, joint, depth);

        if (node instanceof CommandNode) {
            element.addClassName(resources.styles().categorySubElementHeader());

            final SpanElement removeCommandButton = Document.get().createSpanElement();
            removeCommandButton.appendChild(resources.removeCommandButton().getSvg().getElement());
            Event.sinkEvents(removeCommandButton, Event.ONCLICK);
            Event.setEventListener(removeCommandButton, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    if (Event.ONCLICK == event.getTypeInt()) {
                        event.stopPropagation();
                        delegate.onRemoveClicked();
                    }
                }
            });


            final SpanElement duplicateCommandButton = Document.get().createSpanElement();
            duplicateCommandButton.appendChild(resources.duplicateCommandButton().getSvg().getElement());
            Event.sinkEvents(duplicateCommandButton, Event.ONCLICK);
            Event.setEventListener(duplicateCommandButton, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    if (Event.ONCLICK == event.getTypeInt()) {
                        event.stopPropagation();
//                        delegate.onDuplicateClicked();
                    }
                }
            });


            final SpanElement buttonsPanel = Document.get().createSpanElement();
            buttonsPanel.setClassName(resources.styles().buttonArea());

            buttonsPanel.appendChild(removeCommandButton);
            buttonsPanel.appendChild(duplicateCommandButton);

            element.getFirstChildElement().appendChild(buttonsPanel);
        } else if (node instanceof CommandTypeNode) {
            element.getFirstChildElement().addClassName(resources.styles().categoryHeader());

            final SpanElement buttonElement = Document.get().createSpanElement();
            buttonElement.appendChild(resources.addCommandButton().getSvg().getElement());
            Event.sinkEvents(buttonElement, Event.ONCLICK);
            Event.setEventListener(buttonElement, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    if (Event.ONCLICK == event.getTypeInt()) {
                        event.stopPropagation();
//                        namePanel.setVisible(true);
//                        previewUrlPanel.setVisible(true);
//                        selectedType = commandTypeId;
                        delegate.onAddClicked();
//                        resetFilter();
                    }
                }
            });

            element.getFirstChildElement().appendChild(buttonElement);
        }

        return element;
    }
}
