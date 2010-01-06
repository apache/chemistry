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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.ObjectNotFoundException;
import org.apache.chemistry.Paging;
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
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.connector.Connector;
import org.apache.chemistry.atompub.client.connector.Request;
import org.apache.chemistry.atompub.client.connector.Response;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlProperty;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.httpclient.HttpStatus;

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
            entry.addLink(AtomPub.LINK_UP,
                    ((APPFolder) folder).entry.getEditLink(),
                    AtomPub.MEDIA_TYPE_ATOM_ENTRY);
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
            entry.addLink(AtomPub.LINK_UP,
                    ((APPFolder) folder).entry.getEditLink(),
                    AtomPub.MEDIA_TYPE_ATOM_ENTRY);
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
        return new SimpleObjectId(id);
    }

    /*
     * ----- Navigation Services -----
     */

    /**
     * Accumulates the descendant folders into a list recursively.
     */
    protected void accumulateFolders(ObjectId folder, int depth,
            Inclusion inclusion, List<ObjectEntry> list) {
        List<ObjectEntry> children = getChildren(folder, inclusion, null,
                new Paging(Integer.MAX_VALUE, 0));
        for (ObjectEntry child : children) {
            if (child.getBaseType() != BaseType.FOLDER) {
                continue;
            }
            list.add(child);
            if (depth > 1) {
                accumulateFolders(child, depth - 1, inclusion, list);
            }
        }
    }

    // TODO use foldertree feed
    public List<ObjectEntry> getFolderTree(ObjectId folder, int depth,
            Inclusion inclusion) {
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulateFolders(folder, depth, inclusion, list);
        return list;
    }

    public List<ObjectEntry> getDescendants(ObjectId folder, int depth,
            String orderBy, Inclusion inclusion) {
        String href = getObjectEntry(folder).getLink(AtomPub.LINK_DOWN,
                AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        Request req = new Request(href);
        req.setParameter(AtomPubCMIS.PARAM_DEPTH, Integer.toString(depth));
        if (orderBy != null) {
            req.setParameter(AtomPubCMIS.PARAM_ORDER_BY, orderBy);
        }
        if (inclusion != null) {
            if (inclusion.properties != null) {
                req.setParameter(AtomPubCMIS.PARAM_FILTER, inclusion.properties);
            }
            if (inclusion.renditions != null) {
                req.setParameter(AtomPubCMIS.PARAM_RENDITION_FILTER,
                        inclusion.renditions);
            }
            if (inclusion.relationships != null) {
                req.setParameter(
                        AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS,
                        RelationshipDirection.toInclusion(inclusion.relationships));
            }
            req.setParameter(AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS,
                    Boolean.toString(inclusion.allowableActions));
            req.setParameter(AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS,
                    Boolean.toString(inclusion.policies));
            req.setParameter(AtomPubCMIS.PARAM_INCLUDE_ACL,
                    Boolean.toString(inclusion.acls));
        }
        Response resp = connector.get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return resp.getObjectFeed(new ReadContext(this));
    }

    public ListPage<ObjectEntry> getChildren(ObjectId folder,
            Inclusion inclusion, String orderBy, Paging paging) {
        // TODO filter, includeRelationship, includeAllowableActions, orderBy
        String href = getObjectEntry(folder).getLink(AtomPub.LINK_DOWN,
                AtomPub.MEDIA_TYPE_ATOM_FEED);
        Request req = new Request(href);
        if (paging != null) {
            req.setParameter(AtomPubCMIS.PARAM_MAX_ITEMS,
                    Integer.toString(paging.maxItems));
            req.setParameter(AtomPubCMIS.PARAM_SKIP_COUNT,
                    Integer.toString(paging.skipCount));
        }
        Response resp = connector.get(req);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return resp.getObjectFeed(new ReadContext(this));
    }

    public ObjectEntry getFolderParent(ObjectId folder, String filter) {
        // TODO filter
        APPObjectEntry current = getObjectEntry(folder);
        if (!current.getBaseType().equals(BaseType.FOLDER)) {
            throw new IllegalArgumentException("Not a folder: " + folder);
        }
        String rootId = current.connection.getRootFolder().getId();
        if (current.getId().equals(rootId)) {
            return null;
        }
        String href = current.getLink(AtomPub.LINK_UP,
                AtomPub.MEDIA_TYPE_ATOM_ENTRY);
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
            String filter) {
        // TODO filter
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_UP);
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return resp.getObjectFeed(new ReadContext(this));
    }

    public ListPage<ObjectEntry> getCheckedOutDocuments(ObjectId folder,
            Inclusion inclusion, Paging paging) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Object Services -----
     */

    // TODO add hints about what we'd like to fetch from the entry (stream,
    // props, etc.)
    protected APPObjectEntry getObjectEntry(ObjectId objectId) {
        if (objectId instanceof APPObjectEntry) {
            return (APPObjectEntry) objectId;
        }
        URITemplate uriTemplate = repository.getURITemplate(AtomPubCMIS.URITMPL_OBJECT_BY_ID);
        String href = uriTemplate.template;
        href = replace(href, AtomPubCMIS.PARAM_ID, objectId.getId());
        href = replace(href, AtomPubCMIS.PARAM_FILTER, "");
        href = replace(href, AtomPubCMIS.PARAM_RENDITION_FILTER, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ACL, "");
        Response resp = connector.get(new Request(href));
        if (resp.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            throw new ObjectNotFoundException(objectId.getId());
        }
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    protected String replace(String template, String param, String value) {
        return template.replace('{' + param + '}', value);
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

    protected String getPostHref(ObjectId parentId) {
        APPObjectEntry parentEntry = getObjectEntry(parentId);
        String href = parentEntry.getLink(AtomPub.LINK_DOWN,
                AtomPub.MEDIA_TYPE_ATOM_FEED);
        if (href == null) {
            href = parentEntry.getLink(AtomPub.LINK_DOWN,
                    AtomPub.MEDIA_TYPE_ATOM);
            if (href == null) {
                throw new IllegalArgumentException(
                        "Cannot create entry: no 'down' link present");
            }
        }
        return href;
    }

    protected APPObjectEntry createObject(String postHref,
            Map<String, Serializable> properties, ContentStream contentStream,
            BaseType baseType) {
        String typeId = (String) properties.get(Property.TYPE_ID);
        if (typeId == null) {
            throw new IllegalArgumentException("Missing object type id");
        }
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != baseType) {
            throw new IllegalArgumentException(typeId);
        }
        APPObjectEntry entry = newObjectEntry(typeId);
        for (Entry<String, Serializable> en : properties.entrySet()) {
            entry._setValue(en.getKey(), en.getValue());
        }
        if (contentStream != null) {
            entry.setContentStream(contentStream);
        }

        Request req = new Request(postHref);
        req.setHeader("Content-Type", AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        Response resp = connector.postObject(req, entry);
        if (resp.getStatusCode() != 201) { // Created
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        ReadContext ctx = new ReadContext(this);
        APPObjectEntry newEntry = (APPObjectEntry) resp.getObject(ctx);
        // newEntry SHOULD be returned (AtomPub 9.2)...
        String loc = resp.getHeader("Location");
        if (loc == null) {
            throw new ContentManagerException(
                    "Remote server failed to return a Location header");
        }
        if (newEntry == null || !loc.equals(resp.getHeader("Content-Location"))) {
            // (Content-Location defined by AtomPub 9.2)
            // fetch actual new entry from Location header
            // TODO could fetch only a subset of the properties, if deemed ok
            newEntry = (APPObjectEntry) connector.getObject(ctx, loc);
            if (newEntry == null) {
                throw new ContentManagerException(
                        "Remote server failed to return an entry for Location: "
                                + loc);
            }
        }
        return newEntry;
    }

    public ObjectId createDocument(Map<String, Serializable> properties,
            ObjectId folder, ContentStream contentStream,
            VersioningState versioningState) {
        // TODO versioningState
        return createObject(getPostHref(folder), properties, contentStream,
                BaseType.DOCUMENT);
    }

    public ObjectId createFolder(Map<String, Serializable> properties,
            ObjectId folder) {
        return createObject(getPostHref(folder), properties, null,
                BaseType.FOLDER);
    }

    public ObjectId createRelationship(Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createPolicy(Map<String, Serializable> properties,
            ObjectId folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<QName> getAllowableActions(ObjectId object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getProperties(ObjectId object, Inclusion inclusion) {
        // TODO inclusion
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_SELF);
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            if (resp.getStatusCode() == 404) {
                // object not found, signature says return null
                return null;
            }
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        // TODO fill current
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    public ObjectEntry getObjectByPath(String path, Inclusion inclusion) {
        // TODO inclusion
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with / : "
                    + path);
        }
        if (!path.equals("/") && path.endsWith("/")) {
            throw new IllegalArgumentException("Path must not end with / : "
                    + path);
        }
        URITemplate uriTemplate = repository.getURITemplate(AtomPubCMIS.URITMPL_OBJECT_BY_PATH);
        if (uriTemplate == null) {
            throw new UnsupportedOperationException("Cannot get object by path");
        }
        // TODO proper encoding
        String encodedPath = path.replace(" ", "%20");
        String href = uriTemplate.template;
        href = replace(href, AtomPubCMIS.PARAM_PATH, encodedPath);
        href = replace(href, AtomPubCMIS.PARAM_FILTER, "");
        href = replace(href, AtomPubCMIS.PARAM_RENDITION_FILTER, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS, "");
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ACL, "");
        Response resp = connector.get(new Request(href));
        if (!resp.isOk()) {
            if (resp.getStatusCode() == 404) {
                // object not found, signature says return null
                return null;
            }
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    public Folder getFolder(String path) {
        APPObjectEntry entry = (APPObjectEntry) getObjectByPath(path, null);
        if (entry == null) {
            return null;
        }
        if (entry.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException("Not a folder: " + path);
        }
        Type type = getRepository().getType(entry.getTypeId());
        return new APPFolder(entry, type);
    }

    public List<Rendition> getRenditions(ObjectId object, Inclusion inclusion,
            Paging paging) {
        return Collections.emptyList();
    }

    public boolean hasContentStream(ObjectId document) {
        APPObjectEntry current = getObjectEntry(document);
        ContentStream cs = current.getContentStream();
        if (cs == null) {
            return false;
        }
        if (cs != APPObjectEntry.REMOTE_CONTENT_STREAM) {
            return true;
        }
        String href = current.getContentHref();
        return href != null;
    }

    public ContentStream getContentStream(ObjectId object,
            String contentStreamId) throws IOException {
        if (contentStreamId != null) {
            throw new UnsupportedOperationException(
                    "Cannot get non-default content stream: " + contentStreamId);
        }
        APPObjectEntry current = getObjectEntry(object);
        ContentStream cs = current.getContentStream();
        if (cs != APPObjectEntry.REMOTE_CONTENT_STREAM) {
            return cs;
        }

        // must fetch the content stream
        String href = current.getContentHref();
        if (href == null) {
            throw new RuntimeException("Object is missing content src");
        }
        Request req = new Request(href);
        Response resp = connector.get(req);
        int status = resp.getStatusCode();
        if (status == 404 || status == 409) {
            throw new ConstraintViolationException("No content stream");
        }
        if (!resp.isOk()) {
            // TODO exceptions
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        // get MIME type and filename from entry
        InputStream stream = resp.getStream();
        // String mimeType = resp.getHeader("Content-Type");
        String mimeType = (String) current.getValue(Property.CONTENT_STREAM_MIME_TYPE);
        String filename = (String) current.getValue(Property.CONTENT_STREAM_FILE_NAME);
        cs = new SimpleContentStream(stream, mimeType, filename);
        // current.localContentStream = cs; // problem reusing the stream
        return cs;
    }

    public ObjectId setContentStream(ObjectId document, ContentStream cs,
            boolean overwrite) throws IOException {
        APPObjectEntry current = getObjectEntry(document);
        String href = current.getLink(AtomPub.LINK_EDIT_MEDIA);
        if (href == null) {
            throw new RuntimeException("Document is missing link "
                    + AtomPub.LINK_EDIT_MEDIA);
        }
        Request req = new Request(href);
        String filename = cs.getFileName();
        if (filename != null) {
            // Use Slug: header for filename
            req.setHeader(AtomPub.HEADER_SLUG, filename);
        }
        Response resp = connector.put(req, cs.getStream(), cs.getLength(),
                cs.getMimeType());
        if (!resp.isOk()) {
            // TODO exceptions
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        // TODO AtomPub cannot return a new id... (autoversioning)
        return new SimpleObjectId(document.getId());
    }

    public ObjectId deleteContentStream(ObjectId document) {
        APPObjectEntry current = getObjectEntry(document);
        String href = current.getLink(AtomPub.LINK_EDIT_MEDIA);
        if (href == null) {
            throw new RuntimeException("Document is missing link "
                    + AtomPub.LINK_EDIT_MEDIA);
        }
        Response resp = connector.delete(new Request(href));
        if (!resp.isOk()) {
            // TODO exceptions
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }

        // TODO AtomPub cannot return a new id... (autoversioning)
        return new SimpleObjectId(document.getId());
    }

    public ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties) {
        // make properties into an entry for putObject
        APPObjectEntry current = getObjectEntry(object);
        APPObjectEntry update = newObjectEntry(current.getTypeId());
        for (String key : properties.keySet()) {
            update._setValue(key, properties.get(key));
        }
        update._setValue(Property.ID, object.getId());
        // TODO proper title
        update._setValue(Property.NAME, current.getValue(Property.NAME));

        String href = current.getLink(AtomPub.LINK_EDIT);
        if (href == null) {
            throw new RuntimeException("Object is missing link "
                    + AtomPub.LINK_EDIT);
        }
        Request req = new Request(href);
        req.setHeader("Content-Type", AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        Response resp = connector.putObject(req, update);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        return (APPObjectEntry) resp.getObject(new ReadContext(this));
    }

    public ObjectId moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectId object, boolean allVersions) {
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_SELF);
        Request req = new Request(href);
        // TODO XXX allVersions not in spec
        req.setParameter("allVersions", String.valueOf(allVersions));
        Response resp = connector.delete(req);
        int status = resp.getStatusCode();
        if (status == HttpStatus.SC_NOT_FOUND) {
            throw new ObjectNotFoundException(object.getId());
        }
        if (status == HttpStatus.SC_CONFLICT) {
            throw new ConstraintViolationException(resp.getStatusReasonPhrase());
        }
        if (!resp.isOk()) {
            // TODO exceptions
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
    }

    public Collection<ObjectId> deleteTree(ObjectId folder, Unfiling unfiling,
            boolean continueOnFailure) {
        APPObjectEntry current = getObjectEntry(folder);
        String href = current.getLink(AtomPub.LINK_DOWN,
                AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        if (href == null) {
            throw new CMISRuntimeException("Document is missing link "
                    + AtomPub.LINK_DOWN + " "
                    + AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        }
        Request req = new Request(href);
        if (unfiling != null) {
            req.setParameter(AtomPubCMIS.PARAM_UNFILE_OBJECTS,
                    unfiling.toString());
        }
        req.setParameter(AtomPubCMIS.PARAM_CONTINUE_ON_FAILURE,
                Boolean.toString(continueOnFailure));
        Response resp = connector.delete(req);
        int status = resp.getStatusCode();
        if (status == HttpStatus.SC_NOT_FOUND) {
            throw new ObjectNotFoundException(folder.getId());
        }
        if (status == HttpStatus.SC_CONFLICT) {
            throw new ConstraintViolationException(resp.getStatusReasonPhrase());
        }
        if (!resp.isOk()) {
            // TODO exceptions
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        // AtomPub bindings cannot return the objects that could not be deleted
        return Collections.emptyList();
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

    public ListPage<ObjectEntry> query(String statement,
            boolean searchAllVersions, Inclusion inclusion, Paging paging) {
        String href = repository.getCollectionHref(AtomPubCMIS.COL_QUERY);
        Response resp = connector.postQuery(new Request(href), statement,
                searchAllVersions, inclusion, paging);
        if (!resp.isOk()) {
            throw new ContentManagerException(
                    "Remote server returned error code: "
                            + resp.getStatusCode());
        }
        ListPage<ObjectEntry> objects = resp.getObjectFeed(new ReadContext(this));
        return objects;
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        ListPage<ObjectEntry> res = query(statement, searchAllVersions, null,
                null);
        List<CMISObject> objects = new ArrayList<CMISObject>(res.size());
        for (ObjectEntry e : res) {
            objects.add(APPObject.construct((APPObjectEntry) e));
        }
        return objects;
    }

    public ListPage<ObjectEntry> getChangeLog(String changeLogToken,
            boolean includeProperties, Paging paging,
            String[] latestChangeLogToken) {
        latestChangeLogToken[0] = null;
        return SimpleListPage.emptyList();
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

    public ObjectId checkIn(ObjectId document,
            Map<String, Serializable> properties, ContentStream contentStream,
            boolean major, String comment) {
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

    public ListPage<ObjectEntry> getRelationships(ObjectId object,
            String typeId, boolean includeSubRelationshipTypes,
            Inclusion inclusion, Paging paging) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Policy Services -----
     */

    public void applyPolicy(ObjectId object, ObjectId policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(ObjectId object, ObjectId policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId object,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- ACL services -----
     */

    public List<ACE> getACL(ObjectId object, boolean onlyBasicPermissions,
            boolean[] exact) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    public List<ACE> applyACL(ObjectId object, List<ACE> addACEs,
            List<ACE> removeACEs, ACLPropagation propagation, boolean[] exact,
            String[] changeToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
