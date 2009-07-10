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
 * Support for query on full-text or metadata.
 */
public enum QueryCapability {

    /**
     * No query support.
     */
    NONE("none"),

    /**
     * Support only metadata queries.
     */
    METADATA_ONLY("metadataonly"),

    /**
     * Support only full-text queries.
     */
    FULL_TEXT_ONLY("fulltextonly"),

    /**
     * Support both full-text and metadata queries, but not in the same query.
     */
    BOTH_SEPARATE("bothseparate"),

    /**
     * Support both full-text and metadata queries, in the same query.
     */
    BOTH_COMBINED("bothcombined");

    private final String value;

    private QueryCapability(String value) {
        this.value = value;
    }

    private static final Map<String, QueryCapability> all = new HashMap<String, QueryCapability>();
    static {
        for (QueryCapability o : values()) {
            all.put(o.value, o);
        }
    }

    public static QueryCapability get(String value) {
        QueryCapability o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static QueryCapability get(String value, QueryCapability def) {
        QueryCapability o = all.get(value);
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
