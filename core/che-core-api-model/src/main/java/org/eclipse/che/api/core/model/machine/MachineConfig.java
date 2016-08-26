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
package org.eclipse.che.api.core.model.machine;

import org.eclipse.che.commons.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author gazarenkov
 */
@Deprecated
// TODO do not use this class
public interface MachineConfig {

    /**
     * Display name.
     */
    // TODO move to machine
    String getName();

    /**
     * From where to create this Machine (Recipe/Snapshot).
     */
    // TODO remove
    MachineSource getSource();

    /**
     * Is workspace bound to machine or not.
     */
    // TODO remove
    boolean isDev();

    /**
     * Machine type (i.e. "docker").
     */
    // TODO remove
    String getType();

    /**
     * Machine limits such as RAM size.
     */
    // TODO move to machine
    @Nullable
    Limits getLimits();

    /**
     * Get configuration of servers inside of machine.
     *
     * <p>Key is port/transport protocol, e.g. 8080/tcp or 100100/udp
     */
    // TODO remove
    List<? extends ServerConf> getServers();

    /**
     * Get predefined environment variables of machine.
     */
    // TODO remove
    Map<String, String> getEnvVariables();
}
