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
package org.eclipse.che.ide.jsonrpc.external;

import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcInitializer;
import org.eclipse.che.ide.websocket.ng.impl.SessionWebSocketInitializer;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketInitializer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Dmitry Kuleshov
 */
public class ExternalWebSocketJsonRpcInitializer extends WebSocketJsonRpcInitializer {
    @Inject
    public ExternalWebSocketJsonRpcInitializer(@Named("external") WebSocketInitializer webSocketInitializer) {
        super(webSocketInitializer);
    }
}
