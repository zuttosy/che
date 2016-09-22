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
package org.eclipse.che.ide.jsonrpc.internal;

import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcInitializer;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.ng.impl.SessionWebSocketInitializer;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

/**
 * @author Dmitry Kuleshov
 */
public class InternalWebSocketJsonRpcInitializer extends WebSocketJsonRpcInitializer {
    @Inject
    public InternalWebSocketJsonRpcInitializer(@Named("internal") WebSocketInitializer webSocketInitializer) {
        super(webSocketInitializer);
    }
}
