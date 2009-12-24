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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;

/**
 *
 */
public class HttpClientResponse implements Response {

    static final int MIN_BUF_LEN = 32 * 1024;

    static final int MAX_BUF_LEN = 128 * 1024;

    protected HttpMethod method;

    protected final IOProvider io;

    public HttpClientResponse(HttpMethod method, IOProvider io) {
        this.method = method;
        this.io = io;
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

    public String getStatusReasonPhrase() {
        return method.getStatusLine().getReasonPhrase();
    }

    public InputStream getStream() throws ContentManagerException {
        try {
            return method.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new ContentManagerException("Failed to get response stream",
                    e);
        }
    }

    public long getStreamLength() {
        if (method instanceof HttpMethodBase) {
            return ((HttpMethodBase) method).getResponseContentLength();
        }
        return -1;
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
            return io.getObjectEntryReader().read(ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public Type getType(ReadContext ctx) throws ContentManagerException {
        try {
            return io.getTypeEntryReader().read(ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public TypeManager getTypeFeed(ReadContext ctx)
            throws ContentManagerException {
        try {
            return io.getTypeFeedReader().read(ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public ListPage<ObjectEntry> getObjectFeed(ReadContext ctx)
            throws ContentManagerException {
        try {
            return io.getObjectFeedReader().read(ctx, getStream());
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        }
    }

    public Repository[] getServiceDocument(ReadContext ctx)
            throws ContentManagerException {
        try {
            return io.getServiceDocumentReader().read(ctx, getStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        }
    }

}
