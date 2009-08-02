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
 * A repository permission.
 */
public interface Permission {

    /**
     * Permission to read properties and content streams.
     * <p>
     * Can cover more than this depending on the repository.
     */
    String READ = "cmis:read";

    /**
     * Permission to write properties and content stream.
     * <p>
     * Can cover more than this depending on the repository.
     */
    String WRITE = "cmis:write";

    /**
     * Permission to delete objects and object trees.
     * <p>
     * Can cover more than this depending on the repository.
     */
    String DELETE = "cmis:delete";

    /**
     * Covers all permissions.
     */
    String ALL = "cmis:all";

    /**
     * The technical name of the permission.
     */
    String getId();

    /**
     * The description for the permission, presented to the user.
     */
    String getDescription();

}
