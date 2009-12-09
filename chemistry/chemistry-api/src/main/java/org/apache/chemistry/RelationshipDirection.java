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
 * The choice of which relationships having a given document as source or target
 * are to be included in a list of objects.
 */
public enum RelationshipDirection {

    /**
     * The relationships having the given document as a source are included.
     */
    SOURCE("source"),

    /**
     * The relationships having the given document as a target are included.
     */
    TARGET("target"),

    /**
     * The relationships having the given document as either a source or a
     * target are included.
     */
    EITHER("either");

    private final String value;

    private RelationshipDirection(String value) {
        this.value = value;
    }

    private static final Map<String, RelationshipDirection> all = new HashMap<String, RelationshipDirection>();
    static {
        for (RelationshipDirection o : values()) {
            all.put(o.value, o);
        }
    }

    public static RelationshipDirection get(String value) {
        RelationshipDirection o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public static RelationshipDirection get(String value,
            RelationshipDirection def) {
        RelationshipDirection o = all.get(value);
        if (o == null) {
            o = def;
        }
        return o;
    }

    protected static final String INCLUSION_NONE = "none";

    protected static final String INCLUSION_SOURCE = "source";

    protected static final String INCLUSION_TARGET = "target";

    protected static final String INCLUSION_BOTH = "both";

    public static RelationshipDirection fromInclusion(String value) {
        if (INCLUSION_SOURCE.equals(value)) {
            return SOURCE;
        } else if (INCLUSION_TARGET.equals(value)) {
            return TARGET;
        } else if (INCLUSION_BOTH.equals(value)) {
            return EITHER;
        } else {
            return null;
        }
    }

    public static String toInclusion(RelationshipDirection o) {
        if (o == SOURCE) {
            return INCLUSION_SOURCE;
        } else if (o == TARGET) {
            return INCLUSION_TARGET;
        } else if (o == EITHER) {
            return INCLUSION_BOTH;
        } else {
            return INCLUSION_NONE;
        }

    }

    @Override
    public String toString() {
        return value;
    }

}
