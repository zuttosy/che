/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.nodejsdbg.server;

import org.eclipse.che.api.debugger.server.Debugger;
import org.eclipse.che.api.debugger.server.DebuggerFactory;
import org.eclipse.che.api.debugger.server.exceptions.DebuggerException;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Anatoliy Bazko
 */
public class NodejsDebuggerFactory implements DebuggerFactory {
    private static final String TYPE = "nodejs";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Debugger create(Map<String, String> properties, Debugger.DebuggerCallback debuggerCallback) throws DebuggerException {
        Map<String, String> normalizedProps = properties.entrySet()
                                                        .stream()
                                                        .collect(toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue));

        String source = normalizedProps.get("source");
        if (source == null) {
            throw new DebuggerException("Unknown source to debug. Debugger can't be started");
        }

        return NodeJsDebugger.newInstance(source, debuggerCallback);
    }
}
