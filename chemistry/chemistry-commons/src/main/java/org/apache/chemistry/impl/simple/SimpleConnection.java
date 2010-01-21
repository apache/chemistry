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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.namespace.QName;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.chemistry.ACE;
import org.apache.chemistry.ACLPropagation;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
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
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.Rendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.cmissql.CmisSqlLexer;
import org.apache.chemistry.cmissql.CmisSqlParser;
import org.apache.chemistry.util.GregorianCalendar;

public class SimpleConnection implements Connection, SPI {

    protected final SimpleRepository repository;

    protected final SimpleFolder rootFolder;

    public SimpleConnection(SimpleRepository repository) {
        this.repository = repository;
        rootFolder = (SimpleFolder) getObject(repository.getRootFolderId());
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
     * Accumulates descendants into a list recursively.
     */
    // TODO optimized paging
    protected void accumulate(ObjectId folder, int depth, Inclusion inclusion,
            String orderBy, BaseType baseType, List<ObjectEntry> list) {
        List<ObjectEntry> children = getChildren(folder, inclusion, orderBy,
                null);
        for (ObjectEntry child : children) {
            BaseType childBaseType = child.getBaseType();
            if (baseType == null || baseType == childBaseType) {
                list.add(child);
            }
            if (childBaseType == BaseType.FOLDER && depth != 1) {
                accumulate(child, depth - 1, inclusion, orderBy, baseType, list);
            }
        }
    }

    public List<ObjectEntry> getFolderTree(ObjectId folder, int depth,
            Inclusion inclusion) {
        checkFolder(folder);
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulate(folder, depth, inclusion, null, BaseType.FOLDER, list);
        return list;
    }

    public List<ObjectEntry> getDescendants(ObjectId folder, int depth,
            String orderBy, Inclusion inclusion) {
        checkFolder(folder);
        List<ObjectEntry> list = new ArrayList<ObjectEntry>();
        accumulate(folder, depth, inclusion, orderBy, null, list);
        return list;
    }

    public ListPage<ObjectEntry> getChildren(ObjectId folder,
            Inclusion inclusion, String orderBy, Paging paging) {
        // TODO orderBy, inclusion
        checkFolder(folder);
        Set<String> ids = repository.children.get(folder.getId());
        List<ObjectEntry> all = new ArrayList<ObjectEntry>(ids.size());
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            all.add(new SimpleObjectEntry(data, this));
        }
        return SimpleListPage.fromPaging(all, paging);
    }

    protected void checkFolder(ObjectId object) throws ObjectNotFoundException,
            ConstraintViolationException {
        String id = object.getId();
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new ObjectNotFoundException(id);
        }
        String baseTypeId = (String) data.get(Property.BASE_TYPE_ID);
        if (baseTypeId != BaseType.FOLDER.getId()) {
            throw new IllegalArgumentException("Not a folder: " + id);
        }
    }

    public ObjectEntry getFolderParent(ObjectId folder, String filter) {
        // TODO filter
        String folderId = folder.getId();
        SimpleData data = repository.datas.get(folderId);
        if (data == null) {
            throw new RuntimeException("No such folder: " + folder);
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        Type type = repository.getType(typeId);
        if (!type.getBaseType().equals(BaseType.FOLDER)) {
            throw new IllegalArgumentException("Not a folder: " + folder);
        }
        Set<String> parents = repository.parents.get(folderId);
        if (parents == null || parents.isEmpty()) {
            return null;
        }
        if (parents.size() > 1) {
            throw new ConstraintViolationException(folder + " has "
                    + parents.size() + " parents");
        }
        String parentId = parents.iterator().next();
        return new SimpleObjectEntry(repository.datas.get(parentId), this);
    }

    public Collection<ObjectEntry> getObjectParents(ObjectId object,
            String filter) {
        // TODO filter
        Set<String> ids = repository.parents.get(object.getId());
        List<ObjectEntry> parents = new ArrayList<ObjectEntry>(ids.size());
        for (String id : ids) {
            SimpleData data = repository.datas.get(id);
            parents.add(new SimpleObjectEntry(data, this));
        }
        return parents;
    }

    public ListPage<ObjectEntry> getCheckedOutDocuments(ObjectId folder,
            Inclusion inclusion, Paging paging) {
        // TODO Auto-generated method stub
        return SimpleListPage.emptyList();
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
                } else if (Property.PATH.equals(id)) {
                    update.put(Property.PATH, "XXX"); // TODO
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

    public ObjectId createDocument(Map<String, Serializable> properties,
            ObjectId folder, ContentStream contentStream,
            VersioningState versioningState) {
        // TODO contentStream, versioningState
        String typeId = (String) properties.get(Property.TYPE_ID);
        if (typeId == null) {
            // use a default type, useful for pure AtomPub POST
            typeId = BaseType.DOCUMENT.getId();
            properties.put(Property.TYPE_ID, typeId);
            // throw new IllegalArgumentException("Missing object type id");
        }
        Type type = repository.getType(typeId);
        if (type == null || type.getBaseType() != BaseType.DOCUMENT) {
            throw new IllegalArgumentException(typeId);
        }
        SimpleData data = new SimpleData(typeId, type.getBaseType());
        data.putAll(properties);
        // TODO check presence allowed
        if (contentStream != null) {
            data.put(Property.CONTENT_STREAM_LENGTH,
                    Integer.valueOf((int) contentStream.getLength())); // TODO-Long
            String mt = contentStream.getMimeType();
            if (mt != null) {
                data.put(Property.CONTENT_STREAM_MIME_TYPE, mt);
            }
            String fn = contentStream.getFileName();
            if (fn != null) {
                data.put(Property.CONTENT_STREAM_FILE_NAME, fn);
            }
            try {
                data.put(SimpleProperty.CONTENT_BYTES_KEY,
                        SimpleContentStream.getBytes(contentStream.getStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (folder != null) {
            data.put(Property.PARENT_ID, folder.getId());
        }
        saveData(data, typeId);
        return new SimpleObjectId((String) data.get(Property.ID));
    }

    public ObjectId createFolder(Map<String, Serializable> properties,
            ObjectId folder) {
        String typeId = (String) properties.get(Property.TYPE_ID);
        if (typeId == null) {
            throw new IllegalArgumentException("Missing object type id");
        }
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
        return SimpleListPage.emptyList();
    }

    public ObjectEntry getProperties(ObjectId object, Inclusion inclusion) {
        // TODO filter, includeAllowableActions, includeRelationships
        SimpleData data = repository.datas.get(object.getId());
        if (data == null) {
            return null;
        }
        return new SimpleObjectEntry(data, this);
    }

    public ObjectEntry getObjectByPath(String path, Inclusion inclusion) {
        // TODO filter, includeAllowableActions, includeRelationships
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with / : "
                    + path);
        }
        if (!path.equals("/") && path.endsWith("/")) {
            throw new IllegalArgumentException("Path must not end with / : "
                    + path);
        }
        String id = repository.getRootFolderId().getId();
        String[] segments = path.substring(1).split("/");
        if (!path.equals("/")) {
            for (String segment : segments) {
                if ("".equals(segment)) {
                    throw new IllegalArgumentException(
                            "Path must not contain // : " + path);
                }
                String foundId = null;
                for (String childId : repository.children.get(id)) {
                    SimpleData data = repository.datas.get(childId);
                    String name = (String) data.get(Property.NAME);
                    if (segment.equals(name)) {
                        foundId = childId;
                        break;
                    }
                }
                if (foundId == null) {
                    // not found
                    return null;
                }
                id = foundId;
            }
        }
        return new SimpleObjectEntry(repository.datas.get(id), this);
    }

    public Folder getFolder(String path) {
        SimpleObjectEntry entry = (SimpleObjectEntry) getObjectByPath(path,
                null);
        if (entry == null) {
            return null;
        }
        if (entry.getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException("Not a folder: " + path);
        }
        return new SimpleFolder(entry);
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

    public ListPage<Rendition> getRenditions(ObjectId object,
            Inclusion inclusion, Paging paging) {
        return SimpleListPage.emptyList();
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
        String filename = (String) data.get(Property.CONTENT_STREAM_FILE_NAME);
        return new SimpleContentStream(bytes, mimeType, filename);
    }

    public ObjectId setContentStream(ObjectId document,
            ContentStream contentStream, boolean overwrite) {
        SimpleData data = repository.datas.get(document.getId());
        if (contentStream == null) {
            data.remove(SimpleProperty.CONTENT_BYTES_KEY);
            data.remove(Property.CONTENT_STREAM_MIME_TYPE);
            data.remove(Property.CONTENT_STREAM_FILE_NAME);
            data.remove(Property.CONTENT_STREAM_LENGTH);
        } else {
            byte[] bytes;
            try {
                bytes = SimpleContentStream.getBytes(contentStream.getStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            data.put(SimpleProperty.CONTENT_BYTES_KEY, bytes);
            data.put(Property.CONTENT_STREAM_LENGTH,
                    Integer.valueOf(bytes.length)); // TODO-Long
            String mt = contentStream.getMimeType();
            if (mt == null) {
                data.remove(Property.CONTENT_STREAM_MIME_TYPE);
            } else {
                data.put(Property.CONTENT_STREAM_MIME_TYPE, mt);
            }
            String fn = contentStream.getFileName();
            if (fn == null) {
                data.remove(Property.CONTENT_STREAM_FILE_NAME);
            } else {
                data.put(Property.CONTENT_STREAM_FILE_NAME, fn);
            }
        }
        return document;
    }

    public ObjectId deleteContentStream(ObjectId document) {
        return setContentStream(document, null, true);
    }

    public ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties) {
        // TODO changeToken
        SimpleData data = repository.datas.get(object.getId());
        String typeId = (String) data.get(Property.TYPE_ID);
        Type type = repository.getType(typeId);
        for (String key : properties.keySet()) {
            if (key.equals(Property.ID) || key.equals(Property.TYPE_ID)) {
                continue;
            }
            PropertyDefinition pd = type.getPropertyDefinition(key);
            Updatability updatability = pd.getUpdatability();
            if (updatability == Updatability.ON_CREATE
                    || updatability == Updatability.READ_ONLY) {
                // ignore attempts to write a read-only prop, as clients
                // may want to take an existing entry, change a few values,
                // and write the new one
                continue;
                // throw new RuntimeException("Read-only property: " + key);
            }
            Serializable value = properties.get(key);
            if (value == null) {
                if (pd.isRequired()) {
                    throw new RuntimeException("Required property: " + key); // TODO
                }
                data.remove(key);
            } else {
                data.put(key, value);
            }
        }
        return object;
    }

    public ObjectId moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder) {
        String id = object.getId();
        if (repository.rootId.equals(id)) {
            throw new IllegalArgumentException("Cannot move root");
        }
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new ObjectNotFoundException(object.getId());
        }
        checkFolder(targetFolder);
        Set<String> parents = repository.parents.get(id);
        String sourceFolderId;
        if (sourceFolder == null) {
            if (parents.size() > 1) {
                throw new ConstraintViolationException("Object "
                        + object.getId() + " has " + parents.size()
                        + " parents");
            } else if (!parents.isEmpty()) {
                sourceFolderId = parents.iterator().next();
            } else {
                sourceFolderId = null;
            }
        } else {
            sourceFolderId = sourceFolder.getId();
            if (!parents.contains(sourceFolderId)) {
                throw new ConstraintViolationException("Object " + id
                        + " is not filed in " + sourceFolderId);
            }
        }
        if (sourceFolderId != null) {
            parents.remove(sourceFolderId);
            repository.children.get(sourceFolderId).remove(id);
        }
        String targetFolderId = targetFolder.getId();
        parents.add(targetFolderId);
        repository.children.get(targetFolderId).add(id);
        return object;
    }

    public void deleteObject(ObjectId object, boolean allVersions) {
        // TODO allVersions
        String id = object.getId();
        if (repository.rootId.equals(id)) {
            throw new IllegalArgumentException("Cannot delete root");
        }
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new ObjectNotFoundException(object.getId());
        }
        // delete children info
        Set<String> children = repository.children.get(id);
        if (children != null) {
            if (children.size() > 0) {
                throw new ConstraintViolationException(
                        "Cannot delete, folder has children: " + object);
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
            throw new IllegalArgumentException("Cannot delete root");
        }
        SimpleData data = repository.datas.get(id);
        if (data == null) {
            throw new ObjectNotFoundException("No such folder: "
                    + folder.getId());
        }
        String typeId = (String) data.get(Property.TYPE_ID);
        if (repository.getType(typeId).getBaseType() != BaseType.FOLDER) {
            throw new IllegalArgumentException("Not a folder: "
                    + folder.getId());
        }
        for (String childId : new ArrayList<String>(repository.children.get(id))) {
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

    public ListPage<ObjectEntry> query(String statement,
            boolean searchAllVersions, Inclusion inclusion, Paging paging) {
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
        return SimpleListPage.fromPaging(all, paging);
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
            CmisSqlParser parser = new CmisSqlParser(tokens);
            CmisSqlParser.query_return query = parser.query();
            if (parser.errorMessage != null) {
                throw new CMISRuntimeException("Cannot parse query: "
                        + statement + " (" + parser.errorMessage + ")");
            }
            CommonTree tree = (CommonTree) query.getTree();
            CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
            nodes.setTokenStream(tokens);
            CmisSqlSimpleWalker walker = new CmisSqlSimpleWalker(nodes);
            CmisSqlSimpleWalker.query_return res = walker.query(data, this);
            if (walker.errorMessage != null) {
                throw new CMISRuntimeException("Cannot parse query: "
                        + statement + " (" + walker.errorMessage + ")");
            }
            return res;
        } catch (IOException e) {
            throw new CMISRuntimeException(e.getMessage(), e);
        } catch (RecognitionException e) {
            throw new CMISRuntimeException("Cannot parse query: " + statement,
                    e);
        }
    }

    // IN_FOLDER
    protected boolean isInFolder(SimpleData data, Object folderId) {
        if (!(folderId instanceof String)) {
            throw new IllegalArgumentException(folderId.toString());
        }
        Set<String> children = repository.children.get(folderId);
        if (children == null) {
            return false; // no such id
        }
        return children.contains(data.get(Property.ID));
    }

    // IN_TREE
    protected boolean isInTree(SimpleData data, Object folderId) {
        if (!(folderId instanceof String)) {
            throw new IllegalArgumentException(folderId.toString());
        }
        String id = (String) data.get(Property.ID);
        if (id == null) {
            return false; // no such id
        }
        Queue<String> todo = new LinkedList<String>(Collections.singleton(id));
        while (!todo.isEmpty()) {
            String cur = todo.remove();
            Set<String> parents = repository.parents.get(cur);
            for (String pid : parents) {
                if (pid.equals(folderId)) {
                    return true;
                }
            }
            todo.addAll(parents);
        }
        return false;
    }

    // CONTAINS
    protected boolean fulltextContains(SimpleData data, List<Object> args) {
        // String qual;
        String query;
        if (args.size() == 2) {
            // qual = (String) args.get(0);
            query = (String) args.get(1);
        } else {
            // qual = null;
            query = (String) args.get(0);
        }
        Set<String> words = SimpleFulltext.parseFulltext(data);
        return SimpleFulltext.matchesFullText(words, query);
    }

    public Collection<CMISObject> query(String statement,
            boolean searchAllVersions) {
        ListPage<ObjectEntry> res = query(statement, searchAllVersions, null,
                null);
        List<CMISObject> objects = new ArrayList<CMISObject>(res.size());
        for (ObjectEntry e : res) {
            objects.add(SimpleObject.construct((SimpleObjectEntry) e));
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
        return SimpleListPage.emptyList();
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

    public Collection<ObjectEntry> getAppliedPolicies(ObjectId policy,
            String filter) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    /*
     * ----- ACL Services -----
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
