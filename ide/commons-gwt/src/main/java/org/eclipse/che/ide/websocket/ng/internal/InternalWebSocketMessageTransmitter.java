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

import org.eclipse.che.api.core.websocket.shared.WebSocketTransmission;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.ng.WebSocketMessageTransmitter;
import org.eclipse.che.ide.websocket.ng.impl.BasicWebSocketMessageTransmitter;
import org.eclipse.che.ide.websocket.ng.impl.PendingMessagesReSender;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketTransmissionValidator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Transmits messages over WEB SOCKET to a specific endpoint or broadcasts them.
 * If WEB SOCKET session is not opened adds messages to re-sender to try to send
 * them when session will be opened again.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketMessageTransmitter extends BasicWebSocketMessageTransmitter {
    @Inject
    public InternalWebSocketMessageTransmitter(@Named("internal") WebSocketConnection connection,
                                               @Named("internal") PendingMessagesReSender reSender,
                                               @Named("internal") WebSocketTransmissionValidator validator,
                                               DtoFactory dtoFactory) {
        super(connection, reSender, validator, dtoFactory);
    }
}
