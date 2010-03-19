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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.chemistry.ACLCapabilityType;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.SPI;
import org.apache.chemistry.impl.base.BaseRepository;
import org.apache.chemistry.impl.simple.SimpleObjectId;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Repository implementation that exposes a JCR repository.
 */
public class JcrRepository extends BaseRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    /**
     * CMIS property prefix (&quot;cmis:&quot;).
     */
    public static final String CMIS_PREFIX = "cmis:";

    /**
     * Mixin to add in order to add custom properties (&quot;mix:unstructured&quot;).
     */
    public static final String MIX_UNSTRUCTURED = "mix:unstructured";

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrRepository.class);

    /**
     * JCR repository.
     */
    private final javax.jcr.Repository repository;

    /**
     * Workspace to login to.
     */
    private String workspace;

    /**
     * Credentials to use when connecting.
     */
    private SimpleCredentials creds;

    /**
     * Root folder id.
     */
    private ObjectId rootFolderId;

    /**
     * Flag indicating whether the repository has registered <b>cmis</b> prefix.
     */
    private Boolean hasCmisPrefix;

    /**
     * Create a new instance of this class.
     *
     * @param repository JCR repository
     * @param types types to add (in testing)
     */
    public JcrRepository(javax.jcr.Repository repository, Collection<SimpleType> types) {
        super("chemistry-jcr");

        this.repository = repository;

        addTypes(getDefaultTypes());
        if (types != null) {
            addTypes(types);
        }
    }

    /**
     * Create a new instance of this class.
     *
     * @param repository JCR repository
     */
    public JcrRepository(javax.jcr.Repository repository) {
        this(repository, null);
    }

    /**
     * Set workspace to use when connecting.
     *
     * @param workspace workspace
     */
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    /**
     * Set credentials to use when connecting.
     *
     * @param creds credentials
     */
    public void setCredentials(SimpleCredentials creds) {
        this.creds = creds;
    }

    /**
     * Set root folder id.
     *
     * @param rootFolderId root folder id
     */
    public void setRootNodeId(String id) {
        this.rootFolderId = new SimpleObjectId(id);
    }

    /**
     * {@inheritDoc}
     */
    public SPI getSPI(Map<String, Serializable> params) {
        return getConnection(params);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized JcrConnection getConnection(Map<String, Serializable> params) {
        try {
            Session session = repository.login(creds, workspace);
            if (rootFolderId == null) {
                rootFolderId = new SimpleObjectId(session.getRootNode().getIdentifier());
            }
            return new JcrConnection(session, this);
        } catch (RepositoryException e) {
            String msg = "Unable to open connection.";
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getExtension(Class<T> klass) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public Set<BaseType> getChangeLogBaseTypes() {
        // TODO-0.63 TCK checks 0.62 schema which has minOccurs=1
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        changeLogBaseTypes.add(BaseType.DOCUMENT);
        changeLogBaseTypes.add(BaseType.FOLDER);
        changeLogBaseTypes.add(BaseType.RELATIONSHIP);
        changeLogBaseTypes.add(BaseType.POLICY);
        return changeLogBaseTypes;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isChangeLogIncomplete() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getLatestChangeLogToken() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public ACLCapabilityType getACLCapabilityType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getProductName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    /**
     * {@inheritDoc}
     */
    public String getProductVersion() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VERSION_DESC);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectId getRootFolderId() {
        if (rootFolderId == null) {
            Session session = null;

            try {
                session = repository.login(creds, workspace);
                rootFolderId = new SimpleObjectId(session.getRootNode().getIdentifier());
            } catch (RepositoryException e) {
                log.error("Unable to determine root folder id.", e);
            } finally {
                if (session != null) {
                    session.logout();
                }
            }
        }
        return rootFolderId;
    }

    /**
     * Return a flag indicating whether the <b>cmis</b> prefix is registered.
     *
     * @return <code>true</code> if <b>cmis</b> is registered;
     *         <code>false</code> otherwise
     */
    public synchronized boolean hasCmisPrefix() {
        if (hasCmisPrefix == null) {
            Session session = null;

            try {
                session = repository.login(creds, workspace);
                String uri = session.getWorkspace().getNamespaceRegistry().getURI("cmis");
                hasCmisPrefix = uri != null;
            } catch (NamespaceException e) {
                hasCmisPrefix = Boolean.FALSE;
            } catch (RepositoryException e) {
                log.error("Unable to determine check namespace prefix: cmis.", e);
            } finally {
                if (session != null) {
                    session.logout();
                }
            }
        }
        return hasCmisPrefix.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getVendorName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VENDOR_DESC);
    }

    // -------------------------------------------------- RepositoryCapabilities

    /**
     * {@inheritDoc}
     */
    public CapabilityJoin getJoinCapability() {
        return CapabilityJoin.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public CapabilityQuery getQueryCapability() {
        return CapabilityQuery.BOTH_SEPARATE;
    }

    /**
     * {@inheritDoc}
     */
    public CapabilityRendition getRenditionCapability() {
        return CapabilityRendition.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public CapabilityChange getChangeCapability() {
        return CapabilityChange.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasMultifiling() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUnfiling() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasVersionSpecificFiling() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAllVersionsSearchable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasGetDescendants() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasGetFolderTree() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isContentStreamUpdatableAnytime() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPWCSearchable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPWCUpdatable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public CapabilityACL getACLCapability() {
        return CapabilityACL.NONE;
    }
}
