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

import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.ng.impl.BasicWebSocketEndpoint;
import org.eclipse.che.ide.websocket.ng.impl.PendingMessagesReSender;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnectionSustainer;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketTransmissionDispatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Duplex WEB SOCKET endpoint, handles messages, errors, session open/close events.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketEndpoint extends BasicWebSocketEndpoint {

    @Inject
    public InternalWebSocketEndpoint(@Named("internal") WebSocketConnectionSustainer sustainer,
                                     @Named("internal") PendingMessagesReSender pending,
                                     @Named("internal") WebSocketTransmissionDispatcher dispatcher) {
        super(sustainer, pending, dispatcher);
    }
}
