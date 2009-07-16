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
 *     Emanuele Lombardi
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Property;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.connector.Connector;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;

/**
 *
 */
public class APPDocument extends APPObject implements Document {

    protected static final String UNINITIALIZED_STRING = "__UNINITIALIZED__\0\0\0";

    protected static final URI UNINITIALIZED_URI;
    static {
        try {
            UNINITIALIZED_URI = new URI("http://__UNINITIALIZED__/%00%00%00");
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public APPDocument(APPObjectEntry entry, Type type) {
        super(entry, type);
    }

    public ContentStream getContentStream() {
        String url = entry.getLink(CMIS.LINK_STREAM);
        return url == null ? null : new APPContentStream(url);
    }

    /**
     * ContentStream class that fetches a remote URL when needed.
     */
    public class APPContentStream implements ContentStream {

        protected final Connector connector;

        protected final String url;

        protected String mimeType = UNINITIALIZED_STRING;

        protected String filename = UNINITIALIZED_STRING;

        protected URI uri = UNINITIALIZED_URI;

        protected long length = -1;

        public APPContentStream(String url) {
            connector = APPDocument.this.entry.connection.connector;
            this.url = url;
        }

        public String getMimeType() {
            if (mimeType == UNINITIALIZED_STRING) {
                mimeType = getString(Property.CONTENT_STREAM_MIME_TYPE);
            }
            return mimeType;
        }

        public String getFilename() {
            if (filename == UNINITIALIZED_STRING) {
                filename = getString(Property.CONTENT_STREAM_FILENAME);
            }
            return filename;
        }

        public URI getURI() {
            if (uri == UNINITIALIZED_URI) {
                uri = APPDocument.this.getURI(Property.CONTENT_STREAM_URI);
            }
            return uri;
        }

        public long getLength() {
            if (length == -1) {
                Integer value = getInteger(Property.CONTENT_STREAM_LENGTH);
                return length = value == null ? -1 : value.longValue();
            }
            return length;
        }

        public InputStream getStream() throws IOException {
            try {
                Response resp = connector.get(new Request(url));
                if (!resp.isOk()) {
                    throw new IOException("Error: " + resp.getStatusCode()
                            + " fetching: " + url);
                }
                if (length == -1) {
                    // get the "official" length if available
                    length = resp.getStreamLength();
                }
                return resp.getStream();
            } catch (ContentManagerException e) {
                throw (IOException) (new IOException(
                        "Could not fetch stream from: " + url).initCause(e));
            }
        }
    }

    public InputStream getStream() throws IOException {
        String href = entry.getLink(CMIS.LINK_STREAM);
        if (href == null) {
            return null;
        }
        Response resp = entry.connection.connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return resp.getStream();
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void cancelCheckOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkIn(boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Document> getAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document getLatestVersion(boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
