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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.Type;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class JcrObjectEntry implements ObjectEntry {

    public static final String MIX_UNSTRUCTURED = "mix:unstructured";
    private static final Log log = LogFactory.getLog(JcrObjectEntry.class);

    protected Node node;

    public JcrObjectEntry(Node node) {
        this.node = node;
    }

    public JcrObjectEntry() {}

    public Collection<String> getAllowableActions() {
        throw new UnsupportedOperationException();
    }

    public Boolean getBoolean(String name) {
        try {
            return Boolean.valueOf(node.getProperty(name).getBoolean());
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get boolean value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Boolean[] getBooleans(String name) {
        try {
            Value[] values = node.getProperty(name).getValues();
            Boolean[] result = new Boolean[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = Boolean.valueOf(values[i].getBoolean());
            }
            return result;
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get boolean values: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public String getChangeToken() {
        return getString(Property.CHANGE_TOKEN);
    }

    public String getCheckinComment() {
        return getString(Property.CHECKIN_COMMENT);
    }

    public String getCreatedBy() {
        return getString(Property.CREATED_BY);
    }

    public Calendar getCreationDate() {
        return getDateTime(Property.CREATION_DATE);
    }

    public Calendar getDateTime(String name) {
        try {
            return node.getProperty(name).getDate();
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get date time value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Calendar[] getDateTimes(String name) {
        try {
            Value[] values = node.getProperty(name).getValues();
            Calendar[] result = new Calendar[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = values[i].getDate();
            }
            return result;
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get date time values: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public BigDecimal getDecimal(String name) {
        try {
            return new BigDecimal(node.getProperty(name).getDouble());
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get decimal value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public BigDecimal[] getDecimals(String name) {
        try {
            Value[] values = node.getProperty(name).getValues();
            BigDecimal[] result = new BigDecimal[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = new BigDecimal(values[i].getDouble());
            }
            return result;
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get decimal values: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Document getDocument() {
        throw new UnsupportedOperationException();
    }

    public Folder getFolder() {
        throw new UnsupportedOperationException();
    }

    public String getHTML(String name) {
        return (String) getValue(name);
    }

    public String[] getHTMLs(String name) {
        return (String[]) getValue(name);
    }

    public String getId(String name) {
        try {
            javax.jcr.Property prop = node.getProperty(name);
            return getItemId(prop);
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get item path: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public String getId() {
        try {
            return getItemId(node);
        } catch (RepositoryException e) {
            String msg = "Unable to get item path.";
            log.error(msg, e);
        }
        return null;
    }

    public String[] getIds(String name) {
        return (String[]) getValue(name);
    }

    public Integer getInteger(String name) {
        try {
            return Integer.valueOf((int) node.getProperty(name).getLong());
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get integer value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Integer[] getIntegers(String name) {
        try {
            Value[] values = node.getProperty(name).getValues();
            Integer[] result = new Integer[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = Integer.valueOf((int) values[i].getLong());
            }
            return result;
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get integer values: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Calendar getLastModificationDate() {
        return getDateTime(Property.LAST_MODIFICATION_DATE);
    }

    public String getLastModifiedBy() {
        return getString(Property.LAST_MODIFIED_BY);
    }

    public String getName() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            String msg = "Unable to get node name.";
            log.error(msg, e);
        }
        return null;
    }

    public Policy getPolicy() {
        throw new UnsupportedOperationException();
    }

    // TODO use the definition inside SimpleType
    private static final SimplePropertyDefinition PROP_TYPE_ID = new SimplePropertyDefinition(
            Property.TYPE_ID, "def:typeid", "Type ID", "", false,
            PropertyType.ID, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public Map<String, Property> getProperties() {
        Map<String, Property> properties = new HashMap<String, Property>();
        for (PropertyDefinition pd : getType().getPropertyDefinitions()) {
            String name = pd.getName();
            properties.put(name, getProperty(name));
        }
        // TODO return other virtual properties and provide helper class
        properties.put(Property.TYPE_ID, new Property() {

            public PropertyDefinition getDefinition() {
                return PROP_TYPE_ID;
            }

            public Serializable getValue() {
                return getTypeId();
            }

            public void setValue(Serializable value) {
            }
        });
        return properties;
    }

    public Property getProperty(String name) {
        try {
            return new JcrProperty(node.getProperty(name));
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get property: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Relationship getRelationship() {
        throw new UnsupportedOperationException();
    }

    public Collection<ObjectEntry> getRelationships() {
        return Collections.emptyList();
    }

    public String getString(String name) {
        try {
            return node.getProperty(name).getString();
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get string value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public String[] getStrings(String name) {
        try {
            Value[] values = node.getProperty(name).getValues();
            String[] result = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = values[i].getString();
            }
            return result;
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get string values: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Type getType() {
        try {
            return new JcrType(node.getPrimaryNodeType(), getBaseType());
        } catch (RepositoryException e) {
            String msg = "Unable to get primary node type.";
            log.error(msg, e);
        }
        return null;
    }

    protected abstract BaseType getBaseType();

    public String getTypeId() {
        return getType().getId();
    }

    public URI getURI(String name) {
        return (URI) getValue(name);
    }

    public URI getURI() {
        return getURI(Property.URI);
    }

    public URI[] getURIs(String name) {
        return (URI[]) getValue(name);
    }

    public Serializable getValue(String name) {
        try {
            if (node.hasProperty(name)) {
                Value value = node.getProperty(name).getValue();
                if (value instanceof Serializable) {
                    return (Serializable) value;
                }
            }
        } catch (PathNotFoundException e) {
            /* property does not exist */
        } catch (RepositoryException e) {
            String msg = "Unable to get value: " + name;
            log.error(msg, e);
        }
        return null;
    }

    public Map<String, Serializable> getValues() {
        Map<String, Serializable> values = new HashMap<String, Serializable>();
        for (PropertyDefinition def : getType().getPropertyDefinitions()) {
            String name = def.getName();
            values.put(name, getValue(name));
        }
        return values;
    }

    public String getVersionLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersionSeriesCheckedOutBy() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersionSeriesCheckedOutId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersionSeriesId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML(String name) {
        return (String) getValue(name);
    }

    public String[] getXMLs(String name) {
        return (String[]) getValue(name);
    }

    public boolean hasContentStream() {
        return false;
    }

    public boolean isImmutable() {
        return false;
    }

    public boolean isLatestMajorVersion() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLatestVersion() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMajorVersion() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isVersionSeriesCheckedOut() {
        // TODO Auto-generated method stub
        return false;
    }

    public static String getItemId(Item item) throws RepositoryException {
        return escape(item.getPath());
    }

    public static String getPath(String id) {
        return unescape(id);
    }

    public static String escape(String s) {
        StringBuffer result = new StringBuffer();

        char[] ach = s.toCharArray();
        for (char c : ach) {
            String esc = Integer.toHexString(c);
            int len = esc.length();
            if (len > 2) {
                esc = esc.substring(len - 2);
            } else if (len < 2) {
                result.append('0');
            }
            result.append(esc);
        }
        return result.toString();
    }

    public static String unescape(String s) {
        StringBuffer result = new StringBuffer();

        char[] ach = s.toCharArray();
        int i = 0;

        while (i < ach.length) {
            char c = (char) Integer.parseInt(s.substring(i, i + 2), 16);
            result.append(c);
            i += 2;
        }
        return result.toString();
    }

    protected void setNode(Node node) {
        this.node = node;
    }
}
