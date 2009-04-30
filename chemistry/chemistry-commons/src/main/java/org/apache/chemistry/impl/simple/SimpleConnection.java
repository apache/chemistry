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
package org.apache.chemistry.impl.simple;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.chemistry.property.Property;
import org.apache.chemistry.property.PropertyDefinition;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.type.BaseType;
import org.apache.chemistry.type.ContentStreamPresence;
import org.apache.chemistry.type.Type;

public class SimpleConnection implements Connection, SPI {

    protected final SimpleRepository repository;

    protected final SimpleFolder rootFolder;

    public SimpleConnection(SimpleRepository repository) {
        this.repository = repository;
        rootFolder = (SimpleFolder) getObject(
                repository.getInfo().getRootFolderId(), ReturnVersion.THIS);
    }

    public Connection getConnection() {
        return this;
    }

    public SPI getSPI() {
        return this;
    }

    public void close() {
        // TODO disable use of this connection
    }

    public Repository getRepository() {
        return repository;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public ObjectEntry getRootEntry() {
        return rootFolder;
    }

    public List<ObjectEntry> getChildren(ObjectEntry folder) {
        return getChildren(folder.getId(), null, null, false, false,
                Integer.MAX_VALUE, 0, null, new boolean[1]);
    }

    /*
     * ----- Factories -----
     */

    public Document newDocument(String typeId, ObjectEntry folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId);
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        return new SimpleDocument(data, this);
    }

    public Folder newFolder(String typeId, ObjectEntry folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId);
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        return new SimpleFolder(data, this);
    }

    public Relationship newRelationship(String typeId) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.RELATIONSHIP) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId);
        return new SimpleRelationship(data, this);
    }

    public Policy newPolicy(String typeId, ObjectEntry folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.POLICY) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId);
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        return new SimplePolicy(data, this);
    }

    /*
     * Called by save() for new objects.
     */
    protected void saveObject(SimpleObject object) {
        SimpleData data = object.data;
        Map<String, Serializable> update = new HashMap<String, Serializable>();

        // generate an ID
        String id = repository.generateId();
        update.put(Property.ID, id);

        // check mandatory properties
        Type type = object.getType();
        for (PropertyDefinition pd : type.getPropertyDefinitions()) {
            String name = pd.getName();
            if (Property.ID.equals(name)) {
                // ignore, set later
                continue;
            }
            if (pd.isRequired() && !data.containsKey(name)) {
                if (Property.NAME.equals(name)) {
                    update.put(Property.NAME, id);
                } else if (Property.CREATED_BY.equals(name)) {
                    update.put(Property.CREATED_BY, "system"); // TODO
                } else if (Property.CREATION_DATE.equals(name)) {
                    update.put(Property.CREATION_DATE, Calendar.getInstance());
                } else if (Property.LAST_MODIFIED_BY.equals(name)) {
                    update.put(Property.LAST_MODIFIED_BY, "system"); // TODO
                } else if (Property.LAST_MODIFICATION_DATE.equals(name)) {
                    update.put(Property.LAST_MODIFICATION_DATE,
                            Calendar.getInstance());
                } else if (Property.CONTENT_STREAM_ALLOWED.equals(name)) {
                    update.put(Property.CONTENT_STREAM_ALLOWED, "allowed");
                } else if (Property.IS_LATEST_VERSION.equals(name)) {
                    update.put(Property.IS_LATEST_VERSION, Boolean.TRUE);
                } else if (Property.IS_LATEST_MAJOR_VERSION.equals(name)) {
                    update.put(Property.IS_LATEST_MAJOR_VERSION, Boolean.TRUE);
                } else if (Property.IS_VERSION_SERIES_CHECKED_OUT.equals(name)) {
                    update.put(Property.IS_VERSION_SERIES_CHECKED_OUT,
                            Boolean.FALSE);
                } else if (Property.VERSION_SERIES_ID.equals(name)) {
                    update.put(Property.VERSION_SERIES_ID, id);
                } else if (Property.VERSION_LABEL.equals(name)) {
                    update.put(Property.VERSION_LABEL, "1.0");
                } else {
                    throw new RuntimeException("Missing property: " + name); // TODO
                }
            }
        }

        byte[] contentBytes = (byte[]) data.get(SimpleDocument.CONTENT_BYTES_KEY);
        if (type.getContentStreamAllowed() == ContentStreamPresence.REQUIRED
                && contentBytes == null) {
            throw new RuntimeException("Content stream required"); // TODO
        }

        // content stream
        if (contentBytes != null) {
            update.put(Property.CONTENT_STREAM_LENGTH,
                    Integer.valueOf(contentBytes.length));
        }
        // update data once we know there's no error
        data.putAll(update);

        // properties
        repository.datas.put(id, data); // TODO clone data?

        if (contentBytes == null) {
            repository.contentBytes.remove(id);
        } else {
            repository.contentBytes.put(id, contentBytes);
        }

        // parents/children
        String parentId = (String) data.get(Property.PARENT_ID);
        if (type.getBaseType() == BaseType.FOLDER) {
            // new folder, empty set of children
            repository.children.put(id, repository.newSet());
        } else {
            // only folders have this property
            data.remove(Property.PARENT_ID);
        }
        if (parentId != null) {
            // this object is filed
            // pointer to parent
            Set<String> parents = repository.newSet();
            parents.add(parentId);
            repository.parents.put(id, parents);
            // new pointer to child
            repository.children.get(parentId).add(id);
        }
    }

    /*
     * ----- Navigation Services -----
     */

    public List<ObjectEntry> getDescendants(String folderId, BaseType type,
            int depth, String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getChildren(String folderId, BaseType type,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            String orderBy, boolean[] hasMoreItems) {
        // TODO type and orderBy
        Set<String> ids = repository.children.get(folderId);
        List<ObjectEntry> all = new ArrayList<ObjectEntry>(ids.size());
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            // could build a full Object, but some implementations won't
            all.add(new SimpleObjectEntry(data, this));
        }

        int fromIndex = skipCount;
        if (fromIndex < 0 || fromIndex > all.size()) {
            hasMoreItems[0] = false;
            return Collections.emptyList();
        }
        if (maxItems == 0) {
            maxItems = all.size();
        }
        int toIndex = skipCount + maxItems;
        if (toIndex > all.size()) {
            toIndex = all.size();
        }
        hasMoreItems[0] = toIndex < all.size();
        return all.subList(fromIndex, toIndex);
    }

    public List<ObjectEntry> getFolderParent(String folderId, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            boolean returnToRoot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getObjectParents(String objectId,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getCheckedoutDocuments(String folderId,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Object Services -----
     */

    public String createDocument(String typeId,
            Map<String, Serializable> properties, String folderId,
            ContentStream contentStream, VersioningState versioningState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String createFolder(String typeId,
            Map<String, Serializable> properties, String folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String createRelationship(String typeId,
            Map<String, Serializable> properties, String sourceId,
            String targetId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String createPolicy(String typeId,
            Map<String, Serializable> properties, String folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<String> getAllowableActions(String objectId, String asUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectEntry getProperties(String objectId,
            ReturnVersion returnVersion, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {
        SimpleData data = repository.datas.get(objectId);
        if (data == null) {
            throw new RuntimeException("Not found: " + objectId); // TODO
        }
        return new SimpleObjectEntry(data, this);
    }

    public CMISObject getObject(String objectId, ReturnVersion returnVersion) {
        // TODO returnVersion
        SimpleData data = repository.datas.get(objectId);
        if (data == null) {
            throw new RuntimeException("Not found: " + objectId); // TODO
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        switch (repository.getType(typeId).getBaseType()) {
        case DOCUMENT:
            return new SimpleDocument(data, this);
        case FOLDER:
            return new SimpleFolder(data, this);
        case RELATIONSHIP:
            return new SimpleRelationship(data, this);
        case POLICY:
            return new SimplePolicy(data, this);
        }
        throw new RuntimeException();
    }

    public InputStream getContentStream(String documentId, int offset,
            int length) {
        byte[] bytes = repository.contentBytes.get(documentId);
        if (bytes == null) {
            return null;
        }
        if (length == -1) {
            length = bytes.length;
        }
        return new ByteArrayInputStream(bytes, offset, length);
    }

    public void setContentStream(String documentId, boolean overwrite,
            ContentStream contentStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteContentStream(String documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String updateProperties(String objectId, String changeToken,
            Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void moveObject(String objectId, String targetFolderId,
            String sourceFolderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void moveObject(ObjectEntry object, ObjectEntry targetFolder,
            ObjectEntry sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(String objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectEntry object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<String> deleteTree(String folderId, Unfiling unfiling,
            boolean continueOnFailure) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<String> deleteTree(ObjectEntry folder, Unfiling unfiling,
            boolean continueOnFailure) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void addObjectToFolder(String objectId, String folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void addObjectToFolder(ObjectEntry object, ObjectEntry folder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removeObjectFromFolder(String objectId, String folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removeObjectFromFolder(ObjectEntry object, ObjectEntry folder) {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> query(String statement,
            boolean searchAllVersions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Versioning Services -----
     */

    public String checkOut(String documentId, boolean[] contentCopied) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CMISObject checkOut(ObjectEntry document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(String documentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut(ObjectEntry document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String checkIn(String documentId, boolean major,
            Map<String, Serializable> properties, ContentStream contentStream,
            String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CMISObject checkIn(ObjectEntry document, boolean major,
            String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean majorVersion, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CMISObject getLatestVersion(ObjectEntry document, boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(ObjectEntry document,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions(String versionSeriesId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions(ObjectEntry document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Relationship Services -----
     */

    public List<ObjectEntry> getRelationships(String objectId,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes, String filter,
            String includeAllowableActions, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getRelationships(ObjectEntry object,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
     * ----- Policy Services -----
     */

    public void applyPolicy(String policyId, String objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void applyPolicy(Policy policy, ObjectEntry object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(String policyId, String objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(Policy policy, ObjectEntry object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAppliedPolicies(String policyId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Policy> getAppliedPolicies(ObjectEntry object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
