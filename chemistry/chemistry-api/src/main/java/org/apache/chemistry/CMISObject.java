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
package org.apache.chemistry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
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
 */
public interface CMISObject extends ObjectId {

    /*
     * ----- Object Services -----
     */

    /**
     * Moves this filed object from one folder to another.
     * <p>
     * The target folder is that into which the object has to be moved. When the
     * object is multi-filed, a source folder to be moved out of must be
     * specified.
     *
     * @param targetFolder the target folder
     * @param sourceFolder the source folder, or {@code null}
     */
    void move(Folder targetFolder, Folder sourceFolder);

    /**
     * Deletes this object.
     * <p>
     * When a filed object is deleted, it is removed from all folders it is
     * filed in.
     * <p>
     * This deletes a specific version of a document object. To delete all
     * versions, use {@link #deleteAllVersions}.
     * <p>
     * Deletion of a private working copy (checked out version) is the same as
     * to cancel checkout.
     */
    void delete();

    /**
     * Unfiles this non-folder object.
     * <p>
     * This removes this object from all folders it is filed in, but never
     * deletes the object, which means that if unfiling is not supported, an
     * exception will be thrown.
     * <p>
     * If this object is a folder then an exception will be thrown.
     *
     * @see #delete
     * @see Folder#remove
     */
    void unfile();

    /*
     * ----- Navigation Services -----
     */

    /**
     * Gets the parent folder, or the single folder in which the object is
     * filed.
     * <p>
     * For a folder, returns the parent folder, or {@code null} if there is no
     * parent (for the root folder).
     * <p>
     * For a non-folder, if the object is single-filed then the folder in which
     * it is filed is returned, otherwise if the folder is unfiled then {@code
     * null} is returned. An exception is raised if the object is multi-filed,
     * so in doubt use {@link #getParents}.
     *
     * @return the parent folder, or {@code null}.
     *
     * @see #getParents
     * @see Folder#getAncestors
     */
    Folder getParent();

    /**
     * Gets the direct parents of this object.
     * <p>
     * The object must be a non-folder, fileable object.
     *
     * @return the collection of parent folders
     *
     * @see #getParent
     * @see Folder#getAncestors
     */
    Collection<Folder> getParents();

    /*
     * ----- Relationship Services -----
     */

    /**
     * Gets the relationships having as source or target this object.
     * <p>
     * Returns a list of relationships associated with this object, optionally
     * of a specified relationship type, and optionally in a specified
     * direction.
     * <p>
     * If typeId is {@code null}, returns relationships of any type.
     * <p>
     * Ordering is repository specific but consistent across requests.
     *
     * @param direction the direction of relationships to include
     * @param typeId the type ID, or {@code null}
     * @param includeSubRelationshipTypes {@code true} if relationships of any
     *            sub-type of typeId are to be returned as well
     * @return the list of relationships
     */
    List<Relationship> getRelationships(RelationshipDirection direction,
            String typeId, boolean includeSubRelationshipTypes);

    /*
     * ----- Policy Services -----
     */

    /**
     * Applies a policy to this object.
     * <p>
     * The object must be controllable.
     *
     * @param policy the policy
     */
    void applyPolicy(Policy policy);

    /**
     * Removes a policy from this object.
     * <p>
     * Removes a previously applied policy from the object. The policy is not
     * deleted, and may still be applied to other objects.
     * <p>
     * The object must be controllable.
     *
     * @param policy the policy
     */
    void removePolicy(Policy policy);

    /**
     * Gets the policies applied to this object.
     * <p>
     * Returns the list of policy objects currently applied to the object. Only
     * policies that are directly (explicitly) applied to the object are
     * returned.
     * <p>
     * The object must be controllable.
     */
    Collection<Policy> getPolicies();

    /*
     * ----- data access -----
     */

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
