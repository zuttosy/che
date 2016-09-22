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

import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.jsonrpc.impl.JsonRpcObjectValidator;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcTransmitter;
import org.eclipse.che.ide.websocket.ng.WebSocketMessageTransmitter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


/**
 * Transmits JSON RPC objects to {@link WebSocketMessageTransmitter}
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class InternalWebSocketJsonRpcTransmitter extends WebSocketJsonRpcTransmitter {

    @Inject
    public InternalWebSocketJsonRpcTransmitter(@Named("internal") WebSocketMessageTransmitter transmitter,
                                               @Named("internal") JsonRpcObjectValidator validator,
                                               DtoFactory dtoFactory) {
        super(transmitter, dtoFactory, validator);
    }
}
