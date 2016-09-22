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

import org.eclipse.che.api.core.jsonrpc.shared.JsonRpcRequest;
import org.eclipse.che.ide.jsonrpc.JsonRpcRequestTransmitter;
import org.eclipse.che.ide.jsonrpc.impl.JsonRpcRequestRegistry;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcRequestTransmitter;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcTransmitter;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


/**
 * Transmits JSON RPC requests through to {@link WebSocketJsonRpcTransmitter}
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketJsonRpcRequestTransmitter extends WebSocketJsonRpcRequestTransmitter {

    @Inject
    public InternalWebSocketJsonRpcRequestTransmitter(@Named("internal") WebSocketJsonRpcTransmitter transmitter,
                                                      JsonRpcRequestRegistry requestRegistry) {
        super(transmitter, requestRegistry);
    }
}
