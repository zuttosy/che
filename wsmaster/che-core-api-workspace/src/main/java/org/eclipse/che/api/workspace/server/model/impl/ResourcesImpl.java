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
import org.eclipse.che.api.core.model.workspace.Resources;

import java.util.Objects;

/**
 * @author Alexander Garagatyi
 */
public class ResourcesImpl implements Resources {
    private LimitsImpl limits;

    public ResourcesImpl() {}

    public ResourcesImpl(LimitsImpl limits) {
        this.limits = limits;
    }

    public ResourcesImpl(Resources resources) {
        this.limits = new LimitsImpl(resources.getLimits());
    }

    @Override
    public Limits getLimits() {
        return limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourcesImpl)) return false;
        ResourcesImpl resources = (ResourcesImpl)o;
        return Objects.equals(limits, resources.limits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limits);
    }

    @Override
    public String toString() {
        return "ResourcesImpl{" +
               "limits=" + limits +
               '}';
    }
}
