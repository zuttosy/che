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

import com.google.inject.Singleton;

import org.eclipse.che.ide.websocket.ng.impl.PendingMessagesReSender;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Instance is responsible for resending messages that were sent during the period
 * when web socket session was closed.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class ExternalPendingMessagesReSender extends PendingMessagesReSender {
    @Inject
    public ExternalPendingMessagesReSender(@Named("external") WebSocketConnection connection) {
        super(connection);
    }
}
