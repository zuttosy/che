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


import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 'ptype' command parser.
 *
 * @author Anatoliy Bazko
 */
public class GdbPType {

    private static final Pattern GDB_ARGS = Pattern.compile("type = (.*)");

    private final String type;

    public GdbPType(String type) {this.type = type;}

    public String getType() {
        return type;
    }

    /**
     * Factory method.
     */
    public static GdbPType parse(DbgOutput dbgOutput) throws NodeJsDebuggerParseException {
        String output = dbgOutput.getOutput();

        Matcher matcher = GDB_ARGS.matcher(output);
        if (matcher.find()) {
            String type = matcher.group(1);
            return new GdbPType(type);
        }

        throw new NodeJsDebuggerParseException(GdbPrint.class, output);
    }
}
