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

import org.eclipse.che.api.core.jsonrpc.shared.JsonRpcObject;
import org.eclipse.che.api.core.websocket.shared.WebSocketTransmission;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.jsonrpc.impl.JsonRpcDispatcher;
import org.eclipse.che.ide.jsonrpc.impl.JsonRpcObjectValidator;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcDispatcher;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.ng.WebSocketMessageReceiver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Receives raw JSON RPC objects ({@link JsonRpcObject}) extracted from WEB SOCKET
 * transmissions ({@link WebSocketTransmission}) and dispatches them among more specific
 * dispatchers {@link JsonRpcDispatcher}) according to their type (e.g. JSON RPC
 * request/response dispatchers).
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketJsonRpcDispatcher extends WebSocketJsonRpcDispatcher {
    @Inject
    public InternalWebSocketJsonRpcDispatcher(@Named("internal") Map<String, JsonRpcDispatcher> dispatchers,
                                              @Named("internal") JsonRpcObjectValidator validator,
                                              DtoFactory dtoFactory) {
        super(dispatchers, validator, dtoFactory);
    }
}
