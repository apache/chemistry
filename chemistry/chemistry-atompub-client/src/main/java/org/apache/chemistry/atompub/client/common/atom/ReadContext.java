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
 */
package org.apache.chemistry.atompub.client.common.atom;

import java.util.HashMap;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;

/**
 *
 */
public class ReadContext extends HashMap<Object, Object> {

    private static final long serialVersionUID = 1L;

    protected Type type;

    protected Repository repository;

    protected Connection connection;

    public ReadContext() {

    }

    public ReadContext(Connection connection) {
        this(connection, null);
    }

    public ReadContext(Repository repository) {
        this(repository, null);
    }

    public ReadContext(Repository repository, Type type) {
        this.type = type;
        this.repository = repository;
        if (repository == null) {
            throw new IllegalArgumentException(
                    "A BuildContext must be bound to a repository");
        }
    }

    public ReadContext(Connection connection, Type type) {
        this.connection = connection;
        this.type = type;
        this.repository = connection.getRepository();
        if (repository == null) {
            throw new IllegalArgumentException(
                    "A BuildContext must be bound to a repository");
        }
    }

    public Repository getRepository() {
        return repository;
    }

    public Connection getConnection() {
        return connection;
    }

    public Type getType() {
        return type;
    }

}
