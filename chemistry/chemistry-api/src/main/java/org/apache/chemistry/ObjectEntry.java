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
import java.util.Collection;
import java.util.Map;

/**
 * A CMIS object entry, returned from a search or a listing.
 * <p>
 * This holds a subset of the properties of a CMIS object. The actual subset
 * will be determined by the method called to return this object entry, and by
 * the actual implementation.
 */
public interface ObjectEntry extends ObjectId {

    /**
     * Gets the type id for this entry.
     *
     * @return the type id
     */
    String getTypeId();

    /**
     * Gets the base type for this entry.
     *
     * @return the base type
     */
    BaseType getBaseType();

    /**
     * Gets a property value.
     * <p>
     * Returns {@code null} is the property is not set, not fetched or unknown.
     *
     * @param id the property ID
     * @return the property value, or {@code null}
     */
    Serializable getValue(String id);

    /**
     * Sets a property value.
     * <p>
     * Setting a {@code null} value removes the property.
     *
     * @param id the property ID
     * @param value the property value, or {@code null}
     */
    void setValue(String id, Serializable value);

    /**
     * Gets all the property values known to this entry.
     * <p>
     * The map of properties is not necessarily complete, as some of them may
     * not have been fetched.
     *
     * @return a map of the properties values
     */
    Map<String, Serializable> getValues();

    /**
     * Sets several property values.
     * <p>
     * Setting a {@code null} value removes a property.
     *
     * @param values the property values
     */
    void setValues(Map<String, Serializable> values);

    /**
     * The allowable actions, if fetched.
     */
    Collection<String> getAllowableActions();

    /**
     * The relationships in relation to this document, if fetched.
     */
    Collection<ObjectEntry> getRelationships();

}
