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
package org.apache.chemistry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;

/**
 * A CMIS Object.
 * <p>
 * This interface contains a lot of common functionality for the four
 * sub-interfaces that are actually implemented: {@link Folder},
 * {@link Document}, {@link Relationship} and {@link Policy}.
 * <p>
 * This is a "live" object, it will automatically fetch any missing information
 * from the repository if needed, and the values set on this object will be
 * stored into the repository when the object is saved.
 *
 * @see Folder
 * @see Document
 * @see Relationship
 * @see Policy
 * @author Florent Guillaume
 */
public interface CMISObject extends ObjectId {

    /**
     * The object's type definition.
     */
    Type getType();

    /*
     * ----- data access -----
     */

    /**
     * Gets a property.
     *
     * @param name the property name
     * @return the property
     */
    Property getProperty(String name);

    /**
     * Gets all the properties.
     *
     * @return a map of the properties
     */
    Map<String, Property> getProperties();

    /**
     * Gets a property value.
     *
     * @param name the property name
     * @return the property value
     */
    Serializable getValue(String name);

    /**
     * Sets a property value.
     * <p>
     * Setting a {@code null} value removes the property.
     * <p>
     * Whether the value is saved immediately or not is repository-specific, see
     * {@link #save()}.
     *
     * @param name the property name
     * @param value the property value, or {@code null}
     */
    void setValue(String name, Serializable value);

    /**
     * Sets several property values.
     * <p>
     * Setting a {@code null} value removes a property.
     * <p>
     * Whether the values are saved immediately or not is repository-specific,
     * see {@link #save()}.
     *
     * @param values the property values
     */
    void setValues(Map<String, Serializable> values);

    /**
     * Saves the modifications done to the object through {@link #setValue},
     * {@link #setValues} and {@link Document#setContentStream}.
     * <p>
     * Note that a repository is not required to wait until a {@link #save} is
     * called to actually save the modifications, it may do so as soon as
     * {@link #setValue} is called.
     * <p>
     * Calling {#link #save} is needed for objects newly created through
     * {@link Connection#newDocument} and similar methods.
     */
    void save();

    /*
     * ----- misc -----
     */

    /**
     * The parent folder, or the single folder in which the object is filed.
     * <p>
     * For a folder, returns the parent folder, or {@code null} if there is no
     * parent (for the root folder).
     * <p>
     * For a non-folder, if the object is single-filed then the folder in which
     * it is filed is returned, otherwise if the folder is unfiled then {@code
     * null} is returned. An exception is raised if the object is multi-filed.
     *
     * @return the parent folder, or {@code null}.
     */
    Folder getParent();

    /*
     * ----- convenience methods -----
     */

    String getString(String name);

    String[] getStrings(String name);

    BigDecimal getDecimal(String name);

    BigDecimal[] getDecimals(String name);

    Integer getInteger(String name);

    Integer[] getIntegers(String name);

    Boolean getBoolean(String name);

    Boolean[] getBooleans(String name);

    Calendar getDateTime(String name);

    Calendar[] getDateTimes(String name);

    URI getURI(String name);

    URI[] getURIs(String name);

    String getId(String name);

    String[] getIds(String name);

    String getXML(String name);

    String[] getXMLs(String name);

    String getHTML(String name);

    String[] getHTMLs(String name);

    /*
     * ----- convenience methods for specific properties -----
     */

    String getId();

    URI getURI();

    String getTypeId();

    String getCreatedBy();

    Calendar getCreationDate();

    String getLastModifiedBy();

    Calendar getLastModificationDate();

    String getChangeToken();

    String getName();

    boolean isImmutable();

    boolean isLatestVersion();

    boolean isMajorVersion();

    boolean isLatestMajorVersion();

    String getVersionLabel();

    String getVersionSeriesId();

    boolean isVersionSeriesCheckedOut();

    String getVersionSeriesCheckedOutBy();

    String getVersionSeriesCheckedOutId();

    String getCheckinComment();

    /*
     * ----- convenience methods for specific properties (setter) -----
     */

    void setName(String name);

}
