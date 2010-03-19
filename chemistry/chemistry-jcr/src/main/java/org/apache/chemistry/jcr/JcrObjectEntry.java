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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;

import org.apache.chemistry.AllowableAction;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.ChangeInfo;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;
import org.apache.chemistry.impl.base.BaseRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

/**
 * JCR implementation of an {@link ObjectEntry}.
 * <p>
 * This implementation doesn't do type validation when values are set.
 */
class JcrObjectEntry implements ObjectEntry {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrObjectEntry.class);

    /**
     * Values.
     */
    private Map<String, Serializable> values;

    /**
     * Associated JCR node, <code>null</code> if this is a new node.
     */
    private Node node;

    /**
     * Type, <code>null</code> if this hasn't been computed yet.
     */
    private Type type;

    /**
     * JCR connection.
     */
    private final JcrConnection connection;

    /**
     * Create a new instance of this class. Used for existing objects.
     *
     * @param node JCR node
     * @param connection JCR connection
     */
    public JcrObjectEntry(Node node, JcrConnection connection) {
        this(node, null, connection);
    }

    /**
     * Create a new instance of this class. Used for existing objects.
     *
     * @param node JCR node
     * @param type type that should override type saved in JCR properties
     * @param connection JCR connection
     */
    public JcrObjectEntry(Node node, Type type, JcrConnection connection) {
        this.node = node;
        this.type = type;
        this.connection = connection;
    }

    /**
     * Constructor used for new object entries.
     *
     * @param type type
     * @param connection connection
     */
    public JcrObjectEntry(Type type, JcrConnection connection) {
        this.type = type;
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        if (isNew()) {
            return null;
        }
        try {
            return node.getIdentifier();
        } catch (RepositoryException e) {
            log.error("Unable to retrieve identifier", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeId() {
        if (type != null) {
            return type.getId();
        }
        try {
            if (hasCmisPrefix() && node.hasProperty(Property.TYPE_ID)) {
                return node.getProperty(Property.TYPE_ID).getString();
            }
            String nt = node.getPrimaryNodeType().getName();
            if (JcrCmisMap.isBaseTypeFolder(nt)) {
                return BaseType.FOLDER.getId();
            } else {
                return BaseType.DOCUMENT.getId();
            }
        } catch (RepositoryException e) {
            log.error("Unable to get type id", e);
            return BaseType.DOCUMENT.getId();
        }
    }

    /**
     * Load the type unless done.
     */
    private synchronized Type getType() {
        if (type == null) {
            String typeId = getTypeId();
            type = connection.getRepository().getType(typeId);
            if (type == null) {
                log.warn("Actual object type not registered: " + typeId);
                type = BaseRepository.DOCUMENT_TYPE;
            }
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public BaseType getBaseType() {
        return getType().getBaseType();
    }

    /**
     * Return this object's name.
     *
     * @return name or <code>null</code> if this object is new
     */
    private String getName() {
        if (!isNew()) {
            try {
                return node.getName();
            } catch (RepositoryException e) {
                log.error("Unable to get node name", e);
            }
        }
        return null;
    }

    /**
     * Return this object's path.
     *
     * @return path or <code>null</code> if this object is new
     */
    private String getPath() {
        if (!isNew()) {
            try {
                return node.getPath();
            } catch (RepositoryException e) {
                log.error("Unable to get node name", e);
            }
        }
        return null;
    }

    /**
     * Return this object's path.
     *
     * @return path or <code>null</code> if this object is new
     */
    private String getParentId() {
        if (!isNew() && !connection.getRootFolderId().getId().equals(getId())) {
            try {
                return node.getParent().getIdentifier();
            } catch (RepositoryException e) {
                log.error("Unable to get node name", e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ChangeInfo getChangeInfo() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getPathSegment() {
        if (isNew()) {
            return null;
        }
        return (String) getValue(Property.NAME);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Serializable> getValues() {
        loadValues();
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable getValue(String id) {
        loadValues();
        return values.get(id);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String id, Serializable value) {
        loadValues();
        if (value == null) {
            values.remove(id);
        } else {
            values.put(id, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValues(Map<String, Serializable> values) {
        loadValues();
        // don't use putAll as we want to check for nulls
        for (String id : values.keySet()) {
            setValue(id, values.get(id));
        }
    }

    /**
     * Load values unless already done.
     */
    synchronized void loadValues() {
        if (values == null) {
            values = new HashMap<String, Serializable>();

            loadPropertyValues(values);

            values.put(Property.TYPE_ID, getTypeId());
            values.put(Property.BASE_TYPE_ID, getBaseType().getId());

            if (!isNew()) {
                values.put(Property.ID, getId());
                values.put(Property.NAME, getName());
                values.put(Property.PATH, getPath());

                String parentId = getParentId();
                if (parentId != null) {
                    values.put(Property.PARENT_ID, parentId);
                }
                loadContentStreamValues(values);
            }
        }
    }

    /**
     * Load values from the underlying JCR node.
     *
     * @param values map to populate with entries
     */
    private void loadPropertyValues(Map<String, Serializable> values) {
        if (isNew()) {
            return;
        }
        /* Load JCR property values that are included in this
         * type's property definitions
         */
        Type type = connection.getRepository().getType(getTypeId());
        for (PropertyDefinition pd : type.getPropertyDefinitions()) {
            String id = pd.getId();
            try {
                if (id.startsWith(JcrRepository.CMIS_PREFIX) && !hasCmisPrefix()) {
                    continue;
                }
                if (node.hasProperty(id)) {
                    values.put(id, JcrCmisMap.valueToSerializable(
                            pd.getType(),
                            node.getProperty(id).getValue()));
                }
            } catch (RepositoryException e) {
                log.error("Unable to load property: " + id, e);
            }
        }
    }

    /**
     * Load content stream values from the underlying JCR node.
     *
     * @param values map to populate with entries
     */
    private void loadContentStreamValues(Map<String, Serializable> values) {
        if (isNew() || getBaseType() != BaseType.DOCUMENT) {
            return;
        }
        try {
            Node content = node.getNode(JcrConstants.JCR_CONTENT);
            String filename = getName();
            if (hasCmisPrefix() && node.hasProperty(Property.CONTENT_STREAM_FILE_NAME)) {
                filename = node.getProperty(Property.CONTENT_STREAM_FILE_NAME).getString();
            }
            JcrContentStream cs = new JcrContentStream(content, filename);
            if (cs.getLength() != 0) {
                values.put(Property.CONTENT_STREAM_FILE_NAME, cs.getFileName());
                values.put(Property.CONTENT_STREAM_MIME_TYPE, cs.getMimeType());
                values.put(Property.CONTENT_STREAM_LENGTH, Integer.valueOf((int) cs.getLength()));
            }
        } catch (RepositoryException e) {
            String msg = "Unable to inspect jcr:content sub node.";
            log.error(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<QName> getAllowableActions() {
        boolean canWrite = true;
        boolean isFolder = getBaseType() == BaseType.FOLDER;
        Set<QName> set = new HashSet<QName>();
        set.add(AllowableAction.CAN_GET_OBJECT_PARENTS);
        set.add(AllowableAction.CAN_GET_PROPERTIES);
        if (isFolder) {
            set.add(AllowableAction.CAN_GET_DESCENDANTS);
            set.add(AllowableAction.CAN_GET_FOLDER_PARENT);
            set.add(AllowableAction.CAN_GET_FOLDER_TREE);
            set.add(AllowableAction.CAN_GET_CHILDREN);
        } else {
            set.add(AllowableAction.CAN_GET_CONTENT_STREAM);
        }
        if (canWrite) {
            if (isFolder) {
                set.add(AllowableAction.CAN_CREATE_DOCUMENT);
                set.add(AllowableAction.CAN_CREATE_FOLDER);
                set.add(AllowableAction.CAN_CREATE_RELATIONSHIP);
                set.add(AllowableAction.CAN_DELETE_TREE);
                set.add(AllowableAction.CAN_ADD_OBJECT_TO_FOLDER);
                set.add(AllowableAction.CAN_REMOVE_OBJECT_FROM_FOLDER);
            } else {
                set.add(AllowableAction.CAN_SET_CONTENT_STREAM);
                set.add(AllowableAction.CAN_DELETE_CONTENT_STREAM);
            }
            set.add(AllowableAction.CAN_UPDATE_PROPERTIES);
            set.add(AllowableAction.CAN_MOVE_OBJECT);
            set.add(AllowableAction.CAN_DELETE_OBJECT);
        }
        if (Boolean.FALSE.booleanValue()) {
            // TODO
            set.add(AllowableAction.CAN_GET_RENDITIONS);
            set.add(AllowableAction.CAN_CHECK_OUT);
            set.add(AllowableAction.CAN_CANCEL_CHECK_OUT);
            set.add(AllowableAction.CAN_CHECK_IN);
            set.add(AllowableAction.CAN_GET_ALL_VERSIONS);
            set.add(AllowableAction.CAN_GET_OBJECT_RELATIONSHIPS);
            set.add(AllowableAction.CAN_APPLY_POLICY);
            set.add(AllowableAction.CAN_REMOVE_POLICY);
            set.add(AllowableAction.CAN_GET_APPLIED_POLICIES);
            set.add(AllowableAction.CAN_GET_ACL);
            set.add(AllowableAction.CAN_APPLY_ACL);
        }
        return set;
    }

    public Collection<ObjectEntry> getRelationships() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getTypeId() + ',' + getId()
                + ')';
    }

    /**
     * Return the node representing this entry.
     *
     * @return node or <code>null</code> if this entry is new
     */
    Node getNode() {
        return node;
    }

    /**
     * Set the node representing this entry. Done after saving a node
     * for the first time. Populate the missing values in our values
     * array.
     *
     * @param node node
     */
    synchronized void setNode(Node node) {
        this.node = node;

        if (values != null) {
            values.put(Property.ID, getId());
            values.put(Property.NAME, getName());
            values.put(Property.PATH, getPath());

            String parentId = getParentId();
            if (parentId != null) {
                values.put(Property.PARENT_ID, parentId);
            }
        }
    }

    /**
     * Update the internal content stream properties after a content stream
     * has been written.
     *
     * @param cs content stream or <code>null</code>
     */
    synchronized void setContentStream(ContentStream cs) {
        if (values != null) {
            if (cs == null) {
                values.remove(Property.CONTENT_STREAM_FILE_NAME);
                values.remove(Property.CONTENT_STREAM_MIME_TYPE);
                values.remove(Property.CONTENT_STREAM_LENGTH);
            } else {
                String filename = cs.getFileName();
                if (filename == null) {
                    filename = (String) getValue(Property.NAME);
                }
                values.put(Property.CONTENT_STREAM_FILE_NAME, filename);
                values.put(Property.CONTENT_STREAM_MIME_TYPE, cs.getMimeType());
                values.put(Property.CONTENT_STREAM_LENGTH, Integer.valueOf((int) cs.getLength()));
            }
        }
    }

    /**
     * Return a flag indicating whether this entry is new.
     *
     * @return <code>true</code> if this entry is new;
     *         <code>false</code> otherwise
     */
    public boolean isNew() {
        return node == null;
    }

    /**
     * Return the JCR connection.
     *
     * @return connection
     */
    JcrConnection getConnection() {
        return connection;
    }

    /**
     * Return a flag indicating whether the namespace prefix <b>cmis</b> is known to
     * the repository.
     *
     * @return namespace URI
     */
    boolean hasCmisPrefix() {
        return connection.getRepository().hasCmisPrefix();
    }
}
