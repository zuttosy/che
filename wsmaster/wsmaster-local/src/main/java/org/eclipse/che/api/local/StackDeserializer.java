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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.eclipse.che.api.workspace.server.WorkspaceConfigJsonAdapter;
import org.eclipse.che.api.workspace.server.model.impl.stack.StackImpl;

import java.lang.reflect.Type;

import static org.eclipse.che.api.workspace.server.WorkspaceConfigJsonAdapter.findDevMachine;

/**
 * @author Yevhenii Voevodin
 */
public class StackDeserializer implements JsonDeserializer<StackImpl> {

    private final WorkspaceConfigJsonAdapter configJsonAdapter;

    public StackDeserializer(WorkspaceConfigJsonAdapter configJsonAdapter) {
        this.configJsonAdapter = configJsonAdapter;
    }

    @Override
    public StackImpl deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
        if (json.isJsonObject()) {
            final JsonObject stack = json.getAsJsonObject();
            if (stack.has("source") && stack.get("source").isJsonObject()) {
                final JsonObject source = stack.getAsJsonObject("source");
                final JsonObject newWsSource = new JsonObject();
                if (source.has("type") && !source.get("type").isJsonNull()) {
                    final String type = source.get("type").getAsString();
                    switch (type) {
                        case "image":
                            newWsSource.addProperty("type", "image");
                            if (source.has("origin")) {
                                newWsSource.addProperty("location", source.get("origin").getAsString());
                            }
                            break;
                        case "location":
                            newWsSource.addProperty("type", "dockerfile");
                            if (source.has("origin")) {
                                newWsSource.addProperty("location", source.get("origin").getAsString());
                            }
                            break;
                        case "recipe":
                            newWsSource.addProperty("type", "dockerfile");
                            if (source.has("origin")) {
                                newWsSource.addProperty("content", source.get("origin").getAsString());
                            }
                            break;
                    }

                    // fix workspace configuration dev machine's source
                    final JsonElement wsConfEl = stack.get("workspaceConfig");
                    if (wsConfEl != null && wsConfEl.isJsonObject()) {
                        final JsonObject wsConfig = wsConfEl.getAsJsonObject();
                        final JsonObject defaultEnv = findDefaultEnv(wsConfig);
                        if (defaultEnv != null) {
                            final JsonObject devMachine = findDevMachine(defaultEnv);
                            if (devMachine != null) {
                                devMachine.add("source", newWsSource);
                            }

                            // convert workspace config
                            new WorkspaceConfigJsonAdapter().adaptModifying(wsConfig);
                        }
                    }
                }
            }
        }
        return new Gson().fromJson(json, StackImpl.class);
    }

    private static JsonObject findDefaultEnv(JsonObject wsConfig) {
        final JsonElement defaultEnvNameEl = wsConfig.get("defaultEnv");
        if (defaultEnvNameEl != null && wsConfig.has("environments") && wsConfig.get("environments").isJsonArray()) {
            final String defaultEnvName = defaultEnvNameEl.getAsString();
            for (JsonElement envEl : wsConfig.getAsJsonArray("environments")) {
                if (envEl.isJsonObject()) {
                    final JsonObject envObj = envEl.getAsJsonObject();
                    if (envObj.has("name") && envObj.get("name").getAsString().equals(defaultEnvName)) {
                        return envEl.getAsJsonObject();
                    }
                }
            }
        }
        return null;
    }
}
