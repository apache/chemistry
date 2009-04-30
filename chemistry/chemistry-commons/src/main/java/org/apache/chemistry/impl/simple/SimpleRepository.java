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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.chemistry.Connection;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.repository.JoinCapability;
import org.apache.chemistry.repository.QueryCapability;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.repository.RepositoryCapabilities;
import org.apache.chemistry.repository.RepositoryEntry;
import org.apache.chemistry.repository.RepositoryInfo;
import org.apache.chemistry.type.BaseType;
import org.apache.chemistry.type.ContentStreamPresence;
import org.apache.chemistry.type.Type;

public class SimpleRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    // from the spec
    public static final String ROOT_FOLDER_NAME = "CMIS_Root_Folder";

    public static final String ROOT_TYPE_ID = "Root";

    public static final String DOCUMENT_TYPE_ID = "document";

    public static final String FOLDER_TYPE_ID = "folder";

    public static final String RELATIONSHIP_TYPE_ID = "relationship";

    public static final String POLICY_TYPE_ID = "policy";

    protected static SimpleType ROOT_TYPE = new SimpleType(ROOT_TYPE_ID,
            FOLDER_TYPE_ID, "Root", "Root Folder Type", BaseType.FOLDER, "",
            false, false, false, false, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<SimplePropertyDefinition> emptyList());

    protected static SimpleType DOCUMENT_TYPE = new SimpleType(
            DOCUMENT_TYPE_ID, null, "Document", "Document Type",
            BaseType.DOCUMENT, "", true, true, true, true, true, true,
            ContentStreamPresence.ALLOWED, null, null,
            Collections.<SimplePropertyDefinition> emptyList());

    protected static SimpleType FOLDER_TYPE = new SimpleType(FOLDER_TYPE_ID,
            null, "Folder", "Folder Type", BaseType.FOLDER, "", true, true,
            false, true, true, false, ContentStreamPresence.NOT_ALLOWED, null,
            null, Collections.<SimplePropertyDefinition> emptyList());

    protected static SimpleType RELATIONSHIP_TYPE = new SimpleType(
            RELATIONSHIP_TYPE_ID, null, "Relationship", "Relationship Type",
            BaseType.RELATIONSHIP, "", true, true, false, true, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<SimplePropertyDefinition> emptyList());

    protected static SimpleType POLICY_TYPE = new SimpleType(POLICY_TYPE_ID,
            null, "Policy", "Policy Type", BaseType.POLICY, "", true, true,
            false, true, false, false, ContentStreamPresence.NOT_ALLOWED, null,
            null, Collections.<SimplePropertyDefinition> emptyList());

    public static final String USERNAME = "USERNAME";

    public static final String PASSWORD = "PASSWORD";

    protected static final Set<String> NO_PARENT = Collections.unmodifiableSet(new HashSet<String>());

    private final String name;

    private final String rootFolderId;

    private final Map<String, Type> types;

    /** Map of id -> data */
    protected final Map<String, SimpleData> datas;

    /** Map of id -> contentBytes */
    protected final Map<String, byte[]> contentBytes;

    /** Map of id -> children IDs */
    protected final Map<String, Set<String>> children;

    /** Map of id -> parent IDs, or null if unfiled */
    protected final Map<String, Set<String>> parents;

    @SuppressWarnings("unchecked")
    public SimpleRepository(String name, Collection<SimpleType> types) {
        this.name = name;
        this.types = new HashMap<String, Type>();
        for (Collection<SimpleType> ts : Arrays.asList(getDefaultTypes(), types)) {
            for (Type type : ts) {
                String tid = type.getId();
                if (this.types.containsKey(tid)) {
                    throw new RuntimeException("Type already defined: " + tid);
                }
                this.types.put(tid, type);
            }
        }
        rootFolderId = generateId();

        datas = new ConcurrentHashMap<String, SimpleData>();
        contentBytes = new ConcurrentHashMap<String, byte[]>();
        children = new ConcurrentHashMap<String, Set<String>>();
        parents = new ConcurrentHashMap<String, Set<String>>();

        SimpleData rootData = new SimpleData(ROOT_TYPE_ID);
        rootData.put(Property.ID, rootFolderId);
        rootData.put(Property.NAME, ROOT_FOLDER_NAME);
        datas.put(rootFolderId, rootData);
        children.put(rootFolderId, newSet());
        parents.put(rootFolderId, NO_PARENT);
    }

    // private final AtomicLong idCounter = new AtomicLong(0);

    protected String generateId() {
        return UUID.randomUUID().toString();
        // return "ID_" + idCounter.incrementAndGet();
    }

    protected Collection<SimpleType> getDefaultTypes() {
        return Arrays.asList(DOCUMENT_TYPE, FOLDER_TYPE, RELATIONSHIP_TYPE,
                POLICY_TYPE, ROOT_TYPE);
    }

    protected Set<String> newSet() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    /*
     * ----- RepositoryEntry -----
     */

    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public URI getURI() {
        // TODO Return a URI
        return null;
    }

    public String getRelationshipName() {
        return null;
    }

    /*
     * ----- Repository -----
     */

    public Connection getConnection(Map<String, Serializable> parameters) {
        // TODO credentials
        return new SimpleConnection(this);
    }

    public RepositoryInfo getInfo() {
        return this;
    }

    public Type getType(String typeId) {
        return types.get(typeId);
    }

    public Collection<Type> getTypes(String typeId,
            boolean returnPropertyDefinitions) {
        // TODO always returns property definitions for now
        if (typeId == null) {
            return Collections.unmodifiableCollection(types.values());
        }
        if (!types.containsKey(typeId)) {
            return null; // TODO
        }
        // TODO return all descendants as well
        return Collections.singleton(types.get(typeId));
    }

    public List<Type> getTypes(String typeId,
            boolean returnPropertyDefinitions, int maxItems, int skipCount,
            boolean[] hasMoreItems) {
        if (maxItems < 0) {
            throw new IllegalArgumentException(String.valueOf(maxItems));
        }
        if (skipCount < 0) {
            throw new IllegalArgumentException(String.valueOf(skipCount));
        }
        if (hasMoreItems.length < 1) {
            throw new IllegalArgumentException(
                    "hasMoreItems parameter too small");
        }

        Collection<Type> t = getTypes(typeId, returnPropertyDefinitions);
        if (t == null) {
            hasMoreItems[0] = false;
            return Collections.emptyList();
        }
        List<Type> all = new ArrayList<Type>(t);
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

    /*
     * ----- RepositoryInfo -----
     */

    public String getDescription() {
        return "Repository " + name;
    }

    public String getRootFolderId() {
        return rootFolderId;
    }

    public String getVendorName() {
        return "Nuxeo";
    }

    public String getProductName() {
        return "Chemistry Simple Repository";
    }

    public String getProductVersion() {
        // TODO update this when releasing
        return "0.1-SNAPSHOT";
    }

    public String getVersionSupported() {
        // TODO may be overriden by generic client layer
        return "0.51";
    }

    public org.w3c.dom.Document getRepositorySpecificInformation() {
        return null;
    }

    public RepositoryCapabilities getCapabilities() {
        return this;
    }

    public Collection<RepositoryEntry> getRelatedRepositories() {
        return Collections.emptySet();
    }

    /*
     * ----- RepositoryCapabilities -----
     */

    public boolean hasMultifiling() {
        return false;
    }

    public boolean hasUnfiling() {
        return false;
    }

    public boolean hasVersionSpecificFiling() {
        return false;
    }

    public boolean isPWCUpdatable() {
        return false;
    }

    public boolean isPWCSearchable() {
        return false;
    }

    public boolean isAllVersionsSearchable() {
        return false;
    }

    public JoinCapability getJoinCapability() {
        return JoinCapability.NO_JOIN;
    }

    public QueryCapability getQueryCapability() {
        return QueryCapability.BOTH_COMBINED;
    }

    public <T> T getExtension(Class<T> klass) {
        return null; // Not Supported
    }
}
