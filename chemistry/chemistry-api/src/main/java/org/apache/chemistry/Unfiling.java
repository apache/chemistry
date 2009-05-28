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
 * Flag specifying how to unfile non-folder objects when a tree of objects is
 * deleted through {@link Connection#deleteTree}.
 */
public enum Unfiling {

    /**
     * Unfile all non-folder objects from folders in this tree. They may remain
     * filed in other folders, or may become unfiled if no other folder contains
     * them.
     */
    UNFILE("unfile"),

    /**
     * Deletes non-folder objects filed only in this tree, and unfiles the
     * others so they remain filed in other folders.
     */
    DELETE_SINGLE_FILED("deletesinglefiled"),

    /**
     * Deletes all non-folder objects in this tree.
     */
    DELETE("delete");

    private final String value;

    private Unfiling(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
