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

import java.net.URI;

/**
 * Basic information about a CMIS Repository.
 */
public interface RepositoryEntry {

    /**
     * The repository ID.
     * <p>
     * The ID is an opaque string.
     */
    String getId();

    /**
     * The repository name.
     */
    String getName();

    /**
     * The relationship name to another repository.
     * <p>
     * This returns a value only when this basic info was returned by
     * {@link RepositoryInfo#getRelatedRepositories}.
     *
     * @return a relationship name, or {@code null}
     */
    String getRelationshipName();

    /**
     * An optional repository-specific URI pointing to the repository's web
     * interface.
     *
     * @return an URI, or {@code null}
     */
    URI getThinClientURI();

}
