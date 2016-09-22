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
package org.eclipse.che.ide.websocket.ng.external;

import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnectionSustainer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Responsible for keeping connection alive and reconnecting if needed.
 * If connection is closed and sustainer is active it tries to reconnect
 * according to its properties:
 *
 * <ul>
 * <li>reconnection delay - 500 milliseconds</li>
 * <li>reconnection limit - 5 attempts</li>
 * </ul>
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class ExternalWebSocketConnectionSustainer extends WebSocketConnectionSustainer {

    @Inject
    public ExternalWebSocketConnectionSustainer(@Named("internal") WebSocketConnection connection) {
        super(connection);
    }
}
