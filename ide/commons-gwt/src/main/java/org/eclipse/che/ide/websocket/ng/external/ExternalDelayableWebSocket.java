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

import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.websocket.ng.impl.DelayableWebSocket;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketEndpoint;
import org.eclipse.che.ide.websocket.ng.impl.WebSocketJsoWrapper;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Yet another {@link WebSocketJsoWrapper} wrapper to benefit from
 * dependency injection provided by Gin. This implementation allows
 * setting a delay for opening a connection. It is convenient when
 * you are reconnecting.
 *
 * @author Dmitry Kuleshov
 */
public class ExternalDelayableWebSocket extends DelayableWebSocket {

    @Inject
    public ExternalDelayableWebSocket(@Assisted String url, @Assisted Integer delay, @Named("external") WebSocketEndpoint endpoint) {
        super(url, delay, endpoint);
    }
}
