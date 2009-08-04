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
package org.apache.chemistry.impl.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.impl.simple.SimpleProperty;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.chemistry.impl.simple.SimpleTypeManager;

/**
 * Base implementation of a {@link Repository}. The implemented methods are
 * completely generic and base on {@link SimpleType} and {@link SimpleProperty}.
 */
public abstract class BaseRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    public static final String ROOT_TYPE_ID = "chemistry:root";

    public static final String ROOT_FOLDER_NAME = "";

    public static final SimpleType ROOT_TYPE = new SimpleType(ROOT_TYPE_ID,
            BaseType.FOLDER.getId(), ROOT_TYPE_ID, null, ROOT_TYPE_ID,
            "Root Folder Type", BaseType.FOLDER, "", false, false, false,
            false, false, false, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    public static final SimpleType DOCUMENT_TYPE = new SimpleType(
            BaseType.DOCUMENT.getId(), null, BaseType.DOCUMENT.getId(), null,
            BaseType.DOCUMENT.getId(), "Document Type", BaseType.DOCUMENT, "",
            true, true, true, true, true, true, true, true,
            ContentStreamPresence.ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    public static final SimpleType FOLDER_TYPE = new SimpleType(
            BaseType.FOLDER.getId(), null, BaseType.FOLDER.getId(), null,
            BaseType.FOLDER.getId(), "Folder Type", BaseType.FOLDER, "", true,
            true, false, true, true, true, true, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    public static final SimpleType RELATIONSHIP_TYPE = new SimpleType(
            BaseType.RELATIONSHIP.getId(), null, BaseType.RELATIONSHIP.getId(),
            null, BaseType.RELATIONSHIP.getId(), "Relationship Type",
            BaseType.RELATIONSHIP, "", true, true, false, false, false, true,
            false, false, ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    protected static final SimpleType POLICY_TYPE = new SimpleType(
            BaseType.POLICY.getId(), null, BaseType.POLICY.getId(), null,
            "Policy", "Policy Type", BaseType.POLICY, "", true, true, false,
            false, false, true, false, false,
            ContentStreamPresence.NOT_ALLOWED, null, null,
            Collections.<PropertyDefinition> emptyList());

    protected final String name;

    protected final TypeManager typeManager = new SimpleTypeManager();

    protected BaseRepository(String name) {
        this.name = name;
    }

    protected static Collection<SimpleType> getDefaultTypes() {
        return Arrays.asList(DOCUMENT_TYPE, FOLDER_TYPE, RELATIONSHIP_TYPE,
                POLICY_TYPE, ROOT_TYPE);
    }

    protected void addTypes(Collection<SimpleType> types) {
        for (Type type : types) {
            typeManager.addType(type);
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
        return "0.62";
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
     * ----- TypeManager -----
     */
    public void addType(Type type) {
        throw new UnsupportedOperationException("Cannot add types");
    }

    public Type getType(String typeId) {
        return typeManager.getType(typeId);
    }

    public Collection<Type> getTypes(String typeId) {
        return typeManager.getTypes(typeId);
    }

    public Collection<Type> getTypes(String typeId, int depth,
            boolean returnPropertyDefinitions) {
        return typeManager.getTypes(typeId, depth, returnPropertyDefinitions);
    }

}
