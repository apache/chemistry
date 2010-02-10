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

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.Paging;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.impl.simple.SimpleTypeManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An APP client repository proxy
 */
public class APPRepository implements Repository {

    private static final Log log = LogFactory.getLog(APPRepository.class);

    protected final APPContentManager cm;

    protected final Connector connector;

    protected RepositoryInfo info;

    protected String id;

    protected TypeManager typeManager;

    protected final Map<String, String> collections = new HashMap<String, String>();

    protected final Map<String, URITemplate> uriTemplates = new HashMap<String, URITemplate>();

    public APPRepository(APPContentManager cm) {
        this(cm, null);
    }

    public APPRepository(APPContentManager cm, RepositoryInfo info) {
        this.cm = cm;
        connector = new Connector(cm.getClient(), new ReadContext(this));
        this.info = info;
    }

    public void setInfo(RepositoryInfo info) {
        this.info = info;
    }

    public APPContentManager getContentManager() {
        return cm;
    }

    public HttpClient client() {
        return cm.getClient();
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

    public PropertyDefinition getPropertyDefinition(String id) {
        loadTypes();
        return typeManager.getPropertyDefinition(id);
    }

    public Collection<Type> getTypes() {
        loadTypes();
        return typeManager.getTypes();
    }

    public Collection<Type> getTypeDescendants(String typeId) {
        loadTypes();
        return typeManager.getTypeDescendants(typeId);
    }

    public ListPage<Type> getTypeChildren(String typeId,
            boolean includePropertyDefinitions, Paging paging) {
        loadTypes();
        return typeManager.getTypeChildren(typeId, includePropertyDefinitions,
                paging);
    }

    public Collection<Type> getTypeDescendants(String typeId, int depth,
            boolean includePropertyDefinitions) {
        loadTypes();
        return typeManager.getTypeDescendants(typeId, depth,
                includePropertyDefinitions);
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

    protected synchronized void loadTypes() {
        if (typeManager != null) {
            return;
        }
        try {
            // the base types
            String href = getCollectionHref(AtomPubCMIS.COL_TYPES);
            if (href == null) {
                throw new IllegalArgumentException(
                        "Invalid CMIS repository. No types children collection defined");
            }
            typeManager = new SimpleTypeManager();
            // for each base type read all the descendants
            Collection<Type> baseTypes = readTypes(href).getTypes();
            for (Type type : baseTypes) {
                if (!BaseType.ALL_IDS.contains(type.getId())) {
                    // not a base type, shouldn't be there
                    continue;
                }
                typeManager.addType(type);
                href = ((APPType) type).getLink(AtomPub.LINK_DOWN,
                        AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
                if (href == null) {
                    // missing descendants types link
                    log.error("Type " + type.getId()
                            + " is missing descendants link");
                    continue;
                }
                TypeManager rr = readTypes(href);
                Collection<Type> subTypes = rr.getTypes();
                for (Type t : subTypes) {
                    typeManager.addType(t);
                }
            }
        } catch (Exception e) { // TODO how to handle exceptions?
            throw new RuntimeException("Failed to load repository types for "
                    + getName(), e);
        }
    }

    protected TypeManager readTypes(String href) throws Exception {
        href = includePropertyDefinitionsInURI(href);
        return connector.getTypeFeed(href, true);
    }

    protected static String includePropertyDefinitionsInURI(String href) {
        if (!href.contains(AtomPubCMIS.PARAM_INCLUDE_PROPERTY_DEFINITIONS)) {
            char sep = href.contains("?") ? '&' : '?';
            href += sep + AtomPubCMIS.PARAM_INCLUDE_PROPERTY_DEFINITIONS
                    + "=true";
        }
        return href;
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
