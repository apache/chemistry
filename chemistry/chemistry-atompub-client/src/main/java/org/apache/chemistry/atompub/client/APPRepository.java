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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;

/**
 * An APP client repository proxy
 */
public class APPRepository implements Repository {

    protected APPContentManager cm;

    protected RepositoryInfo info;

    protected String id;

    protected TypeManager typeManager;

    protected Map<String, String> collections = new HashMap<String, String>();

    protected Map<String, URITemplate> uriTemplates = new HashMap<String, URITemplate>();

    public APPRepository(APPContentManager cm) {
        this(cm, null);
    }

    public APPRepository(APPContentManager cm, RepositoryInfo info) {
        this.cm = cm;
        this.info = info;
    }

    public void setInfo(RepositoryInfo info) {
        this.info = info;
    }

    public ContentManager getContentManager() {
        return cm;
    }

    public String getId() {
        if (id == null) {
            id = info.getId();
        }
        return id;
    }

    public String getName() {
        return info.getName();
    }

    public URI getThinClientURI() {
        return info.getThinClientURI();
    }

    public SPI getSPI() {
        loadTypes();
        return new APPConnection(this);
    }

    public Connection getConnection(Map<String, Serializable> parameters) {
        loadTypes();
        return new APPConnection(this);
    }

    public void addCollection(String type, String href) {
        collections.put(type, href);
    }

    public void addURITemplate(URITemplate uriTemplate) {
        uriTemplates.put(uriTemplate.type, uriTemplate);
    }

    public RepositoryInfo getInfo() {
        return info;
    }

    public void addType(Type type) {
        throw new UnsupportedOperationException("Cannot add types");
    }

    public Type getType(String typeId) {
        loadTypes();
        return typeManager.getType(typeId);
    }

    public Collection<Type> getTypes(String typeId) {
        loadTypes();
        return typeManager.getTypes(typeId);
    }

    public Collection<Type> getTypes(String typeId, int depth,
            boolean includePropertyDefinitions) {
        loadTypes();
        return typeManager.getTypes(typeId, depth, includePropertyDefinitions);
    }

    public String getCollectionHref(String type) {
        return collections.get(type);
    }

    public URITemplate getURITemplate(String type) {
        return uriTemplates.get(type);
    }

    /** type API */

    public void addType(APPType type) {
        typeManager.addType(type);
    }

    protected void loadTypes() {
        if (typeManager != null) {
            return;
        }
        try {
            String href = getCollectionHref(AtomPubCMIS.COL_TYPES);
            if (href == null) {
                throw new IllegalArgumentException(
                        "Invalid CMIS repository. No types children collection defined");
            }
            // TODO lazy load property definition
            Request req = new Request(href + "?includePropertyDefinitions=true");
            Response resp = cm.getConnector().get(req);
            if (!resp.isOk()) {
                throw new ContentManagerException(
                        "Remote server returned error code: "
                                + resp.getStatusCode());
            }
            InputStream in = resp.getStream();
            try {
                typeManager = TypeFeedReader.INSTANCE.read(
                        new ReadContext(this), in);
            } finally {
                in.close();
            }
        } catch (Exception e) { // TODO how to handle exceptions?
            throw new RuntimeException("Failed to load repository types for "
                    + getName(), e);
        }
    }

    /*
     * Subclasses should override this.
     */
    public <T> T getExtension(Class<T> clazz) {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + getName() + ')';
    }

}
