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
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.server.CMISProvider;
import org.apache.chemistry.atompub.server.jaxrs.AbderaResource.PathMunger.ContextAndServletPath;
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

    // TODO configure somehow
    public static PathMunger pathMunger;

    public interface PathMunger {
        public static class ContextAndServletPath {
            public String contextPath;

            public String servletPath;
        }

        ContextAndServletPath munge(HttpServletRequest request,
                String contextPath, String servletPath);
    }

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
     * Wrapping is needed to fixup the servlet path to include this Resource's
     * path.
     * <p>
     * We need to pass an explicit number of segments because
     * UriInfo.getMatchedURIs is buggy for RESTEasy
     * (https://jira.jboss.org/jira/browse/RESTEASY-100)
     *
     * @param segments the number of segments of the method invoking this, used
     *            to determine the Resource path
     */
    protected ServletRequestContext getRequestContext(int segments) {
        // actual context & servlet path
        String cpath = httpRequest.getContextPath();
        String spath = httpRequest.getServletPath();
        // find this Resource path (remove some segments from it)
        String rpath = ui.getPath();
        while (segments > 0) {
            segments--;
            if (rpath.contains("/")) {
                rpath = rpath.substring(0, rpath.lastIndexOf('/'));
            }
        }
        // find the pretend context & servlet path
        final String contextPath;
        final String servletPath;
        final String resourcePath = rpath;
        if (pathMunger == null) {
            contextPath = cpath;
            servletPath = spath;
        } else {
            ContextAndServletPath cs = pathMunger.munge(httpRequest, cpath,
                    spath);
            contextPath = cs.contextPath;
            servletPath = cs.servletPath;
        }
        HttpServletRequest wrapper;
        if (cpath.equals(contextPath)
                && spath.equals(servletPath + resourcePath)) {
            // no path to fake
            wrapper = httpRequest;
        } else {
            // wrap HttpServletRequest to pretend to have this context & servlet
            // path
            wrapper = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getContextPath() {
                    return contextPath;
                }

                @Override
                public String getServletPath() {
                    return servletPath + resourcePath;
                }

                @Override
                public String getRequestURI() {
                    String uri = super.getRequestURI();
                    String cs = super.getContextPath() + super.getServletPath();
                    // strip original context + servlet, add our own
                    return contextPath + servletPath
                            + uri.substring(cs.length());
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
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResponseContext responseContext = adapter.getFeed(requestContext);
        return Response.status(responseContext.getStatus()).entity(
                responseContext).build();
    }

    protected Response getAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResponseContext responseContext = adapter.getEntry(requestContext);
        return Response.status(responseContext.getStatus()).entity(
                responseContext).build();
    }

    protected Response getAbderaPostFeed(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResponseContext responseContext = adapter.postEntry(requestContext);
        return Response.status(responseContext.getStatus()).entity(
                responseContext).build();
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_SERVICE)
    @Path("repository")
    public Response doGetRepository(@Context HttpServletRequest httpRequest) {
        RequestContext requestContext = getRequestContext(1);
        ResponseContext responseContext = provider.getServiceDocument(requestContext);
        return Response.status(responseContext.getStatus()).entity(
                responseContext).build();
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("types")
    public Response doGetTypes() {
        return getAbderaFeed(1);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("typesdescendants")
    public Response doGetTypesDescendantsAll() {
        return getAbderaFeed(1);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("typesdescendants/{typeid}")
    public Response doGetTypesDescendantsTyped() {
        return getAbderaFeed(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
    @Path("type/{typeid}")
    public Response doGetType() {
        // typeid decoded by Abdera getCollectionAdapter
        return getAbderaEntry(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("children/{objectid}")
    public Response doGetChildren() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaFeed(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
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
        return Response.status(responseContext.getStatus()).entity(
                responseContext).type(contentType).build();
    }

    @POST
    @Consumes(AtomPubCMIS.MEDIA_TYPE_CMIS_QUERY)
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("query")
    public Response doPostQuery() {
        return getAbderaPostFeed(1);
    }

}
