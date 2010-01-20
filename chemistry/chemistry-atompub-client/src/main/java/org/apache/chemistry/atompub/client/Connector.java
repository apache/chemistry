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
 *     Ugo Cei, Sourcesense
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectNotFoundException;
import org.apache.chemistry.Paging;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.client.APPObjectEntry;
import org.apache.chemistry.atompub.client.APPObjectEntryReader;
import org.apache.chemistry.atompub.client.APPObjectEntryWriter;
import org.apache.chemistry.atompub.client.APPObjectFeedReader;
import org.apache.chemistry.atompub.client.APPRepository;
import org.apache.chemistry.atompub.client.APPServiceDocumentReader;
import org.apache.chemistry.atompub.client.APPType;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.TypeEntryReader;
import org.apache.chemistry.atompub.client.TypeFeedReader;
import org.apache.chemistry.atompub.client.stax.EntryReader;
import org.apache.chemistry.atompub.client.stax.FeedReader;
import org.apache.chemistry.atompub.client.stax.QueryWriter;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlObjectWriter;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * A Connector abstracts the HTTP or AtomPub operations.
 */
public class Connector {

    protected final HttpClient client;

    protected final ReadContext ctx;

    public Connector(HttpClient client, ReadContext ctx) {
        this.client = client;
        this.ctx = ctx;
    }

    public APPRepository[] getServiceDocument(String href) {
        GetMethod method = new GetMethod(href);
        try {
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            return new APPServiceDocumentReader().read(ctx,
                    method.getResponseBodyAsStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public ListPage<ObjectEntry> getEntryFeed(String href, NameValuePairs params) {
        return getObjectFeed(href, params, new APPObjectFeedReader());
    }

    public TypeManager getTypeFeed(String href,
            boolean includePropertyDefinitions) {
        return getObjectFeed(href, null, new TypeFeedReader(
                includePropertyDefinitions));
    }

    protected <T> T getObjectFeed(String href, NameValuePairs params,
            FeedReader<T> reader) {
        HttpMethod method = new GetMethod(href);
        try {
            if (params != null) {
                method.setQueryString(params.toArray());
            }
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            return reader.read(ctx, method.getResponseBodyAsStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public APPObjectEntry getEntry(String href, String msg) {
        return getObject(href, msg, new APPObjectEntryReader());
    }

    public APPType getType(String href, boolean includePropertyDefinitions,
            String msg) {
        return getObject(href, msg, new TypeEntryReader(
                includePropertyDefinitions));
    }

    protected <T> T getObject(String href, String msg, EntryReader<T> reader) {
        HttpMethod method = new GetMethod(href);
        try {
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status == HttpStatus.SC_NOT_FOUND) {
                throw new ObjectNotFoundException(msg);
            }
            if (status == HttpStatus.SC_CONFLICT) {
                throw new ConstraintViolationException(msg);
            }
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            return reader.read(ctx, method.getResponseBodyAsStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public ContentStream getContentStream(String href, String mimeType,
            String filename) throws IOException {
        HttpMethod method = new GetMethod(href);
        try {
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status == HttpStatus.SC_NOT_FOUND
                    || status == HttpStatus.SC_CONFLICT) {
                throw new ConstraintViolationException("No content stream");
            }
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            InputStream is = method.getResponseBodyAsStream();
            return new SimpleContentStream(is, mimeType, filename);
        } finally {
            // because of this, we have to consume the stream completely
            // therefore we must copy it (SimpleContentStream does it)
            method.releaseConnection();
        }

    }

    public APPObjectEntry putEntry(String href, Header header,
            APPObjectEntry entry) {
        RequestEntity requestEntity = new XmlObjectWriterRequestEntity<ObjectEntry>(
                new APPObjectEntryWriter(), entry);
        return put(href, header, requestEntity);
    }

    public APPObjectEntry putStream(String href, ContentStream cs)
            throws IOException {
        RequestEntity requestEntity = new InputStreamRequestEntity(
                cs.getStream(), cs.getLength(), cs.getMimeType());
        // Use Slug: header for filename
        String filename = cs.getFileName();
        Header header = filename == null ? null : new Header(
                AtomPub.HEADER_SLUG, filename);
        return put(href, header, requestEntity);
    }

    protected APPObjectEntry put(String href, Header header,
            RequestEntity requestEntity) {
        PutMethod method = new PutMethod(href);
        try {
            if (header != null) {
                method.addRequestHeader(header);
            }
            method.setRequestEntity(requestEntity);
            method.setContentChunked(true);
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            if (requestEntity instanceof InputStreamRequestEntity) {
                // no answer expected for stream put
                return null;
            } else {
                return new APPObjectEntryReader().read(ctx,
                        method.getResponseBodyAsStream());
            }
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public APPObjectEntry postEntry(String href, NameValuePairs params,
            APPObjectEntry entry) {
        PostMethod method = new PostMethod(href);
        try {
            if (params != null) {
                method.setQueryString(params.toArray());
            }
            method.addRequestHeader("Content-Type",
                    AtomPub.MEDIA_TYPE_ATOM_ENTRY);
            method.setRequestEntity(new XmlObjectWriterRequestEntity<ObjectEntry>(
                    new APPObjectEntryWriter(), entry));
            method.setContentChunked(true);
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status != HttpStatus.SC_CREATED) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            APPObjectEntry newEntry = new APPObjectEntryReader().read(ctx,
                    method.getResponseBodyAsStream());
            // newEntry SHOULD be returned (AtomPub 9.2)...
            Header loc = method.getResponseHeader("Location");
            Header cloc = method.getResponseHeader("Content-Location");
            if (loc == null) {
                throw new ContentManagerException(
                        "Remote server failed to return a Location header");
            }
            if (newEntry == null || !loc.equals(cloc)) {
                // (Content-Location defined by AtomPub 9.2)
                // fetch actual new entry from Location header
                // TODO could fetch only a subset of the properties, if deemed
                // ok
                newEntry = getEntry(loc.getValue(), loc.getValue());
                if (newEntry == null) {
                    throw new ContentManagerException(
                            "Remote server failed to return an entry for Location: "
                                    + loc);
                }
            }
            return newEntry;
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public ListPage<ObjectEntry> postQuery(String href, String statement,
            boolean searchAllVersions, Inclusion inclusion, Paging paging) {
        PostMethod method = new PostMethod(href);
        try {
            method.setRequestEntity(new XmlObjectWriterRequestEntity<String>(
                    new QueryWriter(searchAllVersions, inclusion, paging),
                    statement));
            method.setContentChunked(true);
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
            return new APPObjectFeedReader().read(ctx,
                    method.getResponseBodyAsStream());
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } catch (XMLStreamException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public void delete(String href, NameValuePairs params, String msg) {
        HttpMethod method = new DeleteMethod(href);
        try {
            if (params != null) {
                method.setQueryString(params.toArray());
            }
            client.executeMethod(method);
            int status = method.getStatusCode();
            if (status == HttpStatus.SC_NOT_FOUND) {
                throw new ObjectNotFoundException(msg);
            }
            if (status == HttpStatus.SC_CONFLICT) {
                throw new ConstraintViolationException(
                        method.getStatusLine().getReasonPhrase());
            }
            if (status >= HttpStatus.SC_BAD_REQUEST) {
                throw new ContentManagerException(
                        "Remote server returned error code: " + status);
            }
        } catch (IOException e) {
            throw new ContentManagerException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public static class XmlObjectWriterRequestEntity<T> implements
            RequestEntity {

        protected final XmlObjectWriter<T> writer;

        protected final T obj;

        public XmlObjectWriterRequestEntity(XmlObjectWriter<T> writer, T obj) {
            this.writer = writer;
            this.obj = obj;
        }

        public long getContentLength() {
            return -1;
        }

        public String getContentType() {
            return writer.getContentType();
        }

        public boolean isRepeatable() {
            return false;
        }

        public void writeRequest(OutputStream out) throws IOException {
            writer.write(obj, out);
        }
    }

}
