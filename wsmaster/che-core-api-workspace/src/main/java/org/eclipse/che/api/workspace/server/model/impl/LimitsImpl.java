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
package org.eclipse.che.api.workspace.server.model.impl;

import org.eclipse.che.api.core.model.workspace.Limits;
import org.eclipse.che.commons.annotation.Nullable;

import java.util.Objects;

/**
 * @author Alexander Garagatyi
 */
public class LimitsImpl implements Limits {
    private Long memoryBytes;

    public LimitsImpl() {}

    public LimitsImpl(Long memoryBytes) {
        this.memoryBytes = memoryBytes;
    }

    public LimitsImpl(Limits limits) {
        if (limits != null) {
            this.memoryBytes = limits.getMemoryBytes();
        }
    }

    @Override
    @Nullable
    public Long getMemoryBytes() {
        return memoryBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimitsImpl)) return false;
        LimitsImpl limits = (LimitsImpl)o;
        return Objects.equals(memoryBytes, limits.memoryBytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memoryBytes);
    }

    @Override
    public String toString() {
        return "MachineLimitsImpl{" +
               "memoryBytes=" + memoryBytes +
               '}';
    }
}
