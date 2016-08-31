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

import java.lang.reflect.Type;

/**
 * @author Yevhenii Voevodin
 */
public class WorkspaceConfigDeserializer<T> implements JsonDeserializer<T> {

    private final Class<T>                   clazz;
    private final String                     fieldName;
    private final WorkspaceConfigJsonAdapter adapter;

    public WorkspaceConfigDeserializer(Class<T> clazz, String fieldName, WorkspaceConfigJsonAdapter adapter) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.adapter = adapter;
    }

    @Override
    public T deserialize(JsonElement jsonEl, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        if (jsonEl.isJsonObject()) {
            final JsonObject root = jsonEl.getAsJsonObject();
            if (root.has(fieldName) && root.get(fieldName).isJsonObject()) {
                try {
                    adapter.adapt(root.getAsJsonObject(fieldName));
                } catch (ServerException x) {
                    throw new RuntimeException(x.getMessage(), x);
                }
            }
        }
        return ctx.deserialize(jsonEl, clazz);
    }
}
