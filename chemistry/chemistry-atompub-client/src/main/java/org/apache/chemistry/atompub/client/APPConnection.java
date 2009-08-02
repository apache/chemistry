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
 *     Ugo Cei, Sourcesense
 */
package org.apache.chemistry.atompub.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Rendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.atompub.Atom;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.connector.Connector;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlProperty;

/**
 *
 */
public class APPConnection implements Connection, SPI {

    public static final int DEFAULT_MAX_CHILDREN = 20;

    protected APPFolder root;

    protected Connector connector;

    protected APPRepository repository;

    protected Map<Class<?>, Object> singletons = new Hashtable<Class<?>, Object>();

    public APPConnection(APPRepository repo) {
        this.repository = repo;
        this.connector = repo.cm.getConnector(); // TODO clone connector to be
        // able to use different logins
    }

    public Connection getConnection() {
        return this;
    }

    public SPI getSPI() {
        return this;
    }

    public void close() {
        // do nothing? or clear login?
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    public Repository getRepository() {
        return repository;
    }

    public Folder getRootFolder() {
        if (root == null) {
            root = (APPFolder) getObject(repository.info.getRootFolderId());
        }
        return root;
    }

    // not in API

    public Connector getConnector() {
        return connector;
    }

    public String getBaseUrl() {
        return repository.cm.getBaseUrl();
    }

    /*
     * ----- Factories -----
     */

    public Document newDocument(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        APPObjectEntry entry = newObjectEntry(typeId);
        if (folder != null) {
            entry.addLink(Atom.LINK_UP,
                    ((APPFolder) folder).entry.getEditLink());
        }
        return new APPDocument(entry, type);
    }

    public Folder newFolder(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException(typeId);
        }
        APPObjectEntry entry = newObjectEntry(typeId);
        if (folder != null) {
            entry._setValue(Property.PARENT_ID, folder.getId());
            entry.addLink(Atom.LINK_UP,
                    ((APPFolder) folder).entry.getEditLink());
        }
        return new APPFolder(entry, type);
    }

    public Relationship newRelationship(String typeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Policy newPolicy(String typeId, Folder folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public APPObjectEntry newObjectEntry(String typeId) {
        Map<String, XmlProperty> map = new HashMap<String, XmlProperty>();
        Type type = getRepository().getType(typeId);
        XmlProperty p = new XmlProperty(
                type.getPropertyDefinition(Property.TYPE_ID), typeId);
        map.put(p.getId(), p);
        return new APPObjectEntry(this, map, null);
    }

    public ObjectId newObjectId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Navigation Services -----
     */

    /**
     * Accumulates the descendant folders into a list recursively.
     */
    protected void accumulateFolders(ObjectId folder, int depth, String filter,
            boolean includeAllowableActions, List<ObjectEntry> list) {
        List<ObjectEntry> children = getChildren(folder, filter,
                includeAllowableActions, false, Integer.MAX_VALUE, 0, null,
                new boolean[1]);
        for (ObjectEntry child : children) {
            if (child.getBaseType() != BaseType.FOLDER) {
                continue;
            }
            list.add(child);
            if (depth > 1) {
                accumulateFolders(child, depth - 1, filter,
                        includeAllowableActions, list);
            }
        }
    }

    // TODO use foldertree feed
    public List<ObjectEntry> getFolderTree(ObjectId folder, int depth,
            String filter, boolean includeAllowableActions) {
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulateFolders(folder, depth, filter, includeAllowableActions, list);
        return list;
    }

    /**
     * Accumulates the descendants into a list recursively.
     */
    protected void accumulateDescendants(ObjectId folder, int depth,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy, List<ObjectEntry> list) {
        // TODO deal with paging properly
        List<ObjectEntry> children = getChildren(folder, filter,
                includeAllowableActions, includeRelationships,
                Integer.MAX_VALUE, 0, orderBy, new boolean[1]);
        for (ObjectEntry child : children) {
            list.add(child);
            if (depth > 1 && child.getBaseType() == BaseType.FOLDER) {
                accumulateDescendants(child, depth - 1, filter,
                        includeAllowableActions, includeRelationships, orderBy,
                        list);
            }
        }
    }

    // TODO use descendants feed
    public List<ObjectEntry> getDescendants(ObjectId folder, int depth,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy) {
        // TODO includeRelationship, includeAllowableActions, orderBy
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulateDescendants(folder, depth, filter, includeAllowableActions,
                includeRelationships, orderBy, list);
        return list;
    }

    public List<ObjectEntry> getChildren(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            int maxItems, int skipCount, String orderBy, boolean[] hasMoreItems) {
        // TODO filter, includeRelationship, includeAllowableActions, orderBy
        if (maxItems <= 0) {
            maxItems = DEFAULT_MAX_CHILDREN;
        }
        if (skipCount < 0) {
            skipCount = 0;
        }

        String href = getObjectEntry(folder).getLink(Atom.LINK_DOWN);
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        List<ObjectEntry> feed = resp.getObjectFeed(new ReadContext(this));

        List<ObjectEntry> result = new LinkedList<ObjectEntry>();
        hasMoreItems[0] = false;
        boolean done = false;
        for (ObjectEntry entry : feed) {
            // skip
            if (skipCount > 0) {
                skipCount--;
                continue;
            }
            // entry is ok
            if (done) {
                hasMoreItems[0] = true;
                break;
            }
            result.add(entry);
            if (result.size() >= maxItems) {
                done = true;
                // don't break now, we still have to find out if there are more
                // non-filtered entries to fill in hasMoreItems
            }
        }
        return result;
    }

    public ObjectEntry getFolderParent(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {
        // TODO filter, includeRelationship, includeAllowableActions
        APPObjectEntry current = getObjectEntry(folder);
        if (!current.getBaseType().equals(BaseType.FOLDER)) {
            throw new IllegalArgumentException("Not a folder: " + folder);
        }
        String rootId = current.connection.getRootFolder().getId();
        if (current.getId().equals(rootId)) {
            return null;
        }
        String href = current.getLink(Atom.LINK_UP);
        if (href == null) {
            return null;
        }
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId object,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships) {
        // TODO filter, includeRelationship, includeAllowableActions
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(Atom.LINK_UP);
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return resp.getObjectFeed(new ReadContext(this));
    }

    public Collection<ObjectEntry> getCheckedoutDocuments(ObjectId folder,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Object Services -----
     */

    /*
     * TODO hardcoded Chemistry URL pattern here...
     */
    protected APPObjectEntry getObjectEntry(ObjectId objectId) {
        if (objectId instanceof APPObjectEntry) {
            return ((APPObjectEntry) objectId);
        }
        String href;
        URITemplate uriTemplate = repository.getURITemplate(CMIS.URITMPL_ENTRY_BY_ID);
        if (uriTemplate != null) {
            // use entry-by-id URI template
            href = uriTemplate.template;
            // TODO proper URI template syntax
            href = href.replace("{id}", objectId.getId());
        } else {
            // TODO do a search (maybe 4 searches as base type unknown)

            // XXX hardcoded Chemistry URL pattern
            href = repository.getCollectionHref(CMIS.COL_ROOT_CHILDREN);
            if (href.matches(".*/children/[0-9a-f-]{36}")) {
                href = href.substring(0, href.length() - "/children/".length()
                        - 36);
            } else {
                if (href.matches(".*/children$")) {
                    href = href.substring(0, href.length()
                            - "/children".length());
                } else {
                    throw new AssertionError(href);
                }
            }
            href += "/object/" + objectId.getId();
        }
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    public CMISObject getObject(ObjectId object) {
        APPObjectEntry entry = getObjectEntry(object);
        Type type = getRepository().getType(entry.getTypeId());
        switch (entry.getBaseType()) {
        case DOCUMENT:
            return new APPDocument(entry, type);
        case FOLDER:
            return new APPFolder(entry, type);
        case POLICY:
            throw new UnsupportedOperationException("Not yet implemented");
        case RELATIONSHIP:
            throw new UnsupportedOperationException("Not yet implemented");
        default:
            throw new AssertionError(entry.getBaseType());
        }
    }

    public ObjectId createDocument(String typeId,
            Map<String, Serializable> properties, ObjectId folder,
            ContentStream contentStream, VersioningState versioningState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createFolder(String typeId,
            Map<String, Serializable> properties, ObjectId folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createRelationship(String typeId,
            Map<String, Serializable> properties, ObjectId source,
            ObjectId target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createPolicy(String typeId,
            Map<String, Serializable> properties, ObjectId folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<String> getAllowableActions(ObjectId object, String asUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getProperties(ObjectId object, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<Rendition> getRenditions(ObjectId object, String filter,
            int maxItems, int skipCount) {
        return Collections.emptyList();
    }

    public boolean hasContentStream(ObjectId document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ContentStream getContentStream(ObjectId object,
            String contentStreamId) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId setContentStream(ObjectId document, boolean overwrite,
            ContentStream contentStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId deleteContentStream(ObjectId document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectId object, boolean allVersions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectId> deleteTree(ObjectId folder, Unfiling unfiling,
            boolean continueOnFailure) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void addObjectToFolder(ObjectId object, ObjectId folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removeObjectFromFolder(ObjectId object, ObjectId folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Discovery Services -----
     */

    public Collection<ObjectEntry> query(String statement,
            boolean searchAllVersions, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        String href = repository.getCollectionHref(CMIS.COL_QUERY);
        Response resp = connector.postQuery(new Request(href), statement,
                searchAllVersions, maxItems, skipCount, includeAllowableActions);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        List<ObjectEntry> objects = resp.getObjectFeed(new ReadContext(this));
        return objects;
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> res = query(statement, searchAllVersions,
                false, false, -1, 0, hasMoreItems);
        List<CMISObject> objects = new ArrayList<CMISObject>(res.size());
        for (ObjectEntry e : res) {
            objects.add(APPObject.construct((APPObjectEntry) e));
        }
        return objects;
    }

    public Iterator<ObjectEntry> getChangeLog(String changeLogToken,
            boolean includeProperties, int maxItems, boolean[] hasMoreItems,
            String[] lastChangeLogToken) {
        hasMoreItems[0] = false;
        lastChangeLogToken[0] = null;
        return Collections.<ObjectEntry> emptyList().iterator();
    }

    /*
     * ----- Versioning Services -----
     */

    public ObjectId checkOut(ObjectId document, boolean[] contentCopied) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(ObjectId document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId checkIn(ObjectId document, boolean major,
            Map<String, Serializable> properties, ContentStream contentStream,
            String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean major, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Relationship Services -----
     */

    public List<ObjectEntry> getRelationships(ObjectId object,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes, String filter,
            String includeAllowableActions, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Policy Services -----
     */

    public void applyPolicy(ObjectId policy, ObjectId object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(ObjectId policy, ObjectId object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId object,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
