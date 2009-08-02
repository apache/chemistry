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

import java.util.HashMap;
import java.util.Map;

/**
 * The type of change represented by a {@link ChangeEvent}.
 */
public enum ChangeType {

    /**
     * The object was created.
     */
    CREATED("created"),

    /**
     * The object was updated.
     */
    UPDATED("updated"),

    /**
     * The object was deleted.
     */
    DELETED("deleted"),

    /**
     * The access control or security policy for the object were changed.
     */
    SECURITY("security");

    private final String value;

    private ChangeType(String value) {
        this.value = value;
    }

    private static final Map<String, ChangeType> all = new HashMap<String, ChangeType>();
    static {
        for (ChangeType o : values()) {
            all.put(o.value, o);
        }
    }

    public static ChangeType get(String value) {
        ChangeType o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static ChangeType get(String value, ChangeType def) {
        ChangeType o = all.get(value);
        if (o == null) {
            o = def;
        }
        return o;
    }

    @Override
    public String toString() {
        return value;
    }
}
