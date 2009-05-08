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
package org.apache.chemistry.impl.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.Type;
import org.apache.chemistry.impl.simple.SimpleProperty;
import org.apache.chemistry.impl.simple.SimpleType;

/**
 * Base implementation of a {@link Repository}. The implemented methods are
 * completely generic and base on {@link SimpleType} and {@link SimpleProperty}.
 *
 * @author Florent Guillaume
 */
public abstract class BaseRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    // from the spec
    public static final String ROOT_FOLDER_NAME = "CMIS_Root_Folder";

    public static final String ROOT_TYPE_ID = "Root";

    public static final String DOCUMENT_TYPE_ID = "document";

    public static final String FOLDER_TYPE_ID = "folder";

    public static final String RELATIONSHIP_TYPE_ID = "relationship";

    public static final String POLICY_TYPE_ID = "policy";

    public static SimpleType ROOT_TYPE = new SimpleType(ROOT_TYPE_ID,
            FOLDER_TYPE_ID, "Root", "Root Folder Type", BaseType.FOLDER, "",
            false, false, false, false, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    public static SimpleType DOCUMENT_TYPE = new SimpleType(DOCUMENT_TYPE_ID,
            null, "Document", "Document Type", BaseType.DOCUMENT, "", true,
            true, true, true, true, true, ContentStreamPresence.ALLOWED, null,
            null, Collections.<PropertyDefinition> emptyList());

    public static SimpleType FOLDER_TYPE = new SimpleType(FOLDER_TYPE_ID, null,
            "Folder", "Folder Type", BaseType.FOLDER, "", true, true, false,
            true, true, false, ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    public static SimpleType RELATIONSHIP_TYPE = new SimpleType(
            RELATIONSHIP_TYPE_ID, null, "Relationship", "Relationship Type",
            BaseType.RELATIONSHIP, "", true, true, false, true, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    protected static SimpleType POLICY_TYPE = new SimpleType(POLICY_TYPE_ID,
            null, "Policy", "Policy Type", BaseType.POLICY, "", true, true,
            false, true, false, false, ContentStreamPresence.NOT_ALLOWED, null,
            null, Collections.<PropertyDefinition> emptyList());

    protected final Map<String, Type> types = new HashMap<String, Type>();

    protected final String name;

    protected BaseRepository(String name) {
        this.name = name;
    }

    protected static Collection<SimpleType> getDefaultTypes() {
        return Arrays.asList(DOCUMENT_TYPE, FOLDER_TYPE, RELATIONSHIP_TYPE,
                POLICY_TYPE, ROOT_TYPE);
    }

    protected void addTypes(Collection<SimpleType> types) {
        for (Type type : types) {
            String typeId = type.getId();
            if (this.types.containsKey(typeId)) {
                throw new RuntimeException("Type already defined: " + typeId);
            }
            this.types.put(typeId, type);
        }
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

    /*
     * ----- Repository -----
     */

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

    public String getVendorName() {
        return "Apache";
    }

    public String getProductVersion() {
        // TODO update this when releasing
        return "0.1-SNAPSHOT";
    }

    public String getVersionSupported() {
        return "0.61";
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

}
