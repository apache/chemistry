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
package org.apache.chemistry.atompub.client.app.service;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;

/**
 *
 */
public class ServiceContext {

    protected ServiceInfo info;

    protected Repository repository;

    protected Connection connection;

    public ServiceContext(ServiceInfo info, Repository repository) {
        this.info = info;
        this.repository = repository;
    }

    public ServiceContext(ServiceInfo info, Connection connection) {
        this(info, connection.getRepository());
        this.connection = connection;
    }

    public ServiceInfo getInfo() {
        return info;
    }

    public Connection getConnection() {
        return connection;
    }

    public Repository getRepository() {
        return repository;
    }

}
