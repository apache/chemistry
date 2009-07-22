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
 * Support for inner and outer join in query.
 */
public enum JoinCapability {

    /**
     * No join support.
     */
    NO_JOIN("none"),

    /**
     * Support inner join only.
     */
    INNER_ONLY("inneronly"),

    /**
     * Support inner and outer join.
     */
    INNER_AND_OUTER("innerandouter");

    private final String value;

    private JoinCapability(String value) {
        this.value = value;
    }

    private static final Map<String, JoinCapability> all = new HashMap<String, JoinCapability>();
    static {
        for (JoinCapability o : values()) {
            all.put(o.value, o);
        }
    }

    public static JoinCapability get(String value) {
        JoinCapability o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static JoinCapability get(String value, JoinCapability def) {
        JoinCapability o = all.get(value);
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
