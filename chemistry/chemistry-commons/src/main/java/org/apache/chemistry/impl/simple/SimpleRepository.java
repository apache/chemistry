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

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.ACLCapabilityType;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.Connection;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.SPI;
import org.apache.chemistry.impl.base.BaseRepository;

public class SimpleRepository extends BaseRepository {

    protected static final Set<String> NO_PARENT = Collections.unmodifiableSet(new HashSet<String>());

    protected final String rootId;

    private final ObjectId rootFolderId;

    /** Map of id -> data */
    protected final Map<String, SimpleData> datas;

    /** Map of id -> children IDs */
    protected final Map<String, Set<String>> children;

    /** Map of id -> parent IDs, or null if unfiled */
    protected final Map<String, Set<String>> parents;

    public SimpleRepository(String name, Collection<SimpleType> types,
            String rootId) {
        this(name, rootId);
        addTypes(getDefaultTypes());
        addTypes(types);
    }

    public SimpleRepository(String name, String rootId) {
        super(name);

        datas = new ConcurrentHashMap<String, SimpleData>();
        children = new ConcurrentHashMap<String, Set<String>>();
        parents = new ConcurrentHashMap<String, Set<String>>();

        SimpleData rootData = new SimpleData(ROOT_TYPE_ID, BaseType.FOLDER);
        if (rootId == null) {
            rootId = generateId();
        }
        this.rootId = rootId;
        rootData.put(Property.ID, rootId);
        rootData.put(Property.NAME, ROOT_FOLDER_NAME);
        datas.put(rootId, rootData);
        children.put(rootId, newSet());
        parents.put(rootId, NO_PARENT);
        rootFolderId = new SimpleObjectId(rootId);
    }

    // private final AtomicLong idCounter = new AtomicLong(0);

    protected String generateId() {
        return UUID.randomUUID().toString();
        // return "ID_" + idCounter.incrementAndGet();
    }

    protected Set<String> newSet() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    /*
     * ----- RepositoryEntry -----
     */

    public URI getThinClientURI() {
        return null;
    }

    /*
     * ----- Repository -----
     */

    public Connection getConnection(Map<String, Serializable> params) {
        // TODO credentials
        return new SimpleConnection(this);
    }

    public SPI getSPI(Map<String, Serializable> params) {
        // TODO credentials
        return new SimpleConnection(this);
    }

    public <T> T getExtension(Class<T> klass) {
        return null; // not supported
    }

    /*
     * ----- RepositoryInfo -----
     */

    public String getDescription() {
        return "Repository " + name;
    }

    public ObjectId getRootFolderId() {
        return rootFolderId;
    }

    public String getProductName() {
        return "Chemistry Simple Repository";
    }

    public Set<BaseType> getChangeLogBaseTypes() {
        // TODO-0.63 TCK checks 0.62 schema which has minOccurs=1
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        changeLogBaseTypes.add(BaseType.DOCUMENT);
        changeLogBaseTypes.add(BaseType.FOLDER);
        changeLogBaseTypes.add(BaseType.RELATIONSHIP);
        changeLogBaseTypes.add(BaseType.POLICY);
        return changeLogBaseTypes;
    }

    public boolean isChangeLogIncomplete() {
        return false;
    }

    public String getLatestChangeLogToken() {
        // TODO Auto-generated method stub
        return "";
    }

    public ACLCapabilityType getACLCapabilityType() {
        // TODO Auto-generated method stub
        return null;
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

    public boolean hasGetDescendants() {
        return true;
    }

    public boolean hasGetFolderTree() {
        return true;
    }

    public boolean isContentStreamUpdatableAnytime() {
        return true;
    }

    public CapabilityJoin getJoinCapability() {
        return CapabilityJoin.NONE;
    }

    public CapabilityQuery getQueryCapability() {
        return CapabilityQuery.BOTH_COMBINED;
    }

    public CapabilityRendition getRenditionCapability() {
        return CapabilityRendition.NONE;
    }

    public CapabilityChange getChangeCapability() {
        return CapabilityChange.NONE;
    }

    public CapabilityACL getACLCapability() {
        return CapabilityACL.NONE;
    }

}
