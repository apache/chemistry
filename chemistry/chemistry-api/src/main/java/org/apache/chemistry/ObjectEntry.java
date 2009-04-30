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
import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.property.Property;
import org.apache.chemistry.type.Type;

/**
 * A CMIS Object entry returned from a search or a listing.
 * <p>
 * This is a read-only view of a subset of the properties of a CMIS object. The
 * actual subset will be determined by the method called to return this Entry,
 * and by the actual implementation.
 *
 * @author Florent Guillaume
 */
public interface ObjectEntry {

    /**
     * The object's type definition.
     */
    Type getType();

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
     * The allowable actions, if requested.
     */
    Collection<String> getAllowableActions();

    /**
     * The relationships in relation to this document, if requested.
     */
    Collection<ObjectEntry> getRelationships();

    /*
     * ----- link to Document/Folder/etc classes -----
     */

    /**
     * Gets the full {@link Document} corresponding to this entry.
     */
    Document getDocument();

    /**
     * Gets the full {@link Folder} corresponding to this entry.
     */
    Folder getFolder();

    /**
     * Gets the full {@link Relationship} corresponding to this entry.
     */
    Relationship getRelationship();

    /**
     * Gets the full {@link Policy} corresponding to this entry.
     */
    Policy getPolicy();

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

    /**
     * Checks if the entry has an associated content stream.
     * <p>
     * Note that the content stream may be present but still have length 0.
     *
     * @return {@code true} if the entry has an associated content stream
     */
    boolean hasContentStream();

}
