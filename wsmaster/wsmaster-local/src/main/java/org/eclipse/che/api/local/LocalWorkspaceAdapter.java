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
package org.eclipse.che.api.local;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.workspace.server.WorkspaceConfigJsonAdapter;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;

import java.lang.reflect.Type;

/**
 * @author Yevhenii Voevodin
 */
public class LocalWorkspaceAdapter implements JsonDeserializer<WorkspaceImpl> {

    private final WorkspaceConfigJsonAdapter adapter;

    public LocalWorkspaceAdapter(WorkspaceConfigJsonAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public WorkspaceImpl deserialize(JsonElement jsonEl, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        if (jsonEl.isJsonObject()) {
            final JsonObject root = jsonEl.getAsJsonObject();
            if (root.has("config") && root.get("config").isJsonObject()) {
                try {
                    adapter.adapt(root.getAsJsonObject("config"));
                } catch (ServerException x) {
                    throw new RuntimeException(x.getMessage(), x);
                }
            }
         }
        return ctx.deserialize(jsonEl, WorkspaceImpl.class);
    }
}
