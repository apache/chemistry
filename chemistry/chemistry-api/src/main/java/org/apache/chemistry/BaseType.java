/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CMIS base types.
 */
public enum BaseType {

    /**
     * A document represents a standalone information asset.
     */
    DOCUMENT("cmis:document"),

    /**
     * A folder represents a logical container for a collection of fileable
     * objects, which include folders and documents. Folders are used to
     * organize fileable objects.
     */
    FOLDER("cmis:folder"),

    /**
     * A relationship represents a directional relationship between two
     * independent objects.
     */
    RELATIONSHIP("cmis:relationship"),

    /**
     * A policy represents an administrative policy, which may be applied to one
     * or more controllable objects
     */
    POLICY("cmis:policy");

    private final String value;

    private BaseType(String value) {
        this.value = value;
    }

    private static final Map<String, BaseType> all = new HashMap<String, BaseType>();

    public static final List<BaseType> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    public static final List<String> ALL_IDS;

    static {
        List<String> ids = new ArrayList<String>(4);
        for (BaseType o : values()) {
            String id = o.getId();
            all.put(id, o);
            ids.add(id);
        }
        ALL_IDS = Collections.unmodifiableList(ids);
    }

    public static BaseType get(String value) {
        BaseType o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public String getId() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
