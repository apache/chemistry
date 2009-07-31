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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.JoinCapability;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.QueryCapability;
import org.apache.chemistry.RenditionCapability;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryEntry;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;
import org.w3c.dom.Document;

public class JcrRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    private static final Log log = LogFactory.getLog(JcrRepository.class);

    private final javax.jcr.Repository repository;
    private final String workspace;

    public JcrRepository(javax.jcr.Repository repository, String workspace) {
        this.repository = repository;
        this.workspace = workspace;
    }

    public JcrRepository(javax.jcr.Repository repository) {
        this(repository, null);
    }

    public SPI getSPI() {
        // TODO parameters
        return (SPI) getConnection(null);
    }

    public Connection getConnection(Map<String, Serializable> parameters) {
        // TODO pass credentials as parameters
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());

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
            // TODO pass credentials as parameters
            SimpleCredentials creds = new SimpleCredentials("admin", "admin"
                    .toCharArray());

            Session session = repository.login(creds, workspace);

            // TODO fetch the types only once, include other types
            NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
            NodeType nt = ntmgr.getNodeType(typeId);

            BaseType baseType = BaseType.FOLDER;
            if (nt.getName().equals(JcrConstants.NT_FILE)) {
                baseType = BaseType.DOCUMENT;
            }
            return new JcrType(nt, baseType);
        } catch (RepositoryException e) {
            String msg = "Unable get type: " + typeId;
            log.error(msg, e);
        }
        return null;
    }

    public Collection<Type> getTypes(String typeId) {
        return getTypes(typeId, -1, true);
    }

    public List<Type> getTypes(String typeId, int depth,
            boolean returnPropertyDefinitions) {

        // TODO dynamically discover and return types.
        // TODO depth, returnPropertyDefinitions

        try {
            // TODO pass credentials as parameters
            SimpleCredentials creds = new SimpleCredentials("admin", "admin"
                    .toCharArray());

            List<Type> result = new ArrayList<Type>();

            Session session = repository.login(creds, workspace);

            // TODO fetch the types only once, include other types
            NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
            result.add(new JcrType(ntmgr.getNodeType("rep:root"),
                    BaseType.FOLDER));
            result.add(new JcrType(ntmgr.getNodeType(JcrConstants.NT_FOLDER),
                    BaseType.FOLDER));
            result.add(new JcrType(ntmgr.getNodeType(JcrConstants.NT_FILE),
                    BaseType.DOCUMENT));
            return result;
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

    public String getRelationshipName() {
        return null;
    }

    public URI getURI() {
        // TODO Auto-generated method stub
        return null;
    }

    // ---------------------------------------------------------- RepositoryInfo

    public RepositoryCapabilities getCapabilities() {
        return this;
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
        return "0.61";
    }

    // -------------------------------------------------- RepositoryCapabilities

    public JoinCapability getJoinCapability() {
        return JoinCapability.NONE;
    }

    public QueryCapability getQueryCapability() {
        return QueryCapability.BOTH_SEPARATE;
    }

    public RenditionCapability getRenditionCapability() {
        return RenditionCapability.NONE;
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

    public boolean isPWCSearchable() {
        return false;
    }

    public boolean isPWCUpdatable() {
        return false;
    }
}
