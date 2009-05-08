/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.impl.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;

/**
 * Base implementation of a {@link CMISObject}. The implemented methods are
 * completely generic.
 */
public abstract class BaseObject implements CMISObject {

    public Map<String, Property> getProperties() {
        Map<String, Property> properties = new HashMap<String, Property>();
        for (PropertyDefinition pd : getType().getPropertyDefinitions()) {
            String name = pd.getName();
            properties.put(name, getProperty(name));
        }
        return properties;
    }

    public void setValue(String name, Serializable value) {
        getProperty(name).setValue(value);
    }

    public void setValues(Map<String, Serializable> values) {
        for (Entry<String, Serializable> entry : values.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
    }

    /*
     * ----- Convenience methods -----
     */

    public String getString(String name) {
        return (String) getValue(name);
    }

    public String[] getStrings(String name) {
        return (String[]) getValue(name);
    }

    public BigDecimal getDecimal(String name) {
        return (BigDecimal) getValue(name);
    }

    public BigDecimal[] getDecimals(String name) {
        return (BigDecimal[]) getValue(name);
    }

    public Integer getInteger(String name) {
        return (Integer) getValue(name);
    }

    public Integer[] getIntegers(String name) {
        return (Integer[]) getValue(name);
    }

    public Boolean getBoolean(String name) {
        return (Boolean) getValue(name);
    }

    public Boolean[] getBooleans(String name) {
        return (Boolean[]) getValue(name);
    }

    public Calendar getDateTime(String name) {
        return (Calendar) getValue(name);
    }

    public Calendar[] getDateTimes(String name) {
        return (Calendar[]) getValue(name);
    }

    public URI getURI(String name) {
        return (URI) getValue(name);
    }

    public URI[] getURIs(String name) {
        return (URI[]) getValue(name);
    }

    public String getId(String name) {
        return (String) getValue(name);
    }

    public String[] getIds(String name) {
        return (String[]) getValue(name);
    }

    public String getXML(String name) {
        return (String) getValue(name);
    }

    public String[] getXMLs(String name) {
        return (String[]) getValue(name);
    }

    public String getHTML(String name) {
        return (String) getValue(name);
    }

    public String[] getHTMLs(String name) {
        return (String[]) getValue(name);
    }

    /*
     * ----- Convenience methods for specific properties -----
     */

    public String getId() {
        return getString(Property.ID);
    }

    public URI getURI() {
        return getURI(Property.URI);
    }

    public String getTypeId() {
        return getId(Property.TYPE_ID);
    }

    public String getCreatedBy() {
        return getString(Property.CREATED_BY);
    }

    public Calendar getCreationDate() {
        return getDateTime(Property.CREATION_DATE);
    }

    public String getLastModifiedBy() {
        return getString(Property.LAST_MODIFIED_BY);
    }

    public Calendar getLastModificationDate() {
        return getDateTime(Property.LAST_MODIFICATION_DATE);
    }

    public String getChangeToken() {
        return getString(Property.CHANGE_TOKEN);
    }

    public String getName() {
        return getString(Property.NAME);
    }

    public boolean isImmutable() {
        Boolean b = getBoolean(Property.IS_IMMUTABLE);
        return b == null ? false : b.booleanValue();
    }

    public boolean isLatestVersion() {
        Boolean b = getBoolean(Property.IS_LATEST_VERSION);
        return b == null ? false : b.booleanValue();
    }

    public boolean isMajorVersion() {
        Boolean b = getBoolean(Property.IS_MAJOR_VERSION);
        return b == null ? false : b.booleanValue();
    }

    public boolean isLatestMajorVersion() {
        Boolean b = getBoolean(Property.IS_LATEST_MAJOR_VERSION);
        return b == null ? false : b.booleanValue();
    }

    public String getVersionLabel() {
        return getString(Property.VERSION_LABEL);
    }

    public String getVersionSeriesId() {
        return getId(Property.VERSION_SERIES_ID);
    }

    public boolean isVersionSeriesCheckedOut() {
        Boolean b = getBoolean(Property.IS_VERSION_SERIES_CHECKED_OUT);
        return b == null ? false : b.booleanValue();
    }

    public String getVersionSeriesCheckedOutBy() {
        return getString(Property.VERSION_SERIES_CHECKED_OUT_BY);
    }

    public String getVersionSeriesCheckedOutId() {
        return getId(Property.VERSION_SERIES_CHECKED_OUT_ID);
    }

    public String getCheckinComment() {
        return getString(Property.CHECKIN_COMMENT);
    }

    public void setName(String name) {
        setValue(Property.NAME, name);
    }

}
