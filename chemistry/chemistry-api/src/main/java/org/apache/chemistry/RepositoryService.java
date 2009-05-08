/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry;

import java.util.Collection;

/**
 * CMIS Repository Service.
 *
 * @author Florent Guillaume
 */
public interface RepositoryService {

    /**
     * Gets a list of available repositories.
     *
     * @return a collection of repository entries
     */
    Collection<RepositoryEntry> getRepositories();

    /**
     * Gets the default repository, if any.
     * <p>
     * If not default repository is available, {@code null} is returned.
     *
     * @return the default repository, or {@code null}
     */
    Repository getDefaultRepository();

    /**
     * Gets a repository identified by its ID.
     *
     * @param repositoryId the repository ID
     */
    Repository getRepository(String repositoryId);

}
