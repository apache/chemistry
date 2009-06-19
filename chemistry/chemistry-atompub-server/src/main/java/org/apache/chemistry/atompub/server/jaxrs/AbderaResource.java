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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.server.jaxrs;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.server.CMISProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JAX-RS Resource that dispatches to the underlying Abdera
 * {@link CMISProvider}.
 */
public class AbderaResource {

    private static final Log log = LogFactory.getLog(AbderaResource.class);

    protected CMISProvider provider;

    @Context
    protected HttpServletRequest httpRequest;

    // TODO inject repository somehow
    public static Repository repository;

    public AbderaResource() throws Exception {
        try {
            provider = new CMISProvider(repository);
            provider.init(ServiceManager.getAbdera(),
                    new HashMap<String, String>());
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    protected ServletRequestContext getRequestContext() {
        return new ServletRequestContext(provider, httpRequest);
    }

    protected CollectionAdapter getAbderaCollectionAdapter(
            RequestContext requestContext) {
        return provider.getWorkspaceManager(requestContext).getCollectionAdapter(
                requestContext);
    }

    protected Response getAbderaFeed() {
        RequestContext requestContext = getRequestContext();
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        return Response.ok(adapter.getFeed(requestContext)).build();
    }

    protected Response getAbderaEntry() {
        RequestContext requestContext = getRequestContext();
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        return Response.ok(adapter.getEntry(requestContext)).build();
    }

    @GET
    @Produces("application/atomsvc+xml")
    @Path("repository")
    public Response doGetRepository(@Context HttpServletRequest httpRequest) {
        RequestContext requestContext = getRequestContext();
        ResponseContext responseContext = provider.getServiceDocument(requestContext);
        return Response.ok(responseContext).build();
    }

    @GET
    @Produces("application/atom+xml;type=feed")
    @Path("types")
    public Response doGetTypes() {
        return getAbderaFeed();
    }

    @GET
    @Produces("application/atom+xml;type=feed")
    @Path("children/{objectid}")
    public Response doGetChildren() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaFeed();
    }

    @GET
    @Produces("application/atom+xml;type=entry")
    @Path("object/{objectid}")
    public Response doGetObject() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaEntry();
    }

    @GET
    @Path("file/{objectid}")
    public Response doGetFile() {
        // objectid decoded by Abdera getCollectionAdapter
        RequestContext requestContext = getRequestContext();
        AbstractCollectionAdapter adapter = (AbstractCollectionAdapter) getAbderaCollectionAdapter(requestContext);
        ResponseContext responseContext = adapter.getMedia(requestContext);
        String contentType = responseContext.getHeader("Content-Type");
        return Response.ok(responseContext).type(contentType).build();
    }

    // @PUT
    // @Path("object/{objectid}}")
    // @Consumes("application/atom+xml;type=entry")
    // public Response doPut(@PathParam("objectid") String objectid) {
    // return AbderaProvider.putEntry(ctx, getCollectionAdapter());
    // }

}
