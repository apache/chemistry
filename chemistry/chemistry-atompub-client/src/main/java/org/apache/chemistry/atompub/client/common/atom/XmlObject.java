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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Type;

/**
 *
 */
public class XmlObject {

    protected Type type;

    protected Connection connection;

    protected Map<String, Serializable> properties;

    protected Set<String> allowableActions;

    public XmlObject(Connection connection, Type type) {
        this.type = type;
        this.connection = connection;
        properties = new HashMap<String, Serializable>();
    }

    public XmlObject(Connection connection) {
        this(connection, null);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Connection getConnection() {
        return connection;
    }

    public Map<String, Serializable> getProperties() {
        return properties;
    }

}
