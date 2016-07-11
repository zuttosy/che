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
package org.eclipse.che.ide.extension.machine.client.outputspanel.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

/**
 * Console widget containing thousands lines of text,
 *
 * @author Vitaliy Guliy
 */
public class Console extends Composite implements RequiresResize {

    interface ConsoleUiBinder extends UiBinder<Widget, Console> {
    }

    private static final ConsoleUiBinder UI_BINDER   = GWT.create(ConsoleUiBinder.class);

    /* Number of currently visible lines */
    private int visibleLines = 0;

    /* Array of output lines */
    private ArrayList<String> output = new ArrayList<>();

    @UiField
    FlowPanel rowsPanel;

    private ArrayList<FlowPanel> rows = new ArrayList<>();


    public Console() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        render();
    }

    public void print(String text) {
        print(text, false);
    }

    public void print(String text, boolean returnCarrier) {
        if (returnCarrier) {
            if (output.isEmpty()) {
                output.add(text);
            } else {
                output.set(output.size() - 1, text);
            }
        } else {
            output.add(text);
        }

        fillLines();
    }

    private void render() {
        renterTimer.cancel();
        renterTimer.schedule(200);
    }

    private Timer renterTimer = new Timer() {
        @Override
        public void run() {
            doRender();
        }
    };

    private void doRender() {
        // Ignore resize if widget is not visible, height is zero.
        if (getElement().getOffsetHeight() == 0) {
            return;
        }

        // Render lines for the output
        updateLines();

        // Fill the lines
        fillLines();
    }

    private void updateLines() {
        int width = getElement().getOffsetWidth();
        int height = getElement().getOffsetHeight();

        int visibleLines = height / 13;
        if (this.visibleLines == visibleLines) {
            return;
        }

        this.visibleLines = visibleLines;

        rowsPanel.clear();
        rows.clear();

        for (int i = 0; i < visibleLines + 2; i++) {
            FlowPanel row = new FlowPanel();
            rowsPanel.add(row);
            rows.add(row);
        }
    }

    private void fillLines() {
        if (output.size() > visibleLines) {
            int startIndex = output.size() - visibleLines;

            for (int i = 0; i < visibleLines; i++) {
                String text = output.get(startIndex + i);
                rows.get(i).getElement().setInnerText(text);
            }

        } else {
            for (int i = 0; i < output.size(); i++) {
                String text = output.get(i);
                rows.get(i).getElement().setInnerText(text);
            }
        }
    }

}
