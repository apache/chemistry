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

    @Override
    public String toString() {
        return value;
    }
}
