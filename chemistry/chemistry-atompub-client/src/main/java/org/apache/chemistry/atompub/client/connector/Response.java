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

import java.io.InputStream;

import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.stax.ReadContext;

/**
 *
 */
public interface Response {

    int getStatusCode();

    String getHeader(String key);

    InputStream getStream() throws ContentManagerException;

    /**
     * Gets the stream length.
     *
     * @return the stream length, or -1 if not know
     */
    long getStreamLength() throws ContentManagerException;

    byte[] getBytes() throws ContentManagerException;

    String getString() throws ContentManagerException;

    ListPage<ObjectEntry> getObjectFeed(ReadContext ctx)
            throws ContentManagerException;

    TypeManager getTypeFeed(ReadContext ctx, boolean includePropertyDefinitions)
            throws ContentManagerException;

    ObjectEntry getObject(ReadContext ctx) throws ContentManagerException;

    Type getType(ReadContext ctx, boolean includePropertyDefinitions)
            throws ContentManagerException;

    Repository[] getServiceDocument(ReadContext ctx)
            throws ContentManagerException;

    boolean isOk();

    String getStatusReasonPhrase();

}
