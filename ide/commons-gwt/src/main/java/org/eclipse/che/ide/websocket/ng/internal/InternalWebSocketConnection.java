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
package org.eclipse.che.ide.websocket.ng.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;

/**
 * Entry point for high level WEB SOCKET connection operations.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketConnection extends WebSocketConnection {

    @Inject
    public InternalWebSocketConnection(InternalWebSocketCreator webSocketCreator) {
        super(webSocketCreator);
    }
}
