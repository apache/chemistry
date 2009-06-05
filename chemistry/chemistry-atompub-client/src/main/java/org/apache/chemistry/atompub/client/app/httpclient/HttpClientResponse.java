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
package org.apache.chemistry.atompub.client.app.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.app.Connector;
import org.apache.chemistry.atompub.client.app.Response;
import org.apache.chemistry.atompub.client.common.atom.ReadContext;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

/**
 *
 */
public class HttpClientResponse implements Response {

    final static int MIN_BUF_LEN = 32 * 1024;

    final static int MAX_BUF_LEN = 128 * 1024;

    protected HttpMethod method;

    protected Connector connector;

    public HttpClientResponse(Connector connector, HttpMethod method) {
        this.method = method;
        this.connector = connector;
    }

    public String getHeader(String key) {
        Header h = method.getResponseHeader(key);
        return h == null ? null : h.getValue();
    }

    public int getStatusCode() {
        return method.getStatusCode();
    }

    public boolean isOk() {
        return method.getStatusCode() < 400;
    }

    public InputStream getStream() throws ContentManagerException {
        try {
            return method.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new ContentManagerException("Failed to get response stream",
                    e);
        }
    }

    public String getString() throws ContentManagerException {
        try {
            return method.getResponseBodyAsString();
        } catch (IOException e) {
            throw new ContentManagerException("Failed to get response stream",
                    e);
        }
    }

    public byte[] getBytes() throws ContentManagerException {
        InputStream in = null;
        try {
            in = getStream();
            int len = in.available();
            if (len < MIN_BUF_LEN) {
                len = MIN_BUF_LEN;
            } else {
                len = MAX_BUF_LEN;
            }
            byte[] buffer = new byte[len];
            int read;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
            while ((read = in.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ContentManagerException("Failed to get response", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    public ObjectEntry getObject(ReadContext ctx)
            throws ContentManagerException {
        try {
            return connector.getAPPContentManager().getIO().getObjectEntryReader().read(
                    ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public Type getType(ReadContext ctx) throws ContentManagerException {
        try {
            return connector.getAPPContentManager().getIO().getTypeEntryReader().read(
                    ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public Map<String, Type> getTypeFeed(ReadContext ctx)
            throws ContentManagerException {
        try {
            return connector.getAPPContentManager().getIO().getTypeFeedReader().read(
                    ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public List<ObjectEntry> getObjectFeed(ReadContext ctx)
            throws ContentManagerException {
        try {
            return connector.getAPPContentManager().getIO().getObjectFeedReader().read(
                    ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public Repository[] getServiceDocument(ReadContext ctx)
            throws ContentManagerException {
        try {
            return connector.getAPPContentManager().getIO().getServiceDocumentReader().read(
                    ctx, getStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        }
    }

}
