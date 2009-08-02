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

import java.util.Map;
import java.util.Set;

/**
 * The type of ACL capabilities of the repository.
 *
 * @see RepositoryInfo#getACLCapabilityType
 */
public interface ACLCapabilityType {

    /**
     * Gets which types of permissions are supported by the repository (basic,
     * repository-specific, or both).
     */
    PermissionsSupported getSupportedPermissions();

    /**
     * Gets how non-direct ACEs can be handled by the repository
     */
    ACLPropagation getACLPropagation();

    /**
     * The set of permissions supported by the repository.
     * <p>
     * This is only useful if {@link #getSupportedPermissions} is not
     * {@link PermissionsSupported#BASIC}.
     */
    Set<Permission> getRepositoryPermissions();

    /**
     * The mapping of allowable actions to permissions.
     * <p>
     * The key is a unique name representing the allowable action plus one of
     * the operands for this action that the mapping applies to. It is built by
     * using the name of the allowable action (see
     * {@link SPI#getAllowableActions}) and the name of the operand (without the
     * ID or prefix)
     * <p>
     * The value is a permission.
     *
     * @see SPI#getAllowableActions
     * @see Permission#READ
     * @see Permission#WRITE
     * @see Permission#DELETE
     * @see Permission#ALL
     */
    Map<String, String> getPermissionMappings();

}
