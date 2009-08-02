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

import java.util.Collection;
import java.util.Set;

/**
 * Information about a CMIS Repository.
 */
public interface RepositoryInfo extends RepositoryEntry {

    /**
     * The repository description.
     */
    String getDescription();

    /**
     * The ID of the root Folder of the repository.
     */
    ObjectId getRootFolderId();

    /**
     * The repository vendor name.
     */
    String getVendorName();

    /**
     * The repository product name.
     */
    String getProductName();

    /**
     * The repository product version.
     */
    String getProductVersion();

    /**
     * The CMIS version supported by the repository.
     */
    String getVersionSupported();

    /**
     * Some repository-specific information.
     *
     * @return an XML Document, or {@code null} if no information is provided
     */
    org.w3c.dom.Document getRepositorySpecificInformation();

    /**
     * The capabilities of the repository.
     */
    RepositoryCapabilities getCapabilities();

    /**
     * The type of ACL capabilities of the repository.
     * <p>
     * Available only if {@link RepositoryCapabilities#getACLCapability} is not
     * {@link ACLCapability#NONE}.
     */
    ACLCapabilityType getACLCapabilityType();

    /**
     * The base types for which changes are reported by the change log.
     */
    Set<BaseType> getChangeLogBaseTypes();

    /**
     * Is the change log incomplete (missing older events).
     */
    boolean isChangeLogIncomplete();

    /**
     * The change log token corresponding to the most recent change to any
     * object in the repository.
     */
    String getLatestChangeLogToken();

    /**
     * The related repositories.
     *
     * @return the related repositories, or {@code null} if no information is
     *         provided
     */
    Collection<RepositoryEntry> getRelatedRepositories();

}
