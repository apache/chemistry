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
 *     Chris Hubick
 */
package org.apache.chemistry.atompub.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.NameConstraintViolationException;
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
import org.apache.chemistry.Tree;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.stax.XmlProperty;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.httpclient.Header;

/**
 * A {@link Connection} and {@link SPI} using the AtomPub protocol to talk to a
 * CMIS server.
 */
public class APPConnection implements Connection, SPI {

    protected final APPRepository repository;

    protected final Connector connector;

    protected APPFolder root;

    public APPConnection(APPRepository repository,
            Map<String, Serializable> params) {
        this.repository = repository;
        connector = new Connector(repository.getClient(params), new APPContext(
                this));
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

    // TODO check capabilityGetDescendants / capabilityGetFolderTree
    // and folder fall back on recursion based on getChildren

    public Tree<ObjectEntry> getFolderTree(ObjectId folder, int depth,
            Inclusion inclusion) {
        String href = getFolderEntry(folder).getLink(
                AtomPubCMIS.LINK_FOLDER_TREE, AtomPub.MEDIA_TYPE_ATOM_FEED);
        if (href == null) {
            throw new CMISRuntimeException("Missing foldertree link");
        }
        NameValuePairs params = new NameValuePairs();
        params.add(AtomPubCMIS.PARAM_DEPTH, Integer.toString(depth));
        if (inclusion != null) {
            if (inclusion.properties != null) {
                params.add(AtomPubCMIS.PARAM_FILTER, inclusion.properties);
            }
            if (inclusion.renditions != null) {
                params.add(AtomPubCMIS.PARAM_RENDITION_FILTER,
                        inclusion.renditions);
            }
            if (inclusion.relationships != null) {
                params.add(
                        AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS,
                        RelationshipDirection.toInclusion(inclusion.relationships));
            }
            params.add(AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS,
                    Boolean.toString(inclusion.allowableActions));
        }
        return connector.getEntryFeedTree(href, params);
    }

    public Tree<ObjectEntry> getDescendants(ObjectId folder, int depth,
            String orderBy, Inclusion inclusion) {
        String href = getFolderEntry(folder).getLink(AtomPub.LINK_DOWN,
                AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        if (href == null) {
            throw new CMISRuntimeException("Missing down tree link");
        }
        NameValuePairs params = new NameValuePairs();
        params.add(AtomPubCMIS.PARAM_DEPTH, Integer.toString(depth));
        if (orderBy != null) {
            params.add(AtomPubCMIS.PARAM_ORDER_BY, orderBy);
        }
        if (inclusion != null) {
            if (inclusion.properties != null) {
                params.add(AtomPubCMIS.PARAM_FILTER, inclusion.properties);
            }
            if (inclusion.renditions != null) {
                params.add(AtomPubCMIS.PARAM_RENDITION_FILTER,
                        inclusion.renditions);
            }
            if (inclusion.relationships != null) {
                params.add(
                        AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS,
                        RelationshipDirection.toInclusion(inclusion.relationships));
            }
            params.add(AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS,
                    Boolean.toString(inclusion.allowableActions));
            params.add(AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS,
                    Boolean.toString(inclusion.policies));
            params.add(AtomPubCMIS.PARAM_INCLUDE_ACL,
                    Boolean.toString(inclusion.acls));
        }
        return connector.getEntryFeedTree(href, params);
    }

    public ListPage<ObjectEntry> getChildren(ObjectId folder,
            Inclusion inclusion, String orderBy, Paging paging) {
        // TODO filter, includeRelationship, includeAllowableActions, orderBy
        String href = getFolderEntry(folder).getLink(AtomPub.LINK_DOWN,
                AtomPub.MEDIA_TYPE_ATOM_FEED);
        if (href == null) {
            throw new CMISRuntimeException("Missing down link");
        }
        NameValuePairs params = new NameValuePairs();
        if (paging != null) {
            params.add(AtomPubCMIS.PARAM_MAX_ITEMS,
                    Integer.toString(paging.maxItems));
            params.add(AtomPubCMIS.PARAM_SKIP_COUNT,
                    Integer.toString(paging.skipCount));
        }
        return connector.getEntryFeed(href, params);
    }

    public ObjectEntry getFolderParent(ObjectId folder, String filter) {
        // TODO filter
        APPObjectEntry current = getFolderEntry(folder);
        String rootId = repository.getInfo().getRootFolderId().getId();
        if (current.getId().equals(rootId)) {
            return null;
        }
        String href = current.getLink(AtomPub.LINK_UP,
                AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        if (href == null) {
            return null;
        }
        return connector.getEntry(href, folder.getId());
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId object,
            String filter) {
        // TODO filter
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_UP);
        if (href == null) {
            throw new CMISRuntimeException("Missing up link");
        }
        return connector.getEntryFeed(href, null);
    }

    public ListPage<ObjectEntry> getCheckedOutDocuments(ObjectId folder,
            Inclusion inclusion, Paging paging) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Object Services -----
     */

    protected APPObjectEntry getObjectEntryOrNull(ObjectId objectId) {
        if (objectId instanceof APPObjectEntry) {
            return (APPObjectEntry) objectId;
        } else if (objectId instanceof APPObject) {
            return ((APPObject) objectId).getEntry();
        } else {
            return null;
        }
    }

    protected APPObjectEntry getObjectEntry(ObjectId objectId) {
        return getObjectEntry(objectId, null);
    }

    protected APPObjectEntry getObjectEntry(ObjectId objectId,
            Inclusion inclusion) {
        APPObjectEntry entry = getObjectEntryOrNull(objectId);
        if (entry != null) {
            return entry;
        }
        URITemplate uriTemplate = repository.getURITemplate(AtomPubCMIS.URITMPL_OBJECT_BY_ID);
        String href = uriTemplate.template;
        href = replace(href, AtomPubCMIS.PARAM_ID, objectId.getId());
        href = replaceInclusion(href, inclusion);
        return connector.getEntry(href, objectId.getId());
    }

    protected APPObjectEntry getFolderEntry(ObjectId objectId) {
        APPObjectEntry entry = getObjectEntry(objectId);
        if (!entry.getBaseType().equals(BaseType.FOLDER)) {
            throw new IllegalArgumentException("Not a folder: " + objectId);
        }
        return entry;
    }

    protected String replaceInclusion(String href, Inclusion inclusion) {
        if (inclusion == null) {
            inclusion = new Inclusion(null, null, null, false, false, false);
        }
        href = replace(href, AtomPubCMIS.PARAM_FILTER,
                inclusion.properties == null ? "" : inclusion.properties);
        href = replace(href, AtomPubCMIS.PARAM_RENDITION_FILTER,
                inclusion.renditions == null ? "" : inclusion.renditions);
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_RELATIONSHIPS,
                RelationshipDirection.toInclusion(inclusion.relationships));
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS,
                Boolean.toString(inclusion.allowableActions));
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_POLICY_IDS,
                Boolean.toString(inclusion.policies));
        href = replace(href, AtomPubCMIS.PARAM_INCLUDE_ACL,
                Boolean.toString(inclusion.acls));
        return href;
    }

    protected String replace(String template, String param, String value) {
        return template.replace('{' + param + '}', value);
    }

    public CMISObject getObject(ObjectId object) {
        APPObjectEntry entry;
        try {
            entry = getObjectEntry(object);
        } catch (ObjectNotFoundException e) {
            return null;
        }
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

    protected APPObjectEntry createObject(String href,
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
        return connector.postEntry(href, null, entry);
    }

    public ObjectId createDocumentFromSource(ObjectId source, ObjectId folder,
            Map<String, Serializable> properties,
            VersioningState versioningState)
            throws NameConstraintViolationException {
        // TODO implement copy "by hand" or using extensions when available
        throw new CMISRuntimeException(
                "AtomPub bindings do not support createDocumentFromSource");
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

    public Set<QName> getAllowableActions(ObjectId object) {
        APPObjectEntry entry = getObjectEntryOrNull(object);
        if (entry != null && entry.getAllowableActions() == null) {
            // use the allowable action link
            String href = entry.getLink(AtomPubCMIS.LINK_ALLOWABLE_ACTIONS);
            if (href == null) {
                throw new CMISRuntimeException("Missing allowableactions link");
            }
            Set<QName> allowableActions = connector.getAllowableActions(href);
            entry.setAllowableActions(allowableActions);
            return allowableActions;
        }
        if (entry == null) {
            // fetch including allowableActions
            Inclusion inclusion = new Inclusion(null, null, null, true, false,
                    false);
            entry = getObjectEntry(object, inclusion);
        }
        return entry.getAllowableActions();
    }

    public ObjectEntry getProperties(ObjectId object, Inclusion inclusion) {
        // TODO inclusion
        APPObjectEntry current;
        try {
            current = getObjectEntry(object);
        } catch (ObjectNotFoundException e) {
            return null;
        }
        String href = current.getLink(AtomPub.LINK_SELF);
        try {
            return connector.getEntry(href, object.getId());
            // TODO fill current
        } catch (ObjectNotFoundException e) {
            // object not found, signature says return null
            return null;
        }
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
        href = replaceInclusion(href, inclusion);
        try {
            return connector.getEntry(href, path);
            // TODO fill current
        } catch (ObjectNotFoundException e) {
            // object not found, signature says return null
            return null;
        }
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

        String href = current.getContentHref();
        if (href == null) {
            throw new RuntimeException("Missing content src");
        }
        String mimeType = (String) current.getValue(Property.CONTENT_STREAM_MIME_TYPE);
        String filename = (String) current.getValue(Property.CONTENT_STREAM_FILE_NAME);
        cs = connector.getContentStream(href, mimeType, filename);
        // current.localContentStream = cs; // problem reusing the stream
        return cs;
    }

    public ObjectId setContentStream(ObjectId document, ContentStream cs,
            boolean overwrite) throws IOException {
        APPObjectEntry current = getObjectEntry(document);
        String href = current.getLink(AtomPub.LINK_EDIT_MEDIA);
        if (href == null) {
            throw new RuntimeException("Missing link "
                    + AtomPub.LINK_EDIT_MEDIA);
        }
        connector.putStream(href, cs);
        // TODO AtomPub cannot return a new id... (autoversioning)
        return new SimpleObjectId(document.getId());
    }

    public ObjectId deleteContentStream(ObjectId document) {
        APPObjectEntry current = getObjectEntry(document);
        String href = current.getLink(AtomPub.LINK_EDIT_MEDIA);
        if (href == null) {
            throw new RuntimeException("Missing link "
                    + AtomPub.LINK_EDIT_MEDIA);
        }
        connector.delete(href, null, document.getId());
        // TODO AtomPub cannot return a new id... (autoversioning)
        return new SimpleObjectId(document.getId());
    }

    public ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties) {
        // make properties into an entry for putObject
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_EDIT);
        if (href == null) {
            throw new RuntimeException("Missing link " + AtomPub.LINK_EDIT);
        }

        APPObjectEntry update = newObjectEntry(current.getTypeId());
        for (String key : properties.keySet()) {
            update._setValue(key, properties.get(key));
        }
        update._setValue(Property.ID, object.getId());
        // TODO proper title
        update._setValue(Property.NAME, current.getValue(Property.NAME));

        Header header = new Header("Content-Type",
                AtomPub.MEDIA_TYPE_ATOM_ENTRY);
        return connector.putEntry(href, header, update);
    }

    public ObjectId moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder) {
        APPObjectEntry entry = getObjectEntry(object);
        NameValuePairs params = new NameValuePairs();
        params.add(AtomPubCMIS.PARAM_SOURCE_FOLDER_ID,
                sourceFolder == null ? "" : sourceFolder.getId());
        return connector.postEntry(getPostHref(targetFolder), params, entry);
    }

    public void deleteObject(ObjectId object, boolean allVersions) {
        APPObjectEntry current = getObjectEntry(object);
        String href = current.getLink(AtomPub.LINK_SELF);
        NameValuePairs params = new NameValuePairs();
        // TODO XXX allVersions not in spec
        params.add("allVersions", String.valueOf(allVersions));
        connector.delete(href, params, object.getId());
    }

    public Collection<ObjectId> deleteTree(ObjectId folder, Unfiling unfiling,
            boolean continueOnFailure) {
        APPObjectEntry current = getObjectEntry(folder);
        String href = current.getLink(AtomPub.LINK_DOWN,
                AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        if (href == null) {
            throw new CMISRuntimeException("Missing link " + AtomPub.LINK_DOWN
                    + " " + AtomPubCMIS.MEDIA_TYPE_CMIS_TREE);
        }
        NameValuePairs params = new NameValuePairs();
        if (unfiling != null) {
            params.add(AtomPubCMIS.PARAM_UNFILE_OBJECTS, unfiling.toString());
        }
        params.add(AtomPubCMIS.PARAM_CONTINUE_ON_FAILURE,
                Boolean.toString(continueOnFailure));
        connector.delete(href, params, folder.getId());
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
        if (href == null) {
            throw new CMISRuntimeException("Missing collection "
                    + AtomPubCMIS.COL_QUERY);
        }
        return connector.postQuery(href, statement, searchAllVersions,
                inclusion, paging);
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
