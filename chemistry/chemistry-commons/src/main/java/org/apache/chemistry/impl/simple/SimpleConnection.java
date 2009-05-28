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
 *     Florent Guillaume, Nuxeo
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.ReturnVersion;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;

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

    /*
     * ----- Factories -----
     */

    public ObjectId newObjectId(String id) {
        return new SimpleObjectId(id);
    }

    public SimpleObjectEntry newObjectEntry(String typeId) {
        return new SimpleObjectEntry(new SimpleData(typeId), this);
    }

    public Document newDocument(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleObjectEntry entry = newObjectEntry(typeId);
        if (folder != null) {
            entry.setValue(Property.PARENT_ID, folder.getId());
        }
        return new SimpleDocument(entry);
    }

    public Folder newFolder(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleObjectEntry entry = newObjectEntry(typeId);
        if (folder != null) {
            entry.setValue(Property.PARENT_ID, folder.getId());
        }
        return new SimpleFolder(entry);
    }

    public Relationship newRelationship(String typeId) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.RELATIONSHIP) {
            throw new IllegalArgumentException(typeId);
        }
        return new SimpleRelationship(newObjectEntry(typeId));
    }

    public Policy newPolicy(String typeId, Folder folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.POLICY) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleObjectEntry entry = newObjectEntry(typeId);
        if (folder != null) {
            entry.setValue(Property.PARENT_ID, folder.getId());
        }
        return new SimplePolicy(entry);
    }

    /*
     * Called by save() for new objects.
     */
    protected void saveObject(SimpleObject object) {
        SimpleData data = object.entry.data;
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

        // content stream
        byte[] bytes = (byte[]) data.get(SimpleProperty.CONTENT_BYTES_KEY);
        if (type.getContentStreamAllowed() == ContentStreamPresence.REQUIRED
                && bytes == null) {
            throw new RuntimeException("Content stream required"); // TODO
        }
        update.put(Property.CONTENT_STREAM_LENGTH, bytes == null ? null
                : Integer.valueOf(bytes.length)); // TODO Long

        // update data once we know there's no error
        for (String key : update.keySet()) {
            Serializable value = update.get(key);
            if (value == null) {
                data.remove(key);
            } else {
                data.put(key, value);
            }
        }

        // properties
        repository.datas.put(id, data); // TODO clone data?

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

    public List<ObjectEntry> getDescendants(ObjectId folder, BaseType type,
            int depth, String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<ObjectEntry> getChildren(ObjectId folder, BaseType type,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            String orderBy, boolean[] hasMoreItems) {
        // TODO type and orderBy
        Set<String> ids = repository.children.get(folder.getId());
        int total = ids.size();
        List<ObjectEntry> all = new ArrayList<ObjectEntry>(total);
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            // could build a full Object, but some implementations won't
            all.add(new SimpleObjectEntry(data, this));
        }

        int fromIndex = skipCount;
        if (fromIndex < 0 || fromIndex > total) {
            hasMoreItems[0] = false;
            return Collections.emptyList();
        }
        if (maxItems == 0) {
            maxItems = total;
        }
        int toIndex = skipCount + maxItems;
        if (toIndex > total) {
            toIndex = total;
        }
        hasMoreItems[0] = toIndex < total;
        if (fromIndex == 0 && toIndex == total) {
            return all;
        } else {
            return all.subList(fromIndex, toIndex);
        }
    }

    public List<ObjectEntry> getFolderParent(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            boolean returnToRoot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId object,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

    public ObjectEntry getProperties(ObjectId object,
            ReturnVersion returnVersion, String filter,
            boolean includeAllowableActions, boolean includeRelationships) {
        SimpleData data = repository.datas.get(object.getId());
        if (data == null) {
            return null;
        }
        return new SimpleObjectEntry(data, this);
    }

    public CMISObject getObject(ObjectId object, ReturnVersion returnVersion) {
        // TODO returnVersion
        SimpleData data = repository.datas.get(object.getId());
        if (data == null) {
            return null;
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        switch (repository.getType(typeId).getBaseType()) {
        case DOCUMENT:
            return new SimpleDocument(new SimpleObjectEntry(data, this));
        case FOLDER:
            return new SimpleFolder(new SimpleObjectEntry(data, this));
        case RELATIONSHIP:
            return new SimpleRelationship(new SimpleObjectEntry(data, this));
        case POLICY:
            return new SimplePolicy(new SimpleObjectEntry(data, this));
        default:
            throw new AssertionError(typeId);
        }
    }

    public boolean hasContentStream(ObjectId document) {
        SimpleData data = repository.datas.get(document.getId());
        byte[] bytes = (byte[]) data.get(SimpleProperty.CONTENT_BYTES_KEY);
        return bytes != null;
    }

    public InputStream getContentStream(ObjectId document, int offset,
            int length) {
        SimpleData data = repository.datas.get(document.getId());
        byte[] bytes = (byte[]) data.get(SimpleProperty.CONTENT_BYTES_KEY);
        if (bytes == null) {
            return null;
        }
        if (length == -1) {
            length = bytes.length;
        }
        return new ByteArrayInputStream(bytes, offset, length);
    }

    public ObjectId setContentStream(ObjectId document, boolean overwrite,
            ContentStream contentStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteContentStream(ObjectId document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteObject(ObjectId object) {
        String id = object.getId();
        if (repository.rootId.equals(id)) {
            throw new RuntimeException("Cannot delete root"); // TODO
        }
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new RuntimeException("Not found: " + object); // TODO
        }
        // delete children info
        Set<String> children = repository.children.get(id);
        if (children != null) {
            if (children.size() > 0) {
                throw new RuntimeException(
                        "Cannot delete, folder has children: " + object); // TODO
            }
            // remove only if empty
            repository.children.remove(id);
        }
        // delete parents info
        // TODO unfiling, remove from all parents for now
        Set<String> parents = repository.parents.remove(id);
        if (parents != null) {
            for (String pid : parents) {
                // remove as child of parent
                repository.children.get(pid).remove(id);
            }
        }
        repository.datas.remove(id);
    }

    public Collection<ObjectId> deleteTree(ObjectId folder, Unfiling unfiling,
            boolean continueOnFailure) {
        // TODO unfiling
        // TODO continueOnFailure
        String id = folder.getId();
        if (repository.rootId.equals(id)) {
            throw new RuntimeException("Cannot delete root"); // TODO
        }
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new RuntimeException("Not found: " + folder); // TODO
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        if (repository.getType(typeId).getBaseType() != BaseType.FOLDER) {
            throw new RuntimeException("Not a folder: " + folder); // TODO
        }
        Set<ObjectId> deletedIds = new HashSet<ObjectId>();
        for (String childId : repository.children.get(id)) {
            SimpleData childData = repository.datas.get(childId);
            String childTypeId = (String) childData.get(Property.TYPE_ID);
            ObjectId objectId = new SimpleObjectId(childId);
            if (repository.getType(childTypeId).getBaseType() == BaseType.FOLDER) {
                deletedIds.addAll(deleteTree(objectId, unfiling,
                        continueOnFailure));
            } else {
                deleteObject(objectId);
                deletedIds.add(objectId);
            }
        }
        deleteObject(folder);
        deletedIds.add(new SimpleObjectId(id));
        return deletedIds;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
            String versionSeriesId, boolean majorVersion, String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getAllVersions(String versionSeriesId,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions(String versionSeriesId) {
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

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId policy,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
