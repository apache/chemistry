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
 *     Florian Roth, In-integrierte Informationssysteme
 */
package org.apache.chemistry.atompub.server.jaxrs;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.ServletRequestContext;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.server.CMISChildrenCollection;
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
        String rpath = ui.getPath(false);
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

    protected Response getResponse(ResponseContext responseContext) {
        ResponseBuilder b = Response.status(responseContext.getStatus());
        b.entity(responseContext);
        String contentType = responseContext.getHeader("Content-Type");
        b.type(contentType);
        IRI location = responseContext.getLocation();
        if (location != null) {
            try {
                b.location(location.toURI());
            } catch (Exception e) {
                log.error("Bad Location: " + location, e);
            }
        }
        IRI contentLocation = responseContext.getContentLocation();
        if (contentLocation != null) {
            try {
                b.contentLocation(contentLocation.toURI());
            } catch (Exception e) {
                log.error("Bad Content-Location: " + contentLocation, e);
            }
        }
        return b.build();
    }

    protected Response getAbderaFeed(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return getResponse(adapter.getFeed(requestContext));
    }

    protected Response getAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return getResponse(adapter.getEntry(requestContext));
    }

    protected Response postAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return getResponse(adapter.postEntry(requestContext));
    }

    protected Response putAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return getResponse(adapter.putEntry(requestContext));
    }

    protected Response deleteAbderaEntry(int skipSegments) {
        RequestContext requestContext = getRequestContext(skipSegments);
        CollectionAdapter adapter = getAbderaCollectionAdapter(requestContext);
        if (adapter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return getResponse(adapter.deleteEntry(requestContext));
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_SERVICE)
    @Path("repository")
    public Response doGetRepository(@Context HttpServletRequest httpRequest) {
        RequestContext requestContext = getRequestContext(1);
        return getResponse(provider.getServiceDocument(requestContext));
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("typechildren")
    public Response doGetTypeChildrenAll() {
        return getAbderaFeed(1);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("typechildren/{typeid}")
    public Response doGetTypeChildren() {
        return getAbderaFeed(2);
    }

    @GET
    // TODO produces tree
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("typedescendants/{typeid}")
    public Response doGetTypeDescendants() {
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

    @POST
    @Consumes( { AtomPub.MEDIA_TYPE_ATOM_ENTRY,
            // need for RESTeasy:
            AtomPub.MEDIA_TYPE_ATOM,
            AtomPub.MEDIA_TYPE_ATOM_ENTRY + ";charset=UTF-8" })
    @Path("children/{objectid}")
    public Response doPostChildren() {
        // objectid decoded by Abdera getCollectionAdapter
        return postAbderaEntry(2);
    }

    @GET
    @Produces(AtomPubCMIS.MEDIA_TYPE_CMIS_TREE)
    @Path("descendants/{objectid}")
    public Response doGetDescendants() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaFeed(2);
    }

    @DELETE
    @Path("descendants/{objectid}")
    public Response doDeleteDescendants() {
        // objectid decoded by Abdera getCollectionAdapter
        return deleteAbderaEntry(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("foldertree/{objectid}")
    public Response doGetFolderTree() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaFeed(2);
    }

    @DELETE
    @Path("foldertree/{objectid}")
    public Response doDeleteFolderTree() {
        // objectid decoded by Abdera getCollectionAdapter
        return deleteAbderaEntry(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
    @Path("object")
    public Response doGetObjectNoId() {
        return getAbderaEntry(1);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
    @Path("object/{objectid}")
    public Response doGetObject() {
        // objectid decoded by Abdera getCollectionAdapter
        return getAbderaEntry(2);
    }

    // TODO should we really accept AtomPub.MEDIA_TYPE_ATOM ?
    @PUT
    @Consumes( { AtomPub.MEDIA_TYPE_ATOM_ENTRY, AtomPub.MEDIA_TYPE_ATOM })
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
    @Path("object/{objectid}")
    public Response doPutObject() {
        // objectid decoded by Abdera getCollectionAdapter
        return putAbderaEntry(2);
    }

    @DELETE
    @Path("object/{objectid}")
    public Response deleteObject() {
        // objectid decoded by Abdera getCollectionAdapter
        return deleteAbderaEntry(2);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_ENTRY)
    @Path("path/{path:.*}")
    public Response doGetObjectByPath(@Encoded @PathParam("path") String path) {
        // don't use the path argument but refetch it from the UriInfo
        // to ensure that an initial slash isn't swallowed, which is important
        // for the later segments count used in getRequestContext
        path = ui.getPathParameters(false).get("path").get(0);
        int skipSegments = 2; //
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                skipSegments++;
            }
        }
        return getAbderaEntry(skipSegments);
    }

    @GET
    @Path("file/{objectid}")
    public Response doGetFile() {
        // objectid decoded by Abdera getCollectionAdapter
        RequestContext requestContext = getRequestContext(2);
        CMISChildrenCollection adapter = (CMISChildrenCollection) getAbderaCollectionAdapter(requestContext);
        return getResponse(adapter.getMedia(requestContext));
    }

    @PUT
    @Path("file/{objectid}")
    public Response doPutFile() {
        // objectid decoded by Abdera getCollectionAdapter
        RequestContext requestContext = getRequestContext(2);
        CMISChildrenCollection adapter = (CMISChildrenCollection) getAbderaCollectionAdapter(requestContext);
        return getResponse(adapter.putMedia(requestContext));
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("query")
    public Response doGetQuery() {
        return getAbderaEntry(1);
    }

    @POST
    @Consumes(AtomPubCMIS.MEDIA_TYPE_CMIS_QUERY)
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("query")
    public Response doPostQuery() {
        return postAbderaEntry(1);
    }

    @GET
    @Produces(AtomPub.MEDIA_TYPE_ATOM_FEED)
    @Path("checkedout")
    public Response doGetCheckedOut() {
        return getAbderaEntry(1);
    }

}
