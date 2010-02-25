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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryService;

/**
 * Simple repository service holding one ore more repository instance.
 */
public class SimpleRepositoryService implements RepositoryService {

    private List<Repository> repositories;

    /**
     * Service holding one repository.
     */
    public SimpleRepositoryService(Repository repository) {
        this.repositories = Collections.singletonList(repository);
    }

    /**
     * Service holding one or more repository. The first repository is used as
     * the default.
     */
    public SimpleRepositoryService(List<Repository> repositories) {
        if (repositories == null || repositories.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.repositories = new ArrayList<Repository>(repositories);
    }

    public Repository getDefaultRepository() {
        return repositories.get(0);
    }

    public Collection<RepositoryEntry> getRepositories() {
        return Collections.<RepositoryEntry> unmodifiableCollection(repositories);
    }

    public Repository getRepository(String repositoryId) {
        for (Repository repository : repositories) {
            if (repository.getId().equals(repositoryId)) {
                return repository;
            }
        }
        return null;
    }

}
