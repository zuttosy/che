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

import org.eclipse.che.ide.websocket.ng.impl.SessionWebSocketInitializer;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnectionSustainer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Dmitry Kuleshov
 */
@Singleton
public class ExternalSessionWebSocketInitializer extends SessionWebSocketInitializer {
    @Inject
    public ExternalSessionWebSocketInitializer(@Named("external") WebSocketConnection connection,
                                               @Named("external") WebSocketConnectionSustainer sustainer) {
        super(connection, sustainer);
    }
}
