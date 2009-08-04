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
 * The level of changes (if any) that the repository exposes via
 * {@link SPI#getChangeLog}.
 *
 * @see SPI#getChangeLog
 */
public enum CapabilityChange {

    /**
     * The repository does not expose any information in the change log.
     */
    NONE("none"),

    /**
     * The change log can return only the object IDs for changed objects in the
     * repository and an indication of the type of change, not details of the
     * actual change.
     */
    OBJECT_IDS_ONLY("objectidsonly"),

    /**
     * The change log can return the object IDs for changed objects in the
     * repository and the details of the actual properties changed.
     */
    ALL("all");

    private final String value;

    private CapabilityChange(String value) {
        this.value = value;
    }

    private static final Map<String, CapabilityChange> all = new HashMap<String, CapabilityChange>();
    static {
        for (CapabilityChange o : values()) {
            all.put(o.value, o);
        }
    }

    public static CapabilityChange get(String value) {
        CapabilityChange o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static CapabilityChange get(String value, CapabilityChange def) {
        CapabilityChange o = all.get(value);
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
