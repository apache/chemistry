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
package org.apache.chemistry.atompub.client.stax;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.ContentManager;

/**
 *
 */
public class ReadContext {

    protected Repository repository;

    protected Connection connection;

    protected ContentManager contentManager;

    public ReadContext(Connection connection) {
        this.connection = connection;
        this.repository = connection.getRepository();
    }

    public ReadContext(Repository repository) {
        this.repository = repository;
    }

    public ReadContext(ContentManager contentManager) {
        this.contentManager = contentManager;
    }

    public Repository getRepository() {
        return repository;
    }

    public Connection getConnection() {
        return connection;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

}
