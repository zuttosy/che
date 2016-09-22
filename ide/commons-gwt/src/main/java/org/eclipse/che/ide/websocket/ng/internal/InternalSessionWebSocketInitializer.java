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
import org.eclipse.che.ide.websocket.ng.impl.SessionWebSocketInitializer;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketConnectionSustainer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

import static org.eclipse.che.ide.websocket.ng.impl.WebSocketConnection.IMMEDIATELY;

/**
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalSessionWebSocketInitializer extends SessionWebSocketInitializer {
    @Inject
    public InternalSessionWebSocketInitializer(@Named("internal") WebSocketConnection connection,
                                               @Named("internal") WebSocketConnectionSustainer sustainer) {
        super(connection, sustainer);
    }
}
