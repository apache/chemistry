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
 *
 * Authors:
 *     Dominique Pfister, Day
 *     Michael Mertins, Saperion
 */
package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.chemistry.ACLCapabilityType;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.impl.simple.SimpleListPage;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class JcrRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    private static final Log log = LogFactory.getLog(JcrRepository.class);

    private final javax.jcr.Repository repository;

    private final String workspace;

    private final SimpleCredentials creds;

    public JcrRepository(javax.jcr.Repository repository, String workspace,
            SimpleCredentials creds) {
        this.repository = repository;
        this.workspace = workspace;
        this.creds = creds;
    }

    public JcrRepository(javax.jcr.Repository repository, String workspace) {
        this.repository = repository;
        this.workspace = workspace;
        this.creds = new SimpleCredentials("admin", "admin".toCharArray());
    }

    public JcrRepository(javax.jcr.Repository repository) {
        this(repository, null);
    }

    public SPI getSPI() {
        // TODO parameters
        return (SPI) getConnection(null);
    }

    public Connection getConnection(Map<String, Serializable> parameters) {
        try {
            return new JcrConnection(repository.login(creds, workspace), this);
        } catch (RepositoryException e) {
            String msg = "Unable to open connection.";
            throw new RuntimeException(msg, e);
        }
    }

    public <T> T getExtension(Class<T> klass) {
        return null;
    }

    public RepositoryInfo getInfo() {
        return this;
    }

    public void addType(Type type) {
        throw new UnsupportedOperationException("Cannot add types");
    }

    public Type getType(String typeId) {
        try {
            Session session = repository.login(creds, workspace);

            // TODO fetch the types only once, include other types
            NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
            NodeType nt = ntmgr.getNodeType(typeId);

            BaseType baseType = BaseType.FOLDER;
            if (JcrCmisMap.isBaseTypeDocument(nt.getName())) {
                baseType = BaseType.DOCUMENT;
            }
            return new JcrType(nt, baseType);
        } catch (NoSuchNodeTypeException e) {
            return null;
        } catch (RepositoryException e) {
            String msg = "Unable get type: " + typeId;
            log.error(msg, e);
        }
        return null;
    }

    public Collection<Type> getTypes() {
        return getTypeDescendants(null, -1, true);
    }

    public Collection<Type> getTypeDescendants(String typeId) {
        Collection<Type> list = getTypeDescendants(typeId, -1, true);
        if (typeId != null) {
            // add the type itself as first element
            Type type = getType(typeId);
            ((LinkedList<Type>) list).addFirst(type);
        }
        return list;
    }

    public ListPage<Type> getTypeChildren(String typeId,
            boolean includePropertyDefinitions, Paging paging) {
        // TODO proper children
        List<Type> list = getTypeDescendants(typeId, -1, true);
        return new SimpleListPage<Type>(list);
    }

    public List<Type> getTypeDescendants(String typeId, int depth,
            boolean includePropertyDefinitions) {

        // TODO depth, includePropertyDefinitions

        try {
            List<Type> result = new LinkedList<Type>();

            Session session = repository.login(creds, workspace);

            NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
            if (typeId != null) {
                // check existence
                ntmgr.getNodeType(typeId);
            }

            NodeTypeIterator nodeTypes = ntmgr.getAllNodeTypes();
            while (nodeTypes.hasNext()) {
                NodeType nodeType = nodeTypes.nextNodeType();
                if (nodeType.isMixin()) {
                    // Mixing Types will be ignored
                    continue;
                }
                BaseType baseType = BaseType.FOLDER;
                if (JcrCmisMap.isBaseTypeDocument(nodeType.getName())) {
                    baseType = BaseType.DOCUMENT;
                }
                // If typeId is provided, only the descendants are returned,
                // otherwise all types are returned.
                // TODO proper hierarchy of types
                if (typeId == null) {
                    result.add(new JcrType(nodeType, baseType));
                }
            }

            return result;
        } catch (NoSuchNodeTypeException e) {
            throw new IllegalArgumentException("No such type: " + typeId);
        } catch (RepositoryException e) {
            String msg = "Unable to retrieve node types.";
            log.error(msg, e);
        }
        return null;
    }

    public String getId() {
        return getName();
    }

    public String getName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public URI getThinClientURI() {
        URI uri = null;
        try {
            uri = new URI(
                    repository.getDescriptor(javax.jcr.Repository.REP_VENDOR_URL_DESC));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return uri;
    }

    // ---------------------------------------------------------- RepositoryInfo

    public RepositoryCapabilities getCapabilities() {
        return this;
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
        return "";
    }

    public ACLCapabilityType getACLCapabilityType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        return getName();
    }

    public String getProductName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public String getProductVersion() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VERSION_DESC);
    }

    public Collection<RepositoryEntry> getRelatedRepositories() {
        return Collections.emptySet();
    }

    public Document getRepositorySpecificInformation() {
        return null;
    }

    public ObjectId getRootFolderId() {
        return new SimpleObjectId(JcrObjectEntry.escape("/"));
    }

    public String getVendorName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VENDOR_DESC);
    }

    public String getVersionSupported() {
        return "1.0";
    }

    // -------------------------------------------------- RepositoryCapabilities

    public CapabilityJoin getJoinCapability() {
        return CapabilityJoin.NONE;
    }

    public CapabilityQuery getQueryCapability() {
        return CapabilityQuery.BOTH_SEPARATE;
    }

    public CapabilityRendition getRenditionCapability() {
        return CapabilityRendition.NONE;
    }

    public CapabilityChange getChangeCapability() {
        return CapabilityChange.NONE;
    }

    public boolean hasMultifiling() {
        return true;
    }

    public boolean hasUnfiling() {
        return true;
    }

    public boolean hasVersionSpecificFiling() {
        return false;
    }

    public boolean isAllVersionsSearchable() {
        return false;
    }

    public boolean hasGetDescendants() {
        return false;
    }

    public boolean hasGetFolderTree() {
        return false;
    }

    public boolean isContentStreamUpdatableAnytime() {
        return true;
    }

    public boolean isPWCSearchable() {
        return false;
    }

    public boolean isPWCUpdatable() {
        return false;
    }

    public CapabilityACL getACLCapability() {
        // TODO Auto-generated method stub
        return CapabilityACL.NONE;
    }
}
