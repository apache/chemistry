/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Dominique Pfister, Day
 *     Michael Mertins, Saperion
 */
package org.apache.chemistry.jcr;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentAlreadyExistsException;
import org.apache.chemistry.ContentStream;
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
import org.apache.chemistry.Rendition;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Tree;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connection implementation.
 */
class JcrConnection implements Connection, SPI {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrConnection.class);

    /**
     * JCR session.
     */
    private final Session session;

    /**
     * Repository implementation.
     */
    private final JcrRepository repository;

    /**
     * Root folder id.
     */
    private final ObjectId rootFolderId;

    /**
     * Create a new instance of this class.
     *
     * @param session session
     * @param repository repository implementation
     */
    public JcrConnection(Session session, JcrRepository repository) {
        this.session = session;
        this.repository = repository;
        rootFolderId = repository.getRootFolderId();
    }

    /**
     * Return the root folder id.
     *
     * @return root folder id
     */
    public ObjectId getRootFolderId() {
        return rootFolderId;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        session.logout();
    }

    /**
     * Return an entry for a given object id. If the object id is a <code>JcrObject</code>
     * derived class, the entry inside that object will be returned
     *
     * @param objectId object id
     * @return entry or <code>null</code>
     */
    private JcrObjectEntry getEntry(ObjectId objectId) {
        if (objectId instanceof JcrObject) {
            return ((JcrObject) objectId).getEntry();
        }
        try {
            String id = objectId.getId();
            if (rootFolderId.getId().equals(id)) {
                return new JcrObjectEntry(getRootNode(), JcrRepository.ROOT_TYPE, this);
            } else {
                Node node = session.getNodeByIdentifier(id);
                return new JcrObjectEntry(node, this);
            }
        } catch (ItemNotFoundException e) {
            /* item was not found */
            return null;
        } catch (RepositoryException e) {
            String msg = "Unable to get entry with id: " + objectId;
            if (e.getMessage().startsWith("invalid identifier:")) {
                /* treat this as if item was not found */
                log.info(msg, e);
            } else {
                log.error(msg, e);
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public JcrObject getObject(ObjectId objectId) {
        if (objectId instanceof JcrObject) {
            return (JcrObject) objectId;
        }
        JcrObjectEntry entry = getEntry(objectId);
        if (entry == null) {
            return null;
        }
        return JcrObject.construct(entry);
    }

    /**
     * {@inheritDoc}
     */
    public JcrRepository getRepository() {
        return repository;
    }

    /**
     * {@inheritDoc}
     */
    private Node getRootNode() throws RepositoryException {
        if (rootFolderId == null) {
            return session.getRootNode();
        } else {
            return session.getNodeByIdentifier(rootFolderId.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    public JcrFolder getRootFolder() {
        return (JcrFolder) getObject(rootFolderId);
    }

    /**
     * {@inheritDoc}
     */
    public SPI getSPI() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JcrDocument newDocument(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        JcrObjectEntry entry = new JcrObjectEntry(type, this);
        if (folder != null) {
            entry.setValue(Property.PARENT_ID, folder.getId());
        }
        return new JcrDocument(entry);
    }

    /**
     * {@inheritDoc}
     */
    public JcrFolder newFolder(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException(typeId);
        }
        JcrObjectEntry entry = new JcrObjectEntry(type, this);
        if (folder != null) {
            entry.setValue(Property.PARENT_ID, folder.getId());
        }
        return new JcrFolder(entry);
    }

    public Policy newPolicy(String typeId, Folder folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Relationship newRelationship(String typeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        ListPage<ObjectEntry> entries = query(statement, searchAllVersions,
                null, new Paging(Integer.MAX_VALUE, 0));
        List<CMISObject> objects = new ArrayList<CMISObject>(entries.size());
        for (ObjectEntry entry : entries) {
            objects.add(JcrObject.construct((JcrObjectEntry) entry));
        }
        return objects;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectEntry newObjectEntry(String typeId) {
        Type type = repository.getType(typeId);
        if (type == null) {
            throw new IllegalArgumentException(typeId);
        }
        return new JcrObjectEntry(type, this);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId newObjectId(String id) {
        return new SimpleObjectId(id);
    }

    // ----------------------------------------------------------------------
    // SPI

    public void addObjectToFolder(ObjectId objectId, ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void applyPolicy(ObjectId objectId, ObjectId policyId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(ObjectId documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId checkIn(ObjectId documentId,
            Map<String, Serializable> properties, ContentStream contentStream,
            boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId checkOut(ObjectId documentId, boolean[] contentCopied) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * TODO versioningState
     */
    public ObjectId createDocumentFromSource(ObjectId sourceId, ObjectId folderId,
            Map<String, Serializable> properties,
            VersioningState versioningState)
            throws NameConstraintViolationException {

        JcrFolder folder = getFolder(folderId);
        JcrDocument source = getDocument(sourceId);

        try {
            String sourcePath = source.getEntry().getNode().getPath();
            String sourceName = null;
            if (properties != null) {
                sourceName = (String) properties.get(Property.NAME);
            }
            if (sourceName == null) {
                sourceName = source.getEntry().getNode().getName();
            }
            StringBuilder b = new StringBuilder(folder.getEntry().getNode().getPath());
            if (b.charAt(b.length() - 1) != '/') {
                b.append('/');
            }
            b.append(sourceName);
            String destPath = b.toString();

            Workspace workspace = session.getWorkspace();
            workspace.copy(sourcePath, destPath);
            String id = session.getNode(destPath).getIdentifier();

            JcrObject object = getObject(newObjectId(id));
            if (!(object instanceof JcrDocument)) {
                log.error("Target object not found after copying");
                return null;
            }
            if (properties != null) {
                object.setValues(properties);
                object.save();
            }
            return object;

        } catch (RepositoryException e) {
            log.error("Unable to copy.", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId createDocument(Map<String, Serializable> properties,
            ObjectId folderId, ContentStream contentStream,
            VersioningState versioningState)
            throws NameConstraintViolationException {

        String typeId = (String) properties.get(Property.TYPE_ID);
        if (typeId == null) {
            // use a default type, useful for pure AtomPub POST
            typeId = BaseType.DOCUMENT.getId();
        }
        Folder folder = null;
        if (folderId != null) {
            folder = getFolder(folderId);
        }
        JcrDocument doc = newDocument(typeId, folder);
        doc.setProperties(properties);

        if (contentStream != null) {
            doc.setName(contentStream.getFileName());
            try {
                doc.setContentStream(contentStream);
            } catch (IOException e) {
                log.error("I/O exception while setting the content stream.", e);
                return null;
            }
        }
        doc.save();
        return doc;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId createFolder(Map<String, Serializable> properties,
            ObjectId folderId) {

        String typeId = (String) properties.get(Property.TYPE_ID);
        if (typeId == null) {
            throw new IllegalArgumentException("Missing object type id");
        }
        Folder parent = null;
        if (folderId != null) {
            parent = getFolder(folderId);
        }
        JcrFolder folder = newFolder(typeId, parent);
        folder.setProperties(properties);
        folder.save();
        return folder;
    }

    public ObjectId createPolicy(Map<String, Serializable> properties,
            ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createRelationship(Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId deleteContentStream(ObjectId documentId) {
        JcrDocument doc = getDocument(documentId);
        try {
            doc.setContentStream(null);
        } catch (IOException e) {
            /* this should not happen */
            log.error("Unexpected I/O exception while deleting the content stream.", e);
        }
        return doc;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteObject(ObjectId objectId, boolean allVersions) {
        JcrObject object = getObject(objectId);
        if (object == null) {
            throw new ObjectNotFoundException(objectId.getId());
        }
        if (rootFolderId.equals(objectId)) {
            throw new ConstraintViolationException("Unable to delete root folder");
        }
        if (object instanceof JcrFolder) {
            JcrFolder folder = (JcrFolder) object;
            if (folder.getChildren().size() > 0) {
                String msg = "Folder not empty: " + objectId;
                throw new ConstraintViolationException(msg);
            }
        }

        Session session = null;

        try {
            Node node = object.getEntry().getNode();
            session = node.getSession();

            node.remove();
            session.save();

        } catch (RepositoryException e) {
            log.error("Unable to delete object: " + objectId, e);

            if (session != null) {
                try {
                    session.refresh(false);
                } catch (RepositoryException e2) {
                    log.error("Error while refreshing session.", e2);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ObjectId> deleteTree(ObjectId folderId,
            Unfiling unfiling, boolean continueOnFailure) {

        if (rootFolderId.getId().equals(folderId.getId())) {
            throw new ConstraintViolationException("Unable to delete root folder");
        }
        JcrFolder folder = getFolder(folderId);

        Session session = null;

        try {
            Node node = folder.getEntry().getNode();
            session = node.getSession();

            node.remove();
            session.save();

        } catch (RepositoryException e) {
            log.error("Unable to delete folder: " + folderId, e);

            if (session != null) {
                try {
                    session.refresh(false);
                } catch (RepositoryException e2) {
                    log.error("Error while refreshing session.", e2);
                }
            }
        }

        Collection<ObjectId> result = Collections.emptyList();
        return result;
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Set<QName> getAllowableActions(ObjectId objectId) {
        return getProperties(objectId, null).getAllowableActions();
    }

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId objectId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ListPage<ObjectEntry> getCheckedOutDocuments(ObjectId folderId,
            Inclusion inclusion, Paging paging) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ListPage<ObjectEntry> getChildren(ObjectId folderId,
            Inclusion inclusion, String orderBy, Paging paging) {

        try {
            JcrObjectEntry entry = getEntry(folderId);
            if (entry == null) {
                throw new ObjectNotFoundException(folderId.getId());
            }
            if (entry.getBaseType() != BaseType.FOLDER) {
                throw new IllegalArgumentException("Not a folder: " + folderId);
            }

            Node node = entry.getNode();

            NodeIterator iter;
            if (inclusion == null || inclusion.properties == null) {
                iter = node.getNodes();
            } else {
                iter = node.getNodes(inclusion.properties);
            }
            if (iter.hasNext() && paging != null) {
                iter.skip(paging.skipCount);
            }

            int maxItems = paging != null && paging.maxItems != 0 ? paging.maxItems
                    : Integer.MAX_VALUE;
            SimpleListPage<ObjectEntry> result = new SimpleListPage<ObjectEntry>();
            while (result.size() < maxItems && iter.hasNext()) {
                Node child = iter.nextNode();
                if (JcrCmisMap.isInternal(child)) {
                    continue;
                }
                result.add(new JcrObjectEntry(child, this));
            }
            result.setHasMoreItems(iter.hasNext());
            result.setNumItems((int) iter.getSize());
            return result;

        } catch (RepositoryException e) {
            String msg = "Unable to get children: " + folderId;
            log.error(msg, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * TODO contentStreamId
     */
    public ContentStream getContentStream(ObjectId documentId,
            String contentStreamId) throws IOException {

        JcrDocument doc = getDocument(documentId);
        return doc.getContentStream();
    }

    public Tree<ObjectEntry> getFolderTree(ObjectId folderId, int depth,
            Inclusion inclusion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Tree<ObjectEntry> getDescendants(ObjectId folderId, int depth,
            String orderBy, Inclusion inclusion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectEntry getFolderParent(ObjectId folderId, String filter) {
        JcrObjectEntry entry = getEntry(folderId);
        if (entry == null) {
            throw new ObjectNotFoundException(folderId.getId());
        }
        String parentId = (String) entry.getValue(Property.PARENT_ID);
        if (parentId == null) {
            // TODO: test case expects null, but javadoc says
            //       to throw IllegalArgumentException
            return null;
        }
        JcrObjectEntry parent = getEntry(newObjectId(parentId));
        parent.loadValues();
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ObjectEntry> getObjectParents(ObjectId objectId,
            String filter) {

        // TODO JCR 2.0: add support for shareable nodes
        Collection<ObjectEntry> result;
        ObjectEntry parent = getFolderParent(objectId, filter);
        if (parent == null) {
            result = Collections.emptyList();
        } else {
            result = Arrays.asList(new ObjectEntry[] { parent });
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectEntry getObject(ObjectId objectId, Inclusion inclusion) {
        return getProperties(objectId, inclusion);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectEntry getProperties(ObjectId objectId, Inclusion inclusion) {
        return getEntry(objectId);
    }

    /**
     * {@inheritDoc}
     */
    public JcrObjectEntry getObjectByPath(String path, Inclusion inclusion) {
        try {
            Node node = getRootNode();
            if (path == null || path.equals("") || path.equals("/")) {
                return new JcrObjectEntry(node, JcrRepository.ROOT_TYPE, this);
            }
            node = node.getNode(path.substring(1));
            return new JcrObjectEntry(node, this);
        } catch (PathNotFoundException e) {
            log.info("Requested object does not exist: " + path);
        } catch (RepositoryException e) {
            log.error("Unable to get object: " + path, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public JcrFolder getFolder(String path) {
        JcrObjectEntry entry = getObjectByPath(path, null);
        if (entry == null) {
            return null;
        }
        if (entry.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException("Not a folder: " + path);
        }
        return (JcrFolder) JcrObject.construct(entry);
    }

    /**
     * Return a folder given its object id.
     *
     * @param objectId object id
     * @return folder
     * @throws ObjectNotFoundException if the object is not found
     * @throws IllegalArgumentException if the object is not a folder
     */
    private JcrFolder getFolder(ObjectId objectId)
            throws ObjectNotFoundException, IllegalArgumentException {

        if (objectId instanceof JcrFolder) {
            return (JcrFolder) objectId;
        }
        JcrObject o = getObject(objectId);
        if (o == null) {
            throw new ObjectNotFoundException("Folder not found: " + objectId);
        }
        if (!(o instanceof JcrFolder)) {
            throw new IllegalArgumentException("Not a folder: " + objectId);
        }
        return (JcrFolder) o;
    }

    /**
     * Return a document given its object id.
     *
     * @param objectId object id
     * @return document
     * @throws ObjectNotFoundException if the object is not found
     * @throws IllegalArgumentException if the object is not a document
     */
    private JcrDocument getDocument(ObjectId objectId)
            throws ObjectNotFoundException, IllegalArgumentException {

        if (objectId instanceof JcrDocument) {
            return (JcrDocument) objectId;
        }
        JcrObject o = getObject(objectId);
        if (o == null) {
            throw new ObjectNotFoundException("Document not found: " + objectId);
        }
        if (!(o instanceof JcrDocument)) {
            throw new IllegalArgumentException("Not a document: " + objectId);
        }
        return (JcrDocument) o;
    }

    public Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean major, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ListPage<ObjectEntry> getRelationships(ObjectId objectId,
            String typeId, boolean includeSubRelationshipTypes,
            Inclusion inclusion, Paging paging) {
        return SimpleListPage.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public ListPage<Rendition> getRenditions(ObjectId object,
            Inclusion inclusion, Paging paging) {
        return SimpleListPage.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasContentStream(ObjectId documentId) {
        return getDocument(documentId).getContentStream() != null;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId moveObject(ObjectId documentId, ObjectId targetFolderId,
            ObjectId sourceFolderId) {

        JcrFolder folder = getFolder(targetFolderId);
        JcrDocument source = getDocument(documentId);

        if (sourceFolderId != null) {
            if (!source.getParent().getId().equals(sourceFolderId.getId())) {
                throw new IllegalArgumentException("Source folder is not the parent of the document");
            }
        }

        try {
            String sourcePath = source.getEntry().getNode().getPath();
            String sourceName = source.getName();
            String destPath = folder.getEntry().getNode().getPath() + "/" + sourceName;

            Workspace workspace = session.getWorkspace();
            workspace.move(sourcePath, destPath);

            JcrObject object = getObject(documentId);
            if (!(object instanceof JcrDocument)) {
                log.error("Target object not found after moving");
                return null;
            }
            return object;

        } catch (RepositoryException e) {
            log.error("Unable to move.", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ListPage<ObjectEntry> query(String statement,
            boolean searchAllVersions, Inclusion inclusion, Paging paging) {

        try {
            QueryManager qm = session.getWorkspace().getQueryManager();
            QueryResult qr = qm.createQuery(statement, Query.JCR_SQL2).execute();
            NodeIterator iter = qr.getNodes();
            if (iter.hasNext() && paging != null) {
                iter.skip(paging.skipCount);
            }

            int maxItems = paging != null && paging.maxItems != 0 ? paging.maxItems
                    : Integer.MAX_VALUE;
            SimpleListPage<ObjectEntry> result = new SimpleListPage<ObjectEntry>();
            while (result.size() < maxItems && iter.hasNext()) {
                Node child = iter.nextNode();
                if (JcrCmisMap.isInternal(child)) {
                    continue;
                }
                JcrObjectEntry entry = new JcrObjectEntry(child, this);
                entry.loadValues();
                result.add(entry);
            }
            result.setHasMoreItems(iter.hasNext());
            result.setNumItems((int) iter.getSize());
            return result;

        } catch (RepositoryException e) {
            String msg = "Unable to execute query.";
            log.error(msg, e);
            return new SimpleListPage<ObjectEntry>();
        }
    }

    public void removeObjectFromFolder(ObjectId objectId, ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(ObjectId objectId, ObjectId policyId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId setContentStream(ObjectId documentId, ContentStream cs, boolean overwrite)
            throws ContentAlreadyExistsException, IOException {

        JcrDocument doc = getDocument(documentId);
        if (!overwrite && doc.getContentStream() != null) {
            throw new ContentAlreadyExistsException("Document already has a content stream: " + documentId);
        }
        doc.setContentStream(cs);
        return doc;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId updateProperties(ObjectId objectId, String changeToken,
            Map<String, Serializable> properties) {

        JcrObject object = getObject(objectId);
        if (object == null) {
            return null;
        }
        object.setProperties(properties);
        object.save();
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public ListPage<ObjectEntry> getChangeLog(String changeLogToken,
            boolean includeProperties, Paging paging,
            String[] latestChangeLogToken) {
        latestChangeLogToken[0] = null;
        return SimpleListPage.emptyList();
    }

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

    /**
     * Internal: return session associated with this connection.
     */
    Session getSession() {
        return session;
    }
}
