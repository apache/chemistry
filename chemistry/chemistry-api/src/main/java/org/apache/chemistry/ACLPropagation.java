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
 * Specifies how non-direct ACEs can be handled by the repository.
 *
 * @see SPI#applyACL
 */
public enum ACLPropagation {

    /**
     * The repository has its own mechanism of computing how changing an ACL for
     * an object influences the non-direct ACEs of other objects.
     */
    REPOSITORY_DETERMINED("repository-determined"),

    /**
     * The repository is able to apply ACEs to a document or folder without
     * changing the ACLs of other objects. This means that the repository is
     * able to "break" the dependency for non-direct ACEs when requested by the
     * client.
     */
    OBJECT_ONLY("object-only"),

    /**
     * The ACEs are applied to the given object and all "inheriting" objects,
     * with the intended side effect that all objects which somehow share the
     * provided security constraints should be changed accordingly.
     */
    PROPAGATE("propagate");

    private final String value;

    private ACLPropagation(String value) {
        this.value = value;
    }

    private static final Map<String, ACLPropagation> all = new HashMap<String, ACLPropagation>();
    static {
        for (ACLPropagation o : values()) {
            all.put(o.value, o);
        }
    }

    public static ACLPropagation get(String value) {
        ACLPropagation o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static ACLPropagation get(String value, ACLPropagation def) {
        ACLPropagation o = all.get(value);
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
