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
 */
package org.apache.chemistry.atompub.client.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.ContentManagerException;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlObjectWriter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 *
 */
public class HttpClientConnector implements Connector {

    protected HttpClient client;

    protected final IOProvider io;

    public HttpClientConnector(IOProvider io) {
        this.io = io;
        client = new HttpClient();
    }

    public void setCredentialsProvider(CredentialsProvider cp) {
        client.getParams().setAuthenticationPreemptive(true);
        client.getParams().setParameter(CredentialsProvider.PROVIDER, cp);
    }

    protected void setMethodParams(HttpMethod method, Request request) {
        List<String> params = request.getParameters();
        if (params != null) {
            int len = params.size() >> 1;
            if (len > 0) {
                NameValuePair[] qs = new NameValuePair[len];
                for (int i = 0, k = 0; i < len; i++, k += 2) {
                    qs[i] = new NameValuePair(params.get(k), params.get(k + 1));
                }
                method.setQueryString(qs);
            }
        }
    }

    protected void setMethodHeaders(HttpMethod method, Request request) {
        List<String> headers = request.getHeaders();
        if (headers != null) {
            int len = headers.size();
            for (int k = 0; k < len; k += 2) {
                method.addRequestHeader(headers.get(k), headers.get(k + 1));
            }
        }
    }

    public Response get(Request request) throws ContentManagerException {
        try {
            GetMethod method = new GetMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("GET request failed", e);
        }
    }

    public Response delete(Request request) throws ContentManagerException {
        try {
            DeleteMethod method = new DeleteMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("DELETE request failed", e);
        }
    }

    public Response head(Request request) throws ContentManagerException {
        try {
            HeadMethod method = new HeadMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("HEAD request failed", e);
        }
    }

    public <T> Response post(Request request, XmlObjectWriter<T> writer,
            T object) throws ContentManagerException {
        try {
            PostMethod method = new PostMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            method.setRequestEntity(new XmlObjectWriterRequestEntity<T>(writer,
                    object));
            method.setContentChunked(true);
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("POST request failed", e);
        }
    }

    public <T> Response put(Request request, XmlObjectWriter<T> writer, T object)
            throws ContentManagerException {
        try {
            PutMethod method = new PutMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            method.setRequestEntity(new XmlObjectWriterRequestEntity<T>(writer,
                    object));
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("PUT request failed", e);
        }
    }

    public Response put(Request request, InputStream in, long length,
            String type) throws ContentManagerException {
        try {
            PutMethod method = new PutMethod(request.getUrl());
            setMethodParams(method, request);
            setMethodHeaders(method, request);
            method.setRequestEntity(new InputStreamRequestEntity(in, length, type));
            client.executeMethod(method);
            return new HttpClientResponse(method, io);
        } catch (Exception e) {
            throw new ContentManagerException("PUT request failed", e);
        }
    }

    public Type getType(ReadContext ctx, String href) {
        Request req = new Request(href);
        Response resp = get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode() + "\n\n" + resp.getString());
        }
        return resp.getType(ctx);
    }

    public ObjectEntry getObject(ReadContext ctx, String href) {
        Request req = new Request(href);
        Response resp = get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode() + "\n\n" + resp.getString());
        }
        return resp.getObject(ctx);
    }

    public List<ObjectEntry> getObjectFeed(ReadContext ctx, String href)
            throws ContentManagerException {
        Request req = new Request(href);
        Response resp = get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode() + "\n\n" + resp.getString());
        }
        return resp.getObjectFeed(ctx);
    }

    public List<ObjectEntry> getTypeFeed(ReadContext ctx, String href)
            throws ContentManagerException {
        Request req = new Request(href);
        Response resp = get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode() + "\n\n" + resp.getString());
        }
        return resp.getObjectFeed(ctx);
    }

    public Repository[] getServiceDocument(ReadContext ctx, String href)
            throws ContentManagerException {
        Request req = new Request(href);
        Response resp = get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode() + "\n\n" + resp.getString());
        }
        return resp.getServiceDocument(ctx);
    }

    public Response putObject(Request req, ObjectEntry entry)
            throws ContentManagerException {
        return put(req, io.getObjectEntryWriter(), entry);
    }

    public Response putQuery(Request req, String query,
            boolean searchAllVersions, boolean includeAllowableActions,
            Paging paging) throws ContentManagerException {
        return put(req, io.getQueryWriter(searchAllVersions,
                includeAllowableActions, paging), query);
    }

    public Response postObject(Request req, ObjectEntry entry)
            throws ContentManagerException {
        return post(req, io.getObjectEntryWriter(), entry);
    }

    public Response postQuery(Request req, String query,
            boolean searchAllVersions, boolean includeAllowableActions,
            Paging paging) throws ContentManagerException {
        return post(req, io.getQueryWriter(searchAllVersions,
                includeAllowableActions, paging), query);
    }

    public static class XmlObjectWriterRequestEntity<T> implements
            RequestEntity {

        protected XmlObjectWriter<T> writer;

        protected T obj;

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
