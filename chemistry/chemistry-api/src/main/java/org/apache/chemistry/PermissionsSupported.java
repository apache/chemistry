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
 * Which types of permissions are supported by a repository.
 */
public enum PermissionsSupported {

    /**
     * Basic permissions are supported.
     */
    BASIC("basic"),

    /**
     * Repository-specific permissions are supported.
     */
    REPOSITORY("repository"),

    /**
     * Both basic and repository-specific permissions are supported.
     */
    BOTH("both");

    private final String value;

    private PermissionsSupported(String value) {
        this.value = value;
    }

    private static final Map<String, PermissionsSupported> all = new HashMap<String, PermissionsSupported>();
    static {
        for (PermissionsSupported o : values()) {
            all.put(o.value, o);
        }
    }

    public static PermissionsSupported get(String value) {
        PermissionsSupported o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static PermissionsSupported get(String value,
            PermissionsSupported def) {
        PermissionsSupported o = all.get(value);
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
