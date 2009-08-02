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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
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
import org.apache.chemistry.Rendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.cmissql.CmisSqlLexer;
import org.apache.chemistry.cmissql.CmisSqlParser;
import org.apache.chemistry.util.GregorianCalendar;

public class SimpleConnection implements Connection, SPI {

    protected final SimpleRepository repository;

    protected final SimpleFolder rootFolder;

    public SimpleConnection(SimpleRepository repository) {
        this.repository = repository;
        rootFolder = (SimpleFolder) getObject(repository.getInfo().getRootFolderId());
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
        BaseType baseType = repository.getType(typeId).getBaseType();
        return new SimpleObjectEntry(new SimpleData(typeId, baseType), this);
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

    public List<ObjectEntry> getDescendants(ObjectId folder, int depth,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, String orderBy) {
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulateDescendants(folder, depth, filter, includeAllowableActions,
                includeRelationships, orderBy, list);
        return list;
    }

    public List<ObjectEntry> getChildren(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            int maxItems, int skipCount, String orderBy, boolean[] hasMoreItems) {
        // TODO orderBy
        Set<String> ids = repository.children.get(folder.getId());
        List<ObjectEntry> all = new ArrayList<ObjectEntry>(ids.size());
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            all.add(new SimpleObjectEntry(data, this));
        }
        return subList(all, maxItems, skipCount, hasMoreItems);
    }

    /**
     * Extracts part of a list according to given parameters.
     */
    protected static List<ObjectEntry> subList(List<ObjectEntry> all,
            int maxItems, int skipCount, boolean[] hasMoreItems) {
        int total = all.size();
        int fromIndex = skipCount;
        if (fromIndex < 0 || fromIndex > total) {
            hasMoreItems[0] = false;
            return Collections.emptyList();
        }
        if (maxItems <= 0) {
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
        // TODO filter, includeRelationship, includeAllowableActions
        List<ObjectEntry> result = new LinkedList<ObjectEntry>();
        SimpleData data = repository.datas.get(folder.getId());
        if (data == null) {
            throw new RuntimeException("No such folder: " + folder);
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        Type type = repository.getType(typeId);
        if (!type.getBaseType().equals(BaseType.FOLDER)) {
            throw new IllegalArgumentException("Not a folder: " + folder);
        }
        String currentId = (String) data.get(Property.ID);
        do {
            Set<String> parents = repository.parents.get(currentId);
            if (parents == null || parents.isEmpty()) {
                break;
            }
            if (parents.size() > 1) {
                throw new AssertionError(currentId + " has " + parents.size()
                        + " parents");
            }
            currentId = parents.iterator().next();
            data = repository.datas.get(currentId);
            result.add(new SimpleObjectEntry(data, this));
        } while (returnToRoot);
        return result;
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId object,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships) {
        // TODO includeAllowableActions and includeRelationships
        // TODO filter
        Set<String> ids = repository.parents.get(object.getId());
        List<ObjectEntry> parents = new ArrayList<ObjectEntry>(ids.size());
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            parents.add(new SimpleObjectEntry(data, this));
        }
        return parents;
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

    // Called by SimpleObject.save() for new objects.
    protected void saveObject(SimpleObject object) {
        saveData(object.entry.data, object.getTypeId());
    }

    protected void saveData(SimpleData data, String typeId) {
        Map<String, Serializable> update = new HashMap<String, Serializable>();

        // generate an ID
        String objectId = repository.generateId();
        update.put(Property.ID, objectId);

        // check mandatory properties
        Type type = repository.getType(typeId);
        for (PropertyDefinition pd : type.getPropertyDefinitions()) {
            String id = pd.getId();
            if (Property.ID.equals(id)) {
                // ignore, set later
                continue;
            }
            if (pd.isRequired() && !data.containsKey(id)) {
                if (Property.NAME.equals(id)) {
                    update.put(Property.NAME, objectId);
                } else if (Property.CREATED_BY.equals(id)) {
                    update.put(Property.CREATED_BY, "system"); // TODO
                } else if (Property.CREATION_DATE.equals(id)) {
                    update.put(Property.CREATION_DATE,
                            GregorianCalendar.getInstance());
                } else if (Property.LAST_MODIFIED_BY.equals(id)) {
                    update.put(Property.LAST_MODIFIED_BY, "system"); // TODO
                } else if (Property.LAST_MODIFICATION_DATE.equals(id)) {
                    update.put(Property.LAST_MODIFICATION_DATE,
                            GregorianCalendar.getInstance());
                } else if (Property.IS_LATEST_VERSION.equals(id)) {
                    update.put(Property.IS_LATEST_VERSION, Boolean.TRUE);
                } else if (Property.IS_LATEST_MAJOR_VERSION.equals(id)) {
                    update.put(Property.IS_LATEST_MAJOR_VERSION, Boolean.TRUE);
                } else if (Property.IS_VERSION_SERIES_CHECKED_OUT.equals(id)) {
                    update.put(Property.IS_VERSION_SERIES_CHECKED_OUT,
                            Boolean.FALSE);
                } else if (Property.VERSION_SERIES_ID.equals(id)) {
                    update.put(Property.VERSION_SERIES_ID, objectId);
                } else if (Property.VERSION_LABEL.equals(id)) {
                    update.put(Property.VERSION_LABEL, "1.0");
                } else {
                    throw new RuntimeException("Missing property: " + id); // TODO
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
        repository.datas.put(objectId, data); // TODO clone data?

        // parents/children
        String parentId = (String) data.get(Property.PARENT_ID);
        if (type.getBaseType() == BaseType.FOLDER) {
            // new folder, empty set of children
            repository.children.put(objectId, repository.newSet());
        } else {
            // only folders have this property
            data.remove(Property.PARENT_ID);
        }
        if (parentId != null) {
            // this object is filed
            // pointer to parent
            Set<String> parents = repository.newSet();
            parents.add(parentId);
            repository.parents.put(objectId, parents);
            // new pointer to child
            repository.children.get(parentId).add(objectId);
        }
    }

    public ObjectId createDocument(String typeId,
            Map<String, Serializable> properties, ObjectId folder,
            ContentStream contentStream, VersioningState versioningState) {
        // TODO contentStream, versioningState
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId, type.getBaseType());
        data.putAll(properties);
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        saveData(data, typeId);
        return new SimpleObjectId((String) data.get(Property.ID));
    }

    public ObjectId createFolder(String typeId,
            Map<String, Serializable> properties, ObjectId folder) {
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId, type.getBaseType());
        data.putAll(properties);
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        saveData(data, typeId);
        return new SimpleObjectId((String) data.get(Property.ID));
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
        SimpleData data = repository.datas.get(object.getId());
        if (data == null) {
            return null;
        }
        return new SimpleObjectEntry(data, this);
    }

    public CMISObject getObject(ObjectId object) {
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

    public List<Rendition> getRenditions(ObjectId object, String filter,
            int maxItems, int skipCount) {
        return Collections.emptyList();
    }

    public boolean hasContentStream(ObjectId document) {
        SimpleData data = repository.datas.get(document.getId());
        byte[] bytes = (byte[]) data.get(SimpleProperty.CONTENT_BYTES_KEY);
        return bytes != null;
    }

    public ContentStream getContentStream(ObjectId object,
            String contentStreamId) {
        // TODO contentStreamId
        SimpleData data = repository.datas.get(object.getId());
        byte[] bytes = (byte[]) data.get(SimpleProperty.CONTENT_BYTES_KEY);
        if (bytes == null) {
            return null;
        }
        // length is recomputed, no need to read it
        String mimeType = (String) data.get(Property.CONTENT_STREAM_MIME_TYPE);
        String filename = (String) data.get(Property.CONTENT_STREAM_FILENAME);
        return new SimpleContentStream(bytes, mimeType, filename);
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
        // TODO allVersions
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
        boolean allVersions = false; // TODO add in signature?
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
        for (String childId : repository.children.get(id)) {
            SimpleData childData = repository.datas.get(childId);
            String childTypeId = (String) childData.get(Property.TYPE_ID);
            ObjectId objectId = new SimpleObjectId(childId);
            if (repository.getType(childTypeId).getBaseType() == BaseType.FOLDER) {
                deleteTree(objectId, unfiling, continueOnFailure);
            } else {
                deleteObject(objectId, allVersions);
            }
        }
        deleteObject(folder, false);
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

    public Collection<ObjectEntry> query(String statement,
            boolean searchAllVersions, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        // this implementation doesn't try to be very efficient...
        List<ObjectEntry> all = new ArrayList<ObjectEntry>();
        String tableName = null;
        for (SimpleData data : repository.datas.values()) {
            if (tableName != null) {
                // type already available: check early
                if (!typeMatches(tableName, (String) data.get(Property.TYPE_ID))) {
                    continue;
                }
            }
            CmisSqlSimpleWalker.query_return ret = queryData(statement, data);
            if (tableName == null) {
                // first time: check late
                tableName = ret.tableName.toLowerCase();
                if (!typeMatches(tableName, (String) data.get(Property.TYPE_ID))) {
                    continue;
                }
            }
            if (ret.matches) {
                all.add(new SimpleObjectEntry(data, this));
            }
        }
        return subList(all, maxItems, skipCount, hasMoreItems);
    }

    protected boolean typeMatches(String tableName, String typeId) {
        do {
            Type type = repository.getType(typeId);
            if (tableName.equals(type.getQueryName().toLowerCase())) {
                return true;
            }
            // check parent type
            typeId = type.getParentId();
        } while (typeId != null);
        return false;
    }

    protected CmisSqlSimpleWalker.query_return queryData(String statement,
            SimpleData data) {
        try {
            CharStream input = new ANTLRInputStream(new ByteArrayInputStream(
                    statement.getBytes("UTF-8")));
            TokenSource lexer = new CmisSqlLexer(input);
            TokenStream tokens = new CommonTokenStream(lexer);
            CommonTree tree = (CommonTree) new CmisSqlParser(tokens).query().getTree();
            CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
            nodes.setTokenStream(tokens);
            return new CmisSqlSimpleWalker(nodes).query(data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (RecognitionException e) {
            throw new RuntimeException("Cannot parse query: " + statement, e);
        }
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        boolean[] hasMoreItems = new boolean[1];
        Collection<ObjectEntry> res = query(statement, searchAllVersions,
                false, false, 0, 0, hasMoreItems);
        List<CMISObject> objects = new ArrayList<CMISObject>(res.size());
        for (ObjectEntry e : res) {
            objects.add(SimpleObject.construct((SimpleObjectEntry) e));
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

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId policy,
            String filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
