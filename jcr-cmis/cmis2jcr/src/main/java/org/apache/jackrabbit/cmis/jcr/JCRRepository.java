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
package org.apache.jackrabbit.cmis.jcr;

import java.net.URI;
import java.util.Iterator;

import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jackrabbit.cmis.Capabilities;
import org.apache.jackrabbit.cmis.Entry;
import org.apache.jackrabbit.cmis.FullTextSupport;
import org.apache.jackrabbit.cmis.JoinSupport;
import org.apache.jackrabbit.cmis.QuerySupport;
import org.apache.jackrabbit.cmis.Repository;

public class JCRRepository implements Repository, Capabilities {

    private static Logger log = LoggerFactory.getLogger(JCRRepository.class);
    private javax.jcr.Repository repository;
    private String workspace;

    public JCRRepository(javax.jcr.Repository repository, String workspace) {
        this.repository = repository;
        this.workspace = workspace;
    }

    public Capabilities getCapabilities() {
        return this;
    }

    public String getDescription() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public String getId() {
        return getName();
    }

    public String getName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public URI getURI() {
        // TODO Return a URI
        return null;
    }

    public String getProductName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public String getProductVersion() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VERSION_DESC);
    }

    public String getRootFolderId() {
        return workspace;
    }

    public String getVendorName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VENDOR_DESC);
    }

    public String getVersionsSupported() {
        return "0.5";
    }

    public Entry getEntry(String id) {
        javax.jcr.Session session = null;

        try {
            session = repository.login(workspace);
            javax.jcr.Node node = session.getRootNode();
            if (!id.equals("")) {
                node = node.getNode(id);
            }
            return new JCREntry(node);
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve node " + id, e);
            return null;
        } finally {
            /*
            if (session != null) {
                session.logout();
            }
            */
        }
    }

    public Iterable<Entry> query(String s) {
        javax.jcr.Session session = null;

        if (!s.startsWith("select ") && !s.startsWith("SELECT ")) {
            s = "SELECT * FROM nt:base WHERE contains(*, '" + s + "')";
        }

        try {
            session = repository.login(workspace);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery(s, Query.SQL);
            final QueryResult result = query.execute();

            return new Iterable<Entry>() {
                public Iterator<Entry> iterator() {
                    try {
                        return new JCREntryIterator(result.getNodes());
                    } catch (RepositoryException e) {
                        log.warn("Unable to retrieve result nodes", e);
                        return null;
                    }
                }
            };
        } catch (RepositoryException e) {
            log.warn("Unable to execute query: " + s, e);
            return null;
        } finally {
            /*
            if (session != null) {
                session.logout();
            }
            */
        }
    }

    public boolean areAllVersionsSearchable() {
        return false;
    }

    public FullTextSupport getFullTextSupport() {
        return FullTextSupport.FULL_TEXT_AND_STRUCTURED;
    }

    public JoinSupport getJoinSupport() {
        return JoinSupport.NO;
    }

    public QuerySupport getQuerySupport() {
        return QuerySupport.BOTH;
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

    public boolean isPWCSearchable() {
        return false;
    }

    public boolean isPWCUpdatable() {
        return false;
    }
}
