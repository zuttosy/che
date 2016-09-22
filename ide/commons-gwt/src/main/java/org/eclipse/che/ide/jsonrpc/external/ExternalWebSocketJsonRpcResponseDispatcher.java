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
package org.eclipse.che.ide.jsonrpc.external;

import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.jsonrpc.JsonRpcResponseReceiver;
import org.eclipse.che.ide.jsonrpc.impl.JsonRpcRequestRegistry;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcResponseDispatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Dispatches JSON RPC responses among all registered implementations of {@link JsonRpcResponseReceiver}
 * according to their mappings.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class ExternalWebSocketJsonRpcResponseDispatcher extends WebSocketJsonRpcResponseDispatcher {
    @Inject
    public ExternalWebSocketJsonRpcResponseDispatcher(@Named("external") Map<String, JsonRpcResponseReceiver> receivers,
                                                      @Named("external") JsonRpcRequestRegistry registry,
                                                      @Named("external") DtoFactory dtoFactory) {
        super(receivers, registry, dtoFactory);
    }
}
