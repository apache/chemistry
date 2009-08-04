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
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ChangeInfo;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;

/**
 * Simple implementation of an {@link ObjectEntry}.
 * <p>
 * This implementation doesn't do type validation when values are set.
 */
public class SimpleObjectEntry implements ObjectEntry {

    protected final SimpleData data;

    protected final SimpleConnection connection;

    protected ChangeInfo changeInfo;

    protected SimpleObjectEntry(SimpleData data, SimpleConnection connection) {
        this.data = data;
        this.connection = connection;
    }

    public String getId() {
        return (String) data.get(Property.ID);
    }

    public String getTypeId() {
        return (String) data.get(Property.TYPE_ID);
    }

    public BaseType getBaseType() {
        return BaseType.get((String) data.get(Property.BASE_TYPE_ID));
    }

    public ChangeInfo getChangeInfo() {
        return changeInfo;
    }

    public void setChangeInfo(ChangeInfo changeInfo) {
        this.changeInfo = changeInfo;
    }

    public Map<String, Serializable> getValues() {
        return new HashMap<String, Serializable>(data);
    }

    public Serializable getValue(String id) {
        return data.get(id);
    }

    public void setValue(String id, Serializable value) {
        if (value == null) {
            data.remove(id);
        } else {
            data.put(id, value);
        }
    }

    public void setValues(Map<String, Serializable> values) {
        // don't use putAll as we want to check for nulls
        for (String id : values.keySet()) {
            setValue(id, values.get(id));
        }
    }

    public Map<QName, Boolean> getAllowableActions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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

}
