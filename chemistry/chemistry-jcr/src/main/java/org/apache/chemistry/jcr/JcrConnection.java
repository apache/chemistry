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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.ReturnVersion;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.type.BaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrConnection implements Connection, SPI {

    private static final Log log = LogFactory.getLog(JcrConnection.class);

    private Session session;
    private JcrRepository repository;

    public JcrConnection(Session session, JcrRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    public void addObjectToFolder(ObjectEntry object, ObjectEntry folder) {
        throw new UnsupportedOperationException();
    }

    public void applyPolicy(Policy policy, ObjectEntry object) {
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(ObjectEntry document) {
        throw new UnsupportedOperationException();
    }

    public CMISObject checkIn(ObjectEntry document, boolean major,
                              String comment) {
        throw new UnsupportedOperationException();
    }

    public CMISObject checkOut(ObjectEntry document) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        session.logout();
    }

    public void deleteAllVersions(ObjectEntry document) {
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectEntry object) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> deleteTree(ObjectEntry folder, Unfiling unfiling,
                                         boolean continueOnFailure) {
        JcrFolder f = (JcrFolder) folder;
        f.delete();
        return Collections.emptySet();
    }

    public Collection<ObjectEntry> getAllVersions(ObjectEntry document,
                                                  String filter) {
        throw new UnsupportedOperationException();
    }

    public Collection<Policy> getAppliedPolicies(ObjectEntry object) {
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getChildren(ObjectEntry folder) {
        return getChildren(folder.getId(), null, null, false, false,
                Integer.MAX_VALUE, 0, null, new boolean[1]);
    }

    public CMISObject getLatestVersion(ObjectEntry document, boolean major) {
        throw new UnsupportedOperationException();
    }

    public CMISObject getObject(String objectId, ReturnVersion returnVersion) {
        try {
            String relPath = JcrObjectEntry.getPath(objectId).substring(1);
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

    public List<ObjectEntry> getRelationships(ObjectEntry object,
                                              RelationshipDirection direction,
                                              String typeId,
                                              boolean includeSubRelationshipTypes) {
        throw new UnsupportedOperationException();
    }

    public Repository getRepository() {
        return repository;
    }

    public ObjectEntry getRootEntry() {
        return getRootFolder();
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

    public void moveObject(ObjectEntry object, ObjectEntry targetFolder,
                           ObjectEntry sourceFolder) {
        throw new UnsupportedOperationException();
    }

    public Document newDocument(String typeId, ObjectEntry folder) {
        throw new UnsupportedOperationException();
    }

    public Folder newFolder(String typeId, ObjectEntry folder) {
        throw new UnsupportedOperationException();
    }

    public Policy newPolicy(String typeId, ObjectEntry folder) {
        throw new UnsupportedOperationException();
    }

    public Relationship newRelationship(String typeId) {
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> query(String statement,
                                         boolean searchAllVersions) {

        boolean[] hasMoreItems = new boolean[1];
        return query(statement, searchAllVersions, false, false,
                Integer.MAX_VALUE, 0, hasMoreItems);
    }

    public void removeObjectFromFolder(ObjectEntry object, ObjectEntry folder) {
        throw new UnsupportedOperationException();
    }

    public void removePolicy(Policy policy, ObjectEntry object) {
        throw new UnsupportedOperationException();
    }

    //---------------------------------------------------------------------- SPI

    public void addObjectToFolder(String objectId, String folderId) {
        throw new UnsupportedOperationException();
    }

    public void applyPolicy(String policyId, String objectId) {
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(String documentId) {
        throw new UnsupportedOperationException();
    }

    public String checkIn(String documentId, boolean major,
                          Map<String, Serializable> properties,
                          ContentStream contentStream, String comment) {
        throw new UnsupportedOperationException();
    }

    public String checkOut(String documentId, boolean[] contentCopied) {
        throw new UnsupportedOperationException();
    }

    // TODO add IOException to throws clause
    public String createDocument(String typeId,
                                 Map<String, Serializable> properties,
                                 String folderId, ContentStream contentStream,
                                 VersioningState versioningState) {

    	try {
	    	JcrFolder folder = (JcrFolder) getObject(folderId, ReturnVersion.LATEST);
	    	Document doc = folder.newDocument(null);
	    	doc.setValues(properties);
	    	if (contentStream != null) {
		    	doc.setName(contentStream.getFilename());
		    	doc.setValue("title", contentStream.getFilename());
		    	doc.setContentStream(contentStream);
	    	}
	    	doc.save();
	    	return doc.getId();
    	} catch (IOException e) {
    		String msg = "Unable to create document.";
    		log.error(msg, e);
    	}
    	return null;
    }

    public String createFolder(String typeId,
                               Map<String, Serializable> properties,
                               String folderId) {
        throw new UnsupportedOperationException();
    }

    public String createPolicy(String typeId,
                               Map<String, Serializable> properties,
                               String folderId) {
        throw new UnsupportedOperationException();
    }

    public String createRelationship(String typeId,
                                     Map<String, Serializable> properties,
                                     String sourceId, String targetId) {
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions(String versionSeriesId) {
        throw new UnsupportedOperationException();
    }

    public void deleteContentStream(String documentId) {
        throw new UnsupportedOperationException();
    }

    public void deleteObject(String objectId) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> deleteTree(String folderId, Unfiling unfiling,
                                         boolean continueOnFailure) {
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
                                                  String filter) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getAllowableActions(String objectId, String asUser) {
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAppliedPolicies(String objectId,
                                                      String filter) {
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getCheckedoutDocuments(String folderId,
                                                          String filter,
                                                          boolean includeAllowableActions,
                                                          boolean includeRelationships,
                                                          int maxItems,
                                                          int skipCount,
                                                          boolean[] hasMoreItems) {
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getChildren(String folderId, BaseType type,
                                         String filter,
                                         boolean includeAllowableActions,
                                         boolean includeRelationships,
                                         int maxItems, int skipCount,
                                         String orderBy, boolean[] hasMoreItems) {

        try {
            if (maxItems == 0) {
                maxItems = Integer.MAX_VALUE;
            }

            Node node = session.getRootNode();
            String relPath = JcrObjectEntry.getPath(folderId).substring(1);
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

    public InputStream getContentStream(String documentId, int offset,
                                        int length) throws IOException {

        try {
            String relPath = JcrObjectEntry.getPath(documentId).substring(1);
            Node node = session.getRootNode().getNode(relPath);
            JcrDocument document = new JcrDocument(node);
            return document.getStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get object: " + documentId;
            log.error(msg, e);
        }
        return null;
    }

    public List<ObjectEntry> getDescendants(String folderId, BaseType type,
                                            int depth, String filter,
                                            boolean includeAllowableActions,
                                            boolean includeRelationships,
                                            String orderBy) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ObjectEntry> getFolderParent(String folderId, String filter,
                                             boolean includeAllowableActions,
                                             boolean includeRelationships,
                                             boolean returnToRoot) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ObjectEntry> getObjectParents(String objectId,
                                                    String filter,
                                                    boolean includeAllowableActions,
                                                    boolean includeRelationships) {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectEntry getProperties(String objectId,
                                     ReturnVersion returnVersion,
                                     String filter,
                                     boolean includeAllowableActions,
                                     boolean includeRelationships) {

        try {
            String relPath = JcrObjectEntry.getPath(objectId).substring(1);
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

    public Map<String, Serializable> getPropertiesOfLatestVersion(String versionSeriesId,
                                                                  boolean majorVersion,
                                                                  String filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ObjectEntry> getRelationships(String objectId,
                                              RelationshipDirection direction,
                                              String typeId,
                                              boolean includeSubRelationshipTypes,
                                              String filter,
                                              String includeAllowableActions,
                                              int maxItems, int skipCount,
                                              boolean[] hasMoreItems) {

        return Collections.emptyList();
    }

    public void moveObject(String objectId, String targetFolderId,
                           String sourceFolderId) {

        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> query(String statement,
                                         boolean searchAllVersions,
                                         boolean includeAllowableActions,
                                         boolean includeRelationships,
                                         int maxItems, int skipCount,
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

    public void removeObjectFromFolder(String objectId, String folderId) {
        throw new UnsupportedOperationException();
    }

    public void removePolicy(String policyId, String objectId) {
        throw new UnsupportedOperationException();
    }

    public void setContentStream(String documentId, boolean overwrite,
                                 ContentStream contentStream) {
        throw new UnsupportedOperationException();
    }

    public String updateProperties(String objectId, String changeToken,
                                   Map<String, Serializable> properties) {
        throw new UnsupportedOperationException();
    }
}
