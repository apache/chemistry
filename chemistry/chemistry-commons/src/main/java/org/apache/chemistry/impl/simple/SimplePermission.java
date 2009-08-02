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

import org.apache.chemistry.Permission;

public class SimplePermission implements Permission {

    public static final SimplePermission PERMISSION_READ = new SimplePermission(
            Permission.READ, "Read");

    public static final SimplePermission PERMISSION_WRITE = new SimplePermission(
            Permission.WRITE, "Write");

    public static final SimplePermission PERMISSION_DELETE = new SimplePermission(
            Permission.DELETE, "Delete");

    public static final SimplePermission PERMISSION_ALL = new SimplePermission(
            Permission.ALL, "All");

    protected final String id;

    protected final String description;

    protected SimplePermission(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
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
        if (obj instanceof SimplePermission) {
            return equals((SimplePermission) obj);
        }
        return false;
    }

    private boolean equals(SimplePermission other) {
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + id + ')';
    }

}
