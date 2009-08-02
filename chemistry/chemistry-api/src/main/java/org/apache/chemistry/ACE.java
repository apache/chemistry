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
 * An access control entry.
 */
public interface ACE {

    /**
     * The principal.
     */
    String getPrincipal();

    /**
     * The permission.
     *
     * @see Permission#READ
     * @see Permission#WRITE
     * @see Permission#DELETE
     * @see Permission#ALL
     */
    String getPermission();

    /**
     * This is {@code true} if the ACE is directly assigned to the object
     * itself, and {@code false} if the ACE is somehow derived from some other
     * ACE or Policy applied to another object.
     */
    boolean isDirect();

}
