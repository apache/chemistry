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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Folder;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Type;
import org.apache.chemistry.impl.base.BaseObject;

/**
 * Simple implementation of a {@link CMISObject}.
 */
public class SimpleObject extends BaseObject {

    protected final SimpleObjectEntry entry;

    private final Type type;

    protected SimpleObject(SimpleObjectEntry entry) {
        this.entry = entry;
        type = entry.connection.repository.getType(entry.getTypeId());
    }

    protected static SimpleObject construct(SimpleObjectEntry entry) {
        BaseType baseType = entry.connection.repository.getType(
                entry.getTypeId()).getBaseType();
        switch (baseType) {
        case DOCUMENT:
            return new SimpleDocument(entry);
        case FOLDER:
            return new SimpleFolder(entry);
        case POLICY:
            return new SimplePolicy(entry);
        case RELATIONSHIP:
            return new SimpleRelationship(entry);
        default:
            throw new AssertionError();
        }
    }

    public void move(Folder targetFolder, Folder sourceFolder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void delete() {
        entry.connection.deleteObject(this);
    }

    public void unfile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Folder getParent() {
        Set<String> parents = entry.connection.repository.parents.get(getId());
        if (parents == SimpleRepository.NO_PARENT) {
            return null;
        }
        if (parents.size() != 1) {
            throw new RuntimeException("Several parents for: " + getId()); // TODO
        }
        String pid = parents.iterator().next();
        SimpleData data = entry.connection.repository.datas.get(pid);
        return new SimpleFolder(new SimpleObjectEntry(data, entry.connection));
    }

    public Collection<Folder> getParents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

    public Type getType() {
        return type;
    }

    public BaseType getBaseType() {
        return type.getBaseType();
    }

    public Serializable getValue(String name) {
        PropertyDefinition pd = getType().getPropertyDefinition(name);
        if (pd == null) {
            throw new IllegalArgumentException(name);
        }
        Serializable value = entry.data.get(name);
        if (value == null) {
            value = pd.getDefaultValue();
        }
        return value;
    }

    public Property getProperty(String name) {
        PropertyDefinition propertyDefinition = getType().getPropertyDefinition(
                name);
        if (propertyDefinition == null) {
            throw new IllegalArgumentException(name);
        }
        return new SimpleProperty(entry, name, propertyDefinition);
    }

    public void save() {
        if (getId() == null) {
            entry.connection.saveObject(this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + getTypeId() + ',' + getId()
                + ')';
    }

}
