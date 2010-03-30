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
package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Property;
import org.apache.chemistry.Unfiling;
import org.apache.chemistry.UpdateConflictException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

/**
 * Folder implementation.
 */
class JcrFolder extends JcrObject implements Folder {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrFolder.class);

    /**
     * Create a new instance of this class.
     *
     * @param entry object entry
     */
    public JcrFolder(JcrObjectEntry entry) {
        super(entry);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ObjectId> deleteTree(Unfiling unfiling)
            throws UpdateConflictException {
        return connection.getSPI().deleteTree(this, unfiling, true);
    }

    /**
     * Return all children of this folder.
     *
     * @return list of children
     */
    public List<CMISObject> getChildren() {
        try {
            List<CMISObject> result = new ArrayList<CMISObject>();

            NodeIterator iter = entry.getNode().getNodes();
            while (iter.hasNext()) {
                Node child = iter.nextNode();
                if (JcrCmisMap.isInternal(child)) {
                    continue;
                }
                JcrObjectEntry entry = new JcrObjectEntry(child, connection);
                result.add(JcrObject.construct(entry));
            }
            return result;
        } catch (RepositoryException e) {
            String msg = "Unable to get children.";
            log.error(msg, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public JcrDocument newDocument(String typeId) {
        return connection.newDocument(typeId, this);
    }

    /**
     * {@inheritDoc}
     */
    public JcrFolder newFolder(String typeId) {
        return connection.newFolder(typeId, this);
    }

    /**
     * {@inheritDoc}
     */
    public void add(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(CMISObject object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        if (entry.isNew()) {
            add();
        } else {
            update();
        }
    }

    /**
     * Add a new folder.
     */
    protected void add() {
        Session session = null;

        try {
            String parentId = (String) entry.getValue(Property.PARENT_ID);
            if (parentId == null) {
                parentId = connection.getRepository().getInfo().getRootFolderId().getId();
            }
            String name = getName();
            if (name == null) {
                name = "Untitled";
            }

            session = connection.getSession();
            Node parent = session.getNodeByIdentifier(parentId);
            Node node = parent.addNode(name, JcrConstants.NT_FOLDER);
            node.addMixin(JcrRepository.MIX_UNSTRUCTURED);

            Map<String, Serializable> values = entry.getValues();
            for (String key : values.keySet()) {
                if (!key.startsWith(JcrRepository.CMIS_PREFIX)) {
                    node.setProperty(key, values.get(key).toString());
                }
            }
            node.setProperty(Property.TYPE_ID, entry.getTypeId());

            parent.getSession().save();
            entry.setNode(node);

        } catch (RepositoryException e) {
            log.error("Unable to add folder.", e);

            if (session != null) {
                try {
                    session.refresh(false);
                } catch (RepositoryException e2) {
                    log.error("Error while refreshing session.", e2);
                }
            }
        }
    }

    /**
     * Update an existing folder.
     */
    protected void update() {
        Session session = null;

        try {
            Node node = entry.getNode();
            session = node.getSession();

            Map<String, Serializable> values = entry.getValues();
            for (String key : values.keySet()) {
                if (!key.startsWith(JcrRepository.CMIS_PREFIX)) {
                    node.setProperty(key, values.get(key).toString());
                }
            }
            session.save();

        } catch (RepositoryException e) {
            log.error("Unable to update folder.", e);

            if (session != null) {
                try {
                    session.refresh(false);
                } catch (RepositoryException e2) {
                    log.error("Error while refreshing session.", e2);
                }
            }
        }
    }
}
