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

import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jackrabbit.cmis.Entry;

public class JCREntry implements Entry {

    private static Logger log = LoggerFactory.getLogger(JCREntry.class);

    private javax.jcr.Node node;

    public JCREntry(javax.jcr.Node node) {
        this.node = node;
    }

    public String getId() {
        return getObjectId();
    }

    public String getChangeToken() {
        return null;
    }

    public String getCreatedBy() {
        try {
            return node.getSession().getUserID();
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve user ID", e);
            return null;
        }
    }

    public Calendar getCreationDate() {
        return Calendar.getInstance();
    }

    public String getLastModifiedBy() {
        try {
            return node.getSession().getUserID();
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve user ID", e);
            return null;
        }
    }

    public Calendar getLastModificationDate() {
        return Calendar.getInstance();
    }

    public String getName() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve item name", e);
            return null;
        }
    }

    public String getObjectId() {
        try {
            String id = node.getPath();
            if (id.startsWith("/")) {
                id = id.substring(1);
            }
            return id;
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve item path", e);
            return null;
        }
    }

    public String getObjectTypeId() {
        try {
            return node.getPrimaryNodeType().getName();
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve primary node type", e);
            return null;
        }
    }

    public String getParentId() {
        try {
            return node.getParent().getPath();
        } catch (ItemNotFoundException e) {
            /* this is the root node */
            return "";
        } catch (RepositoryException e) {
            log.warn("Unable to retrieve parent path", e);
            return null;
        }
    }

    public Iterable<Entry> getChildren() {
        final javax.jcr.Node node = this.node;

        return new Iterable<Entry>() {
            public Iterator<Entry> iterator() {
                try {
                    return new JCREntryIterator(node.getNodes());
                } catch (RepositoryException e) {
                    log.warn("Unable to retrieve children", e);
                    return null;
                }
            }
        };
    }

    public Iterable<Entry> getDescendants() {
	// TODO: retrieve all descendants, not just the direct ones
        return getChildren();
    }
}
