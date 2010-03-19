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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.NameConstraintViolationException;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.util.GregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

/**
 * Document implementation.
 */
class JcrDocument extends JcrObject implements Document {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrDocument.class);

    /**
     * Content stream.
     */
    private ContentStream cs;

    /**
     * Create a new instance of this class.
     *
     * @param entry
     */
    public JcrDocument(JcrObjectEntry entry) {
        super(entry);
    }

    /**
     * {@inheritDoc}
     */
    public ContentStream getContentStream() {
        if (entry.isNew()) {
            return cs;
        }
        try {
            Node node = entry.getNode();

            Node content = node.getNode(JcrConstants.JCR_CONTENT);
            String filename = null;
            if (hasCmisPrefix() && node.hasProperty(Property.CONTENT_STREAM_FILE_NAME)) {
                filename = node.getProperty(Property.CONTENT_STREAM_FILE_NAME).getString();
            }
            JcrContentStream cs = new JcrContentStream(content, filename);
            if (cs.getLength() != 0) {
                return cs;
            }
        } catch (RepositoryException e) {
            String msg = "Unable to get parent.";
            log.error(msg, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setContentStream(ContentStream cs)
            throws IOException {

        ContentStreamPresence csa = getType().getContentStreamAllowed();
        if (csa == ContentStreamPresence.NOT_ALLOWED && cs != null) {
            throw new IllegalStateException("Content stream not allowed"); // TODO
        } else if (csa == ContentStreamPresence.REQUIRED
                && cs == null) {
            throw new IllegalStateException("Content stream required"); // TODO
        }
        if (entry.isNew()) {
            this.cs = cs;
            return;
        }

        Node node = entry.getNode();
        Session session = null;

        try {
            session = node.getSession();
            Node content;

            if (node.hasNode(JcrConstants.JCR_CONTENT)) {
                content = node.getNode(JcrConstants.JCR_CONTENT);
            } else {
                content = node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            }
            if (cs != null) {
                content.setProperty(JcrConstants.JCR_MIMETYPE, cs.getMimeType());
                content.setProperty(JcrConstants.JCR_DATA,
                        session.getValueFactory().createBinary(cs.getStream()));

                String filename = cs.getFileName();
                if (filename != null) {
                    node.setProperty(Property.CONTENT_STREAM_FILE_NAME, filename);
                }
            } else {
                content.setProperty(JcrConstants.JCR_MIMETYPE, "application/octet-stream");
                content.setProperty(JcrConstants.JCR_DATA,
                        session.getValueFactory().createBinary(new ByteArrayInputStream(new byte[0])));
            }
            content.setProperty(JcrConstants.JCR_LASTMODIFIED,
                    GregorianCalendar.getInstance());
            session.save();

            entry.setContentStream(cs);

        } catch (RepositoryException e) {
            log.error("Unable to set content stream.", e);

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
     * {@inheritDoc}
     */
    public Document copy(Folder folder) throws NameConstraintViolationException {
        return (JcrDocument) connection.createDocumentFromSource(this, folder, null, null);
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
     * Add a new document.
     */
    protected void add() {
        Session session = null;
        InputStream is = null;

        if (cs != null) {
            try {
                is = cs.getStream();
            } catch (IOException e) {
                String msg = "Unable to get stream.";
                log.error(msg, e);
                return;
            }
        }

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
            Node node = parent.addNode(name, JcrConstants.NT_FILE);
            node.addMixin(JcrRepository.MIX_UNSTRUCTURED);

            Node content = node.addNode(JcrConstants.JCR_CONTENT,
                    JcrConstants.NT_RESOURCE);
            if (cs != null) {
                content.setProperty(JcrConstants.JCR_MIMETYPE, cs.getMimeType());
                content.setProperty(JcrConstants.JCR_DATA,
                        session.getValueFactory().createBinary(is));

                String filename = cs.getFileName();
                if (filename != null) {
                    node.setProperty(Property.CONTENT_STREAM_FILE_NAME, filename);
                }
            } else {
                content.setProperty(JcrConstants.JCR_MIMETYPE, "application/octet-stream");
                content.setProperty(JcrConstants.JCR_DATA,
                        session.getValueFactory().createBinary(new ByteArrayInputStream(new byte[0])));
            }
            content.setProperty(JcrConstants.JCR_LASTMODIFIED,
                    GregorianCalendar.getInstance());

            javax.jcr.ValueFactory f = connection.getSession().getValueFactory();
            Map<String, Serializable> values = entry.getValues();
            for (String key : values.keySet()) {
                if (!key.startsWith(JcrRepository.CMIS_PREFIX)) {
                    PropertyDefinition pd = type.getPropertyDefinition(key);
                    Serializable v = values.get(key);
                    node.setProperty(key, JcrCmisMap.serializableToValue(pd.getType(), v, f));
                }
            }
            node.setProperty(Property.TYPE_ID, entry.getTypeId());
            session.save();

            entry.setNode(node);
            entry.setContentStream(cs);

        } catch (RepositoryException e) {
            log.error("Unable to add document.", e);

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
     * Update an existing document.
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
            log.error("Unable to update document.", e);

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
     * {@inheritDoc}
     */
    public Document checkOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void cancelCheckOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Document checkIn(boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Document getLatestVersion(boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Document> getAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
