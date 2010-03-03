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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.NameConstraintViolationException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.Rendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Tree;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrConnection implements Connection, SPI {

    private static final Log log = LogFactory.getLog(JcrConnection.class);

    private final Session session;

    private final JcrRepository repository;

    public JcrConnection(Session session, JcrRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    public void close() {
        session.logout();
    }

    public CMISObject getObject(ObjectId objectId) {
        try {
            String relPath = JcrObjectEntry.getPath(objectId.getId()).substring(
                    1);
            if (relPath.equals("")) {
                return getRootFolder();
            } else {
                Node node = session.getRootNode().getNode(relPath);
                if (JcrCmisMap.isNodeDocument(node)) {
                    return new JcrDocument(node, this);
                } else {
                    return new JcrFolder(node, this);
                }
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + objectId;
            log.error(msg, e);
        }
        return null;
    }

    public Repository getRepository() {
        return repository;
    }

    public Folder getRootFolder() {
        try {
            return new JcrFolder(session.getRootNode(), this);
        } catch (RepositoryException e) {
            String msg = "Unable to get root entry.";
            log.error(msg, e);
        }
        return null;
    }

    public SPI getSPI() {
        return this;
    }

    public Document newDocument(String typeId, Folder folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Folder newFolder(String typeId, Folder folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Policy newPolicy(String typeId, Folder folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Relationship newRelationship(String typeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        ListPage<ObjectEntry> entries = query(statement, searchAllVersions,
                null, new Paging(Integer.MAX_VALUE, 0));
        List<CMISObject> objects = new ArrayList<CMISObject>(entries.size());
        for (ObjectEntry entry : entries) {
            // cast entries, they are all JcrFolder or JcrDocument
            objects.add((CMISObject) entry);
        }
        return objects;
    }

    public ObjectEntry newObjectEntry(String typeId) {
        if (JcrCmisMap.isBaseTypeDocument(typeId)) {
            return new JcrDocument(null, this);
        } else {
            return new JcrFolder(null, this);
        }
    }

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

    public ObjectId createDocumentFromSource(ObjectId source, ObjectId folder,
            Map<String, Serializable> properties,
            VersioningState versioningState)
            throws NameConstraintViolationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    // TODO add IOException to throws clause
    public ObjectId createDocument(Map<String, Serializable> properties,
            ObjectId folderId, ContentStream contentStream,
            VersioningState versioningState)
            throws NameConstraintViolationException {

        try {
            JcrFolder folder = (JcrFolder) getObject(folderId);
            Document doc = folder.newDocument(null);
            doc.setValues(properties);
            if (contentStream != null) {
                doc.setName(contentStream.getFileName());
                doc.setValue("title", contentStream.getFileName());
                doc.setContentStream(contentStream);
            }
            doc.save();
            return new SimpleObjectId(doc.getId());
        } catch (Exception e) {
            String msg = "Unable to create document.";
            log.error(msg, e);
        }
        return null;
    }

    public ObjectId createFolder(Map<String, Serializable> properties,
            ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

    public ObjectId deleteContentStream(ObjectId documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectId objectId, boolean allVersions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectId> deleteTree(ObjectId folderId,
            Unfiling unfiling, boolean continueOnFailure) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Set<QName> getAllowableActions(ObjectId objectId) {
        // TODO Auto-generated method stub
        return null;
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

    public ListPage<ObjectEntry> getChildren(ObjectId folderId,
            Inclusion inclusion, String orderBy, Paging paging) {

        try {
            Node node = session.getRootNode();
            String relPath = JcrObjectEntry.getPath(folderId.getId()).substring(
                    1);
            if (!relPath.equals("")) {
                node = node.getNode(relPath);
            }
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
                if (JcrCmisMap.isNodeDocument(child)) {
                    result.add(new JcrDocument(child, this));
                } else {
                    result.add(new JcrFolder(child, this));
                }
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

    public Connection getConnection() {
        return this;
    }

    public ContentStream getContentStream(ObjectId documentId,
            String contentStreamId) throws IOException {
        // TODO contentStreamId
        try {
            String relPath = JcrObjectEntry.getPath(documentId.getId()).substring(
                    1);
            Node node = session.getRootNode().getNode(relPath);
            JcrDocument document = new JcrDocument(node, this);
            return document.getContentStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + documentId;
            log.error(msg, e);
        }
        return null;
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

    public ObjectEntry getFolderParent(ObjectId folderId, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId objectId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getProperties(ObjectId objectId, Inclusion inclusion) {

        try {
            String relPath = JcrObjectEntry.getPath(objectId.getId()).substring(
                    1);
            if (relPath.equals("")) {
                return (ObjectEntry) getRootFolder();
            } else {
                Node node = session.getRootNode().getNode(relPath);
                if (JcrCmisMap.isNodeDocument(node)) {
                    return new JcrDocument(node, this);
                } else {
                    return new JcrFolder(node, this);
                }
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + objectId;
            log.error(msg, e);
        }
        return null;
    }

    public ObjectEntry getObjectByPath(String path, Inclusion inclusion) {
        try {
            if (path == null || path.equals("") || path.equals("/")) {
                return (ObjectEntry) getRootFolder();
            } else {
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                Node node = session.getRootNode().getNode(path);
                if (node.isNodeType(JcrConstants.NT_FOLDER)) {
                    return new JcrFolder(node, this);
                } else if (node.isNodeType(JcrConstants.NT_FILE)) {
                    return new JcrDocument(node, this);
                }
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + path;
            log.error(msg, e);
        }
        return null;
    }

    public Folder getFolder(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean major, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ListPage<ObjectEntry> getRelationships(ObjectId objectId,
            String typeId, boolean includeSubRelationshipTypes,
            Inclusion inclusion, Paging paging) {
        return SimpleListPage.emptyList();
    }

    public ListPage<Rendition> getRenditions(ObjectId object,
            Inclusion inclusion, Paging paging) {
        return SimpleListPage.emptyList();
    }

    public boolean hasContentStream(ObjectId document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId moveObject(ObjectId objectId, ObjectId targetFolderId,
            ObjectId sourceFolderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ListPage<ObjectEntry> query(String statement,
            boolean searchAllVersions, Inclusion inclusion, Paging paging) {

        try {
            QueryManager qm = session.getWorkspace().getQueryManager();
            QueryResult qr = qm.createQuery(statement, Query.SQL).execute();
            NodeIterator iter = qr.getNodes();
            if (iter.hasNext() && paging != null) {
                iter.skip(paging.skipCount);
            }

            int maxItems = paging != null && paging.maxItems != 0 ? paging.maxItems
                    : Integer.MAX_VALUE;
            SimpleListPage<ObjectEntry> result = new SimpleListPage<ObjectEntry>();
            while (result.size() < maxItems && iter.hasNext()) {
                Node child = iter.nextNode();
                if (child.isNodeType(JcrConstants.NT_FOLDER)) {
                    result.add(new JcrFolder(child, this));
                } else if (child.isNodeType(JcrConstants.NT_FILE)) {
                    result.add(new JcrDocument(child, this));
                }
            }
            result.setHasMoreItems(iter.hasNext());
            result.setNumItems((int) iter.getSize());
            return result;

        } catch (RepositoryException e) {
            String msg = "Unable to execute query.";
            log.error(msg, e);
        }
        return null;
    }

    public void removeObjectFromFolder(ObjectId objectId, ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(ObjectId objectId, ObjectId policyId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId setContentStream(ObjectId documentId,
            ContentStream contentStream, boolean overwrite) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId updateProperties(ObjectId objectId, String changeToken,
            Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

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

}
