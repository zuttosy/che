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
public class ExternalWebSocketEndpoint extends BasicWebSocketEndpoint {

    @Inject
    public ExternalWebSocketEndpoint(@Named("external") WebSocketConnectionSustainer sustainer,
                                     @Named("external") PendingMessagesReSender pending,
                                     @Named("external") WebSocketTransmissionDispatcher dispatcher) {
        super(sustainer, pending, dispatcher);
    }
}
