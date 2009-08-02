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
package org.apache.chemistry.impl.simple;

import org.apache.chemistry.ObjectId;

/**
 * A wrapper for an object ID.
 */
public class SimpleObjectId implements ObjectId {

    protected final String id;

    public SimpleObjectId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + id + ')';
    }

    @Override
    public int hashCode() {
        return id == null ? 31 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SimpleObjectId) {
            return equals((SimpleObjectId) obj);
        }
        return false;
    }

    private boolean equals(SimpleObjectId other) {
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }

}
