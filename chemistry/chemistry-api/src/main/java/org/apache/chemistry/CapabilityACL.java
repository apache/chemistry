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
 * The level of support for ACLs by the repository.
 *
 * @see RepositoryCapabilities#getACLCapability
 */
public enum CapabilityACL {

    /**
     * The repository does not support ACL services.
     */
    NONE("none"),

    /**
     * The repository supports discovery of ACLs ({@link SPI#getACL}).
     *
     * @see SPI#getACL
     */
    DISCOVER("discover"),

    /**
     * The repository supports discovery of ACLs and applying ACLs (
     * {@link SPI#getACL} and {@link SPI#applyACL}).
     *
     * @see SPI#getACL
     * @see SPI#applyACL
     */
    MANAGE("manage");

    private final String value;

    private CapabilityACL(String value) {
        this.value = value;
    }

    private static final Map<String, CapabilityACL> all = new HashMap<String, CapabilityACL>();
    static {
        for (CapabilityACL o : values()) {
            all.put(o.value, o);
        }
    }

    public static CapabilityACL get(String value) {
        CapabilityACL o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static CapabilityACL get(String value, CapabilityACL def) {
        CapabilityACL o = all.get(value);
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
