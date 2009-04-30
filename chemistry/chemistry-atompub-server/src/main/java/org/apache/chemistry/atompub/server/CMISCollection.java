/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.atompub.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.chemistry.repository.Repository;

/**
 * Base abstract class for the CMIS collections.
 *
 * @author Florent Guillaume
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

    public String getRepositoryLink(RequestContext request) {
        return request.absoluteUrlFor(TargetType.TYPE_SERVICE, null);
    }

    public String getTypeLink(String tid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "type");
        params.put("id", tid);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

    public String getChildrenLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "children");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

    public String getDescendantsLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "descendants");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
    }

    public String getParentsLink(String fid, RequestContext request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("entrytype", "parents");
        params.put("id", fid);
        return request.absoluteUrlFor(TargetType.TYPE_ENTRY, params);
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
