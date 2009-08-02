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
 * The capabilities of a CMIS Repository.
 */
public interface RepositoryCapabilities {

    /**
     * Ability to file a document (or other fileable object) in more than a
     * folder.
     */
    boolean hasMultifiling();

    /**
     * Ability to leave a document (or other fileable object) not filed in a any
     * folder.
     */
    boolean hasUnfiling();

    /**
     * Ability to file a particular version of a document in a folder.
     */
    boolean hasVersionSpecificFiling();

    /**
     * Ability to update the private working copy of a checked-out document.
     */
    boolean isPWCUpdatable();

    /**
     * Ability to include the private working copy of checked-out documents in
     * query search scope; otherwise private working copies are not searchable.
     */
    boolean isPWCSearchable();

    /**
     * Ability to include non-latest versions of document in query search scope;
     * otherwise only the latest version of each document is searchable.
     */
    // prefixed by "is" to follow the JavaBean spec
    boolean isAllVersionsSearchable();

    /**
     * Ability to enumerate the descendants of a folder via
     * {@link SPI#getDescendants} and {@link SPI#getFolderTree}.
     */
    boolean hasGetDescendants();

    /**
     * Support for query on full-text or metadata.
     */
    QueryCapability getQueryCapability();

    /**
     * Support for inner and outer join in query.
     */
    JoinCapability getJoinCapability();

    /**
     * Support for renditions.
     */
    RenditionCapability getRenditionCapability();

    /**
     * Support for change log.
     */
    ChangeCapability getChangeCapability();

}
