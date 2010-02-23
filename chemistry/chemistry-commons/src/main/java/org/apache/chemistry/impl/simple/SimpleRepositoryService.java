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

import java.util.Collection;
import java.util.Collections;

import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryService;

/**
 * Simple repository service holding one repository instance.
 */
public class SimpleRepositoryService implements RepositoryService {

    protected final Repository repository;

    public SimpleRepositoryService(Repository repository) {
        this.repository = repository;
    }

    public Repository getDefaultRepository() {
        return repository;
    }

    public Collection<RepositoryEntry> getRepositories() {
        return Collections.<RepositoryEntry> singleton(repository);
    }

    public Repository getRepository(String repositoryId) {
        return repository.getId().equals(repositoryId) ? repository : null;
    }

}
