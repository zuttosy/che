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
package org.eclipse.che.plugin.nodejsdbg.server.parser;

/**
 * Wrapper for process output.
 *
 * @author Anatoliy Bazko
 */
public class DbgOutput {
    private final String  output;
    private final boolean terminated;

    private DbgOutput(String output, boolean terminated) {
        this.output = output;
        this.terminated = terminated;
    }

    public static DbgOutput of(String output) {
        return new DbgOutput(output, false);
    }

    public static DbgOutput of(String output, boolean terminated) {
        return new DbgOutput(output, terminated);
    }


    public String getOutput() {
        return output;
    }

    /**
     * Indicates that no more output then.
     */
    public boolean isTerminated() {
        return terminated;
    }
}
