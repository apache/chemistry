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
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.Atom;
import org.apache.chemistry.atompub.server.CMISProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JAX-RS Resource that dispatches to the underlying Abdera
 * {@link CMISProvider}.
 */
@Path("cmis")
public class AbderaResource {

    private static final Log log = LogFactory.getLog(AbderaResource.class);

    protected CMISProvider provider;

    @Context
    protected HttpServletRequest httpRequest;

    @Context
    protected UriInfo ui;

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

    /**
     * Gets a {@link ServletRequestContext} wrapping the httpRequest.
     * <p>
     * Wrapping is needed to fixup the servlet path to take include this
     * Resource's path.
     * <p>
     * We need to pass an explicit number of segments because
     * UriInfo.getMatchedURIs is buggy for RESTEasy
     * (https://jira.jboss.org/jira/browse/RESTEASY-100)
     *
     * @param segments the number of segments of the method invoking this, used
     *            to determine the Resource path
     */
    protected ServletRequestContext getRequestContext(int segments) {
        // actual servlet path
        String spath = httpRequest.getServletPath();
        // find this Resource path (remove some segments from it)
        String rpath = ui.getPath();
        while (segments > 0) {
            segments--;
            if (rpath.contains("/")) {
                rpath = rpath.substring(0, rpath.lastIndexOf('/'));
            }
        }
        HttpServletRequest wrapper;
        if (rpath.length() == 0) {
            // no resource path to fake
            wrapper = httpRequest;
        } else {
            // this gives the pretend servlet path
            final String pspath = spath + rpath;
            // wrap HttpServletRequest to pretend to have this servlet path
            wrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getServletPath() {
                    return pspath;
                }
            };
        }
        return new ServletRequestContext(provider, wrapper);
    }

    protected CollectionAdapter getAbderaCollectionAdapter(
            RequestContext requestContext) {
        return provider.getWorkspaceManager(requestContext).getCollectionAdapter(
                requestContext);
    }

    protected Response getAbderaFeed(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        return Response.ok(adapter.getFeed(requestContext)).build();
    }

    protected Response getAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        return Response.ok(adapter.getEntry(requestContext)).build();
    }

    protected Response getAbderaPostFeed(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        return Response.ok(adapter.postEntry(requestContext)).build();
    }

    @GET
    @Produces(Atom.MEDIA_TYPE_ATOM_SERVICE)
    @Path("repository")
    public Response doGetRepository(@Context HttpServletRequest httpRequest) {
        RequestContext requestContext = getRequestContext(1);
        ResponseContext responseContext = provider.getServiceDocument(requestContext);
        return Response.ok(responseContext).build();
    }

    @GET
    @Produces(Atom.MEDIA_TYPE_ATOM_FEED)
    @Path("types")
    public Response doGetTypes() {
        return getAbderaFeed(1);
    }

    @GET
    @Produces(Atom.MEDIA_TYPE_ATOM_FEED)
    @Path("children/{objectid}")
    public Response doGetChildren() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaFeed(2);
    }

    @GET
    @Produces(Atom.MEDIA_TYPE_ATOM_ENTRY)
    @Path("object/{objectid}")
    public Response doGetObject() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaEntry(2);
    }

    @GET
    @Path("file/{objectid}")
    public Response doGetFile() {
        // objectid decoded by Abdera getCollectionAdapter
        RequestContext requestContext = getRequestContext(2);
        AbstractCollectionAdapter adapter = (AbstractCollectionAdapter) getAbderaCollectionAdapter(requestContext);
        ResponseContext responseContext = adapter.getMedia(requestContext);
        String contentType = responseContext.getHeader("Content-Type");
        return Response.ok(responseContext).type(contentType).build();
    }

    @POST
    @Consumes("application/cmisquery+xml")
    @Produces(Atom.MEDIA_TYPE_ATOM_FEED)
    @Path("query")
    public Response doPostQuery() {
        return getAbderaPostFeed(1);
    }

}
