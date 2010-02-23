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
 *     Bogdan Stefanescu, Nuxeo
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;

/**
 * A context for the current AtomPub operations.
 * <p>
 * Used to construct new objects.
 */
public class APPContext {

    protected APPRepositoryService repositoryService;

    protected APPRepository repository;

    protected APPConnection connection;

    public APPContext(APPConnection connection) {
        this.connection = connection;
        this.repository = (APPRepository) connection.getRepository();
    }

    public APPContext(APPRepository repository) {
        this.repository = repository;
    }

    public APPContext(APPRepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public APPRepositoryService getRepositoryService() {
        return repositoryService;
    }

    public Repository getRepository() {
        return repository;
    }

    public Connection getConnection() {
        return connection;
    }

}
