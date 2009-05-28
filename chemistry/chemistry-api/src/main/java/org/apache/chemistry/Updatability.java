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
 * The Updatability of a property.
 */
public enum Updatability {

    /**
     * Property is read-only.
     * <p>
     * A read-only property is a system property that is either maintained or
     * computed by the repository. An application can not alter the property
     * value directly using the updateProperties() service, and often can not
     * explicitly set the property value when the object is created. In some
     * cases, an application may indirectly cause a change in the property value
     * as a result of calling a special-purpose service. For example, the
     * ParentID property of a folder object is maintained by repository. An
     * application can not alter its value using the updateProperties() service,
     * but may use the moveObject() service to cause a change in the value of
     * the ParentID property.
     */
    READ_ONLY("readonly"),

    /**
     * Property is read-write.
     * <p>
     * A "read + write" property is one that is updatable using the
     * updateProperties() service.
     */
    READ_WRITE("readwrite"),

    /**
     * Property is read-write when checked out.
     * <p>
     * A read-write when checked out property is updatable when the update is
     * made using a Private Working Copy object ID. That is, the update is
     * either made on a Private Working Copy object or made using a "check in"
     * service
     */
    WHEN_CHECKED_OUT("whencheckedout");

    private final String value;

    private Updatability(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
