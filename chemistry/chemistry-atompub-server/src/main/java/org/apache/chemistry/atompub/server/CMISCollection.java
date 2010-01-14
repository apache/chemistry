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
package org.apache.chemistry.atompub.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.AtomPub;

/**
 * Base abstract class for the CMIS collections.
 */
public abstract class CMISCollection<T> extends
        AbstractEntityCollectionAdapter<T> {

    protected final String type;

    protected final String name;

    protected final String id;

    protected final Repository repository;

    public CMISCollection(String type, String name, String id,
            Repository repository) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.repository = repository;
    }

    public String getType() {
        return type;
    }

    /*
     * ----- Helpers -----
     */

    public static int getParameter(RequestContext request, String name, int def) {
        String value = request.getTarget().getParameter(name);
        return value == null ? def : Integer.parseInt(value);
    }

    public static boolean getParameter(RequestContext request, String name,
            boolean def) {
        String value = request.getTarget().getParameter(name);
        return value == null ? def : Boolean.parseBoolean(value);
    }

    /*
     * ----- Transactional -----
     */

    @Override
    public void start(RequestContext request) throws ResponseContextException {
    }

    @Override
    public void end(RequestContext request, ResponseContext response) {
    }

    @Override
    public void compensate(RequestContext request, Throwable t) {
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    public ResponseContext buildGetFeedResponse(Feed feed) {
        ResponseContext rc = super.buildGetFeedResponse(feed);
        rc.setContentType(AtomPub.MEDIA_TYPE_ATOM_FEED);
        return rc;
    }

    // called by AbstractProvider.process if unknown TargetType
    @Override
    public ResponseContext extensionRequest(RequestContext request) {
        return ProviderHelper.notallowed(request,
                ProviderHelper.getDefaultMethods(request));
    }

    @Override
    public String getHref(RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", name);
        params.put("id", id); // id may be null
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[0];
        // return new String[] { "application/atom+xml;type=entry" };
    }

    /*
     * ----- Utilities -----
     */

    public String getServiceLink(RequestContext request) {
        return request.absoluteUrlFor(TargetType.TYPE_SERVICE, null);
    }

    public String getTypeLink(String tid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "type");
        params.put("id", tid);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

    public String getTypeChildrenLink(String tid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "typechildren");
        params.put("id", tid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public static String getTypeDescendantsLink(String tid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "typedescendants");
        params.put("id", tid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public String getChildrenLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "children");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public String getDescendantsLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "descendants");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public static String getFolderTreeLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "foldertree");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public String getParentsLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "parents");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public String getCheckedOutLink(RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("collection", "checkedout");
        return request.absoluteUrlFor(TargetType.TYPE_COLLECTION, params);
    }

    public String getObjectLink(String id, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "object");
        params.put("id", id);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

    public String getMediaLink(String id, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "file");
        params.put("id", id);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

}
