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
package org.apache.chemistry.atompub.client.connector;

import java.util.List;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlObjectWriter;

/**
 * A Connector abstracts the HTTP or AtomPub operations.
 */
public interface Connector {

    <T> Response post(Request operation, XmlObjectWriter<T> writer, T object)
            throws ContentManagerException;

    <T> Response put(Request operation, XmlObjectWriter<T> writer, T object)
            throws ContentManagerException;

    Response get(Request operation) throws ContentManagerException;

    Response head(Request operation) throws ContentManagerException;

    Response delete(Request operation) throws ContentManagerException;

    Type getType(ReadContext ctx, String href) throws ContentManagerException;

    ObjectEntry getObject(ReadContext ctx, String href)
            throws ContentManagerException;

    List<ObjectEntry> getObjectFeed(ReadContext ctx, String href)
            throws ContentManagerException;

    List<ObjectEntry> getTypeFeed(ReadContext ctx, String href)
            throws ContentManagerException;

    Repository[] getServiceDocument(ReadContext ctx, String href)
            throws ContentManagerException;

    Response putObject(Request req, ObjectEntry entry)
            throws ContentManagerException;

    Response putQuery(Request req, String query) throws ContentManagerException;

    Response postObject(Request req, ObjectEntry entry)
            throws ContentManagerException;

    Response postQuery(Request req, String query)
            throws ContentManagerException;

}
