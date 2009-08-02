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
 */
package org.apache.chemistry.jcr;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Rendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
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
                if (node.isNodeType(JcrConstants.NT_FOLDER)) {
                    return new JcrFolder(node);
                } else if (node.isNodeType(JcrConstants.NT_FILE)) {
                    return new JcrDocument(node);
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
            return new JcrFolder(session.getRootNode());
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
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> entries = query(statement, searchAllVersions,
                false, false, Integer.MAX_VALUE, 0, hasMoreItems);
        List<CMISObject> objects = new ArrayList<CMISObject>(entries.size());
        for (ObjectEntry entry : entries) {
            // cast entries, they are all JcrFolder or JcrDocument
            objects.add((CMISObject) entry);
        }
        return objects;
    }

    public ObjectEntry newObjectEntry(String typeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId newObjectId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

    public ObjectId checkIn(ObjectId documentId, boolean major,
            Map<String, Serializable> properties, ContentStream contentStream,
            String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId checkOut(ObjectId documentId, boolean[] contentCopied) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    // TODO add IOException to throws clause
    public ObjectId createDocument(String typeId,
            Map<String, Serializable> properties, ObjectId folderId,
            ContentStream contentStream, VersioningState versioningState) {

        try {
            JcrFolder folder = (JcrFolder) getObject(folderId);
            Document doc = folder.newDocument(null);
            doc.setValues(properties);
            if (contentStream != null) {
                doc.setName(contentStream.getFilename());
                doc.setValue("title", contentStream.getFilename());
                doc.setContentStream(contentStream);
            }
            doc.save();
            return new SimpleObjectId(doc.getId());
        } catch (IOException e) {
            String msg = "Unable to create document.";
            log.error(msg, e);
        }
        return null;
    }

    public ObjectId createFolder(String typeId,
            Map<String, Serializable> properties, ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createPolicy(String typeId,
            Map<String, Serializable> properties, ObjectId folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId createRelationship(String typeId,
            Map<String, Serializable> properties, ObjectId sourceId,
            ObjectId targetId) {
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

    public Collection<String> getAllowableActions(ObjectId objectId,
            String asUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId objectId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getCheckedoutDocuments(ObjectId folderId,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getChildren(ObjectId folderId, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            int maxItems, int skipCount, String orderBy, boolean[] hasMoreItems) {

        try {
            if (maxItems == 0) {
                maxItems = Integer.MAX_VALUE;
            }

            Node node = session.getRootNode();
            String relPath = JcrObjectEntry.getPath(folderId.getId()).substring(
                    1);
            if (!relPath.equals("")) {
                node = node.getNode(relPath);
            }
            NodeIterator iter;
            if (filter == null) {
                iter = node.getNodes();
            } else {
                iter = node.getNodes(filter);
            }
            /* Problem with skipCount == 0, when there are no more elements */
            if (iter.hasNext()) {
                iter.skip(skipCount);
            }

            List<ObjectEntry> result = new ArrayList<ObjectEntry>();
            while (result.size() < maxItems && iter.hasNext()) {
                Node child = iter.nextNode();
                if (child.isNodeType(JcrConstants.NT_FOLDER)) {
                    result.add(new JcrFolder(child));
                } else if (child.isNodeType(JcrConstants.NT_FILE)) {
                    result.add(new JcrDocument(child));
                }
            }
            hasMoreItems[0] = iter.hasNext();
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
            JcrDocument document = new JcrDocument(node);
            return document.getContentStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + documentId;
            log.error(msg, e);
        }
        return null;
    }

    public List<ObjectEntry> getFolderTree(ObjectId folderId, int depth,
            String filter, boolean includeAllowableActions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getDescendants(ObjectId folderId, int depth,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getFolderParent(ObjectId folderId, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId objectId,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getProperties(ObjectId objectId, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {

        try {
            String relPath = JcrObjectEntry.getPath(objectId.getId()).substring(
                    1);
            if (relPath.equals("")) {
                return (ObjectEntry) getRootFolder();
            } else {
                Node node = session.getRootNode().getNode(relPath);
                if (node.isNodeType(JcrConstants.NT_FOLDER)) {
                    return new JcrFolder(node);
                } else if (node.isNodeType(JcrConstants.NT_FILE)) {
                    return new JcrDocument(node);
                }
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + objectId;
            log.error(msg, e);
        }
        return null;
    }

    public Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean major, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getRelationships(ObjectId objectId,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes, String filter,
            String includeAllowableActions, int maxItems, int skipCount,
            boolean[] hasMoreItems) {

        return Collections.emptyList();
    }

    public List<Rendition> getRenditions(ObjectId object, String filter,
            int maxItems, int skipCount) {
        return Collections.emptyList();
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

    public Collection<ObjectEntry> query(String statement,
            boolean searchAllVersions, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {

        try {
            QueryManager qm = session.getWorkspace().getQueryManager();
            QueryResult qr = qm.createQuery(statement, Query.SQL).execute();
            NodeIterator iter = qr.getNodes();
            iter.skip(skipCount);

            List<ObjectEntry> result = new ArrayList<ObjectEntry>();

            while (result.size() < maxItems && iter.hasNext()) {
                Node child = iter.nextNode();
                if (child.isNodeType(JcrConstants.NT_FOLDER)) {
                    result.add(new JcrFolder(child));
                } else if (child.isNodeType(JcrConstants.NT_FILE)) {
                    result.add(new JcrDocument(child));
                }
            }
            hasMoreItems[0] = iter.hasNext();
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

    public ObjectId setContentStream(ObjectId documentId, boolean overwrite,
            ContentStream contentStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId updateProperties(ObjectId objectId, String changeToken,
            Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<ObjectEntry> getChangeLog(String changeLogToken,
            boolean includeProperties, int maxItems, boolean[] hasMoreItems,
            String[] lastChangeLogToken) {
        hasMoreItems[0] = false;
        lastChangeLogToken[0] = null;
        return Collections.<ObjectEntry> emptyList().iterator();
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
