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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Folder;
import org.apache.chemistry.NameConstraintViolationException;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Type;
import org.apache.chemistry.UpdateConflictException;
import org.apache.chemistry.impl.base.BaseObject;
import org.apache.chemistry.impl.simple.SimpleProperty;

/**
 * JCR implementation of a {@link CMISObject}.
 */
abstract class JcrObject extends BaseObject {

    /**
     * Underlying entry.
     */
    protected final JcrObjectEntry entry;

    /**
     * Connection used to create this object.
     */
    protected final JcrConnection connection;

    /**
     * Object type.
     */
    protected final Type type;

    /**
     * Create a new instance of this class. Used for derived classes only.
     *
     * @param entry entry
     */
    protected JcrObject(JcrObjectEntry entry) {
        this.entry = entry;

        connection = entry.getConnection();
        type = connection.getRepository().getType(entry.getTypeId());
    }

    protected static JcrObject construct(JcrObjectEntry entry) {
        JcrConnection connection = entry.getConnection();
        BaseType baseType = connection.getRepository().getType(
                entry.getTypeId()).getBaseType();

        switch (baseType) {
        case DOCUMENT:
            return new JcrDocument(entry);
        case FOLDER:
            return new JcrFolder(entry);
        case POLICY:
            return new JcrPolicy(entry);
        case RELATIONSHIP:
            return new JcrRelationship(entry);
        default:
            throw new AssertionError();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    public void move(Folder targetFolder, Folder sourceFolder)
            throws NameConstraintViolationException, UpdateConflictException {
        connection.getSPI().moveObject(this, targetFolder, sourceFolder);
    }

    /**
     * {@inheritDoc}
     */
    public void delete() throws UpdateConflictException {
        connection.getSPI().deleteObject(this, false);
    }

    public void unfile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Folder getParent() {
        String id = (String) entry.getValue(Property.PARENT_ID);
        if (id == null) {
            return null;
        }
        return (Folder) connection.getObject(connection.newObjectId(id));
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Folder> getParents() {
        Collection<Folder> result;
        Folder parent = getParent();
        if (parent == null) {
            result = Collections.emptyList();
        } else {
            result = Arrays.asList(new Folder[] { parent });
        }
        return result;
    }

    public List<Relationship> getRelationships(RelationshipDirection direction,
            String typeId, boolean includeSubRelationshipTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void applyPolicy(Policy policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removePolicy(Policy policy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Policy> getPolicies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public BaseType getBaseType() {
        return type.getBaseType();
    }

    /**
     * {@inheritDoc}
     */
    public Serializable getValue(String id) {
        PropertyDefinition pd = getType().getPropertyDefinition(id);
        if (pd == null) {
            throw new IllegalArgumentException(id);
        }
        Serializable value = entry.getValue(id);
        if (value == null) {
            value = pd.getDefaultValue();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Property getProperty(String id) {
        PropertyDefinition propertyDefinition = getType().getPropertyDefinition(
                id);
        if (propertyDefinition == null) {
            throw new IllegalArgumentException(id);
        }
        return new SimpleProperty(entry, id, propertyDefinition);
    }

    /**
     * {@inheritDoc}
     */
    public ContentStream getContentStream(String contentStreamId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
        /* Derived classes have to implement this */
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getTypeId() + ',' + getId()
                + ')';
    }

    /**
     * Return the underlying entry.
     *
     * @return entry
     */
    JcrObjectEntry getEntry() {
        return entry;
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
