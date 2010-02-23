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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Entry point for Java code wishing to get to registered CMIS repositories, or
 * to register new ones for the current JVM.
 */
public class RepositoryManager implements RepositoryService {

    protected static RepositoryManager instance;

    protected List<RepositoryService> services = new CopyOnWriteArrayList<RepositoryService>();

    public static RepositoryManager getInstance() {
        if (instance == null) {
            synchronized (RepositoryManager.class) {
                if (instance == null) {
                    instance = new RepositoryManager();
                }
            }
        }
        return instance;
    }

    public synchronized void registerService(RepositoryService service) {
        if (service == this) {
            // avoid stupid errors
            throw new IllegalArgumentException();
        }
        if (services.contains(service)) {
            throw new IllegalArgumentException();
        }
        services.add(service);
    }

    public synchronized void unregisterService(RepositoryService service) {
        if (!services.remove(service)) {
            throw new IllegalArgumentException();
        }
    }

    public Repository getDefaultRepository() {
        for (RepositoryService service : services) {
            Repository repository = service.getDefaultRepository();
            if (repository != null) {
                return repository;
            }
        }
        return null;
    }

    public Collection<RepositoryEntry> getRepositories() {
        List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>(1);
        for (RepositoryService service : services) {
            entries.addAll(service.getRepositories());
        }
        return entries;
    }

    public Repository getRepository(String repositoryId) {
        for (RepositoryService service : services) {
            Repository repository = service.getRepository(repositoryId);
            if (repository != null) {
                return repository;
            }
        }
        return null;
    }

}
