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
import org.eclipse.che.ide.jsonrpc.JsonRpcRequestReceiver;
import org.eclipse.che.ide.jsonrpc.impl.WebSocketJsonRpcRequestDispatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Dispatches JSON RPC requests among all registered implementations of {@link JsonRpcRequestReceiver}
 * according to their mappings.
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class ExternalWebSocketJsonRpcRequestDispatcher extends WebSocketJsonRpcRequestDispatcher {
    @Inject
    public ExternalWebSocketJsonRpcRequestDispatcher(@Named("external") Map<String, JsonRpcRequestReceiver> receivers,
                                                     DtoFactory dtoFactory) {
        super(receivers, dtoFactory);
    }
}
