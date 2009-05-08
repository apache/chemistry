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
import java.net.URI;
import java.util.List;

/**
 * A CMIS Property Definition.
 *
 * @author Florent Guillaume
 */
public interface PropertyDefinition {

    /**
     * The property's name.
     * <p>
     * This identifies this property among all properties of this type,
     * including inherited properties. This is also used as a column name in a
     * SQL query. It may be in mixed case, but must uniquely identify this
     * property case-insensitively, and must conform to the syntax rules for SQL
     * identifiers.
     *
     * @return the property's name
     */
    String getName();

    /**
     * The property ID.
     * <p>
     * This contains a system-assigned ID which uniquely identifies this
     * property.
     * <p>
     * Two properties of different names from different types may have the same
     * ID if they are considered to represent "identical" information by the
     * repository.
     *
     * @return the property ID
     */
    String getId();

    /**
     * The property's display name.
     * <p>
     * This is used for presentation by the application.
     *
     * @return the property's display name
     */
    String getDisplayName();

    /**
     * The property's description.
     * <p>
     * This is a description of the property, or {@code null} if none is
     * provided.
     *
     * @return the property's description, or {@code null}
     */
    String getDescription();

    /**
     * Is the property inherited.
     * <p>
     * This indicates whether the property is inherited from the parent type or
     * it is explicitly defined for the type from which this property definition
     * was retrieved.
     *
     * @return {@code true} if the property is inherited
     */
    boolean isInherited();

    /**
     * The property's type.
     * <p>
     * This indicates the type of this property.
     *
     * @return the property's type
     */
    PropertyType getType();

    /**
     * Is the property multi-valued.
     * <p>
     * Repositories should preserve the ordering of values in a multi-valued
     * property. That is, the order in which the values of a multi-valued
     * property are returned in "read" operations should be the same as the
     * order in which they were supplied during previous 'write" operation.
     *
     * @return true if the property is multi-valued
     */
    boolean isMultiValued();

    /**
     * The choices for this property.
     * <p>
     * This is optional and is only applicable to application-maintained
     * properties. It specifies what property values are allowed. If choices are
     * not specified, there is no constraint on the data value.
     * <p>
     * The choices are returned ordered by index.
     *
     * @return a list of choices, or {@code null} if no choices are provided
     */
    List<Choice> getChoices();

    /**
     * Are the choices open.
     * <p>
     * This is only applicable to properties that provide a value for
     * {@link #getChoices}.
     * <p>
     * If {@code false}, then the value for the property must be one of the
     * values specified by {@link #getChoices}. If {@code true}, then values
     * other than those included from {@link #getChoices} may be used for the
     * property.
     *
     * @return {@code true} if the choices are open
     */
    boolean isOpenChoice();

    /**
     * Is the property required.
     * <p>
     * A property that is required can never be {@code null}.
     * <p>
     * For non-read-only properties: the value of a required property is never
     * {@code null}. If a value is not provided by the application, then the
     * default value is used. If no default value is defined, then this
     * constraint is violated.
     * <p>
     * For read-only properties: the value of a required system property must
     * always be set by the repository or computed by the repository when it is
     * requested by an application. A not required system property may be left
     * {@code null}.
     *
     * @return {@code true} if the property is required
     */
    boolean isRequired();

    /**
     * The property's default value.
     * <p>
     * This is optional and only applicable to application-maintained
     * properties. The property will have this value if a value is not provided
     * by the application. Without a default value, the property value will be
     * left {@code null} until a value is provided by the application.
     *
     * @return the default value, or {@code null} if none is provided
     */
    Serializable getDefaultValue();

    /**
     * The property's updatability status.
     * <p>
     * This can be {@link Updatability#READ_ONLY read-only},
     * {@link Updatability#READ_WRITE read-write} or
     * {@link Updatability#WHEN_CHECKED_OUT read-write when checked out}.
     */
    Updatability getUpdatability();

    /**
     * Is the property queryable.
     * <p>
     * This defines whether or not the property can appear in the {@code WHERE}
     * clause of a SQL {@code SELECT} statement. Only the properties of a
     * queryable type, both inherited and specifically defined properties, may
     * be queryable.
     * <p>
     * Note that "Queryable" has a different meaning for type and for property.
     * The former pertains to the {@code FROM} clause and the latter pertains to
     * the {@code WHERE} clause.
     *
     * @return {@code true} if the property is queryable
     */
    boolean isQueryable();

    /**
     * Is the property orderable.
     * <p>
     * This defines whether or not the property can appear in the {@code ORDER
     * BY} clause of a SQL {@code SELECT} statement.
     * <p>
     * Only single-valued properties of a queryable type may be orderable.
     *
     * @return {@code true} if the property is orderable
     */
    boolean isOrderable();

    /**
     * The precision for this decimal property.
     * <p>
     * This is the precision in bits supported for this property (32 or 64
     * currently).
     *
     * @return the precisions
     */
    int getPrecision();

    /**
     * The minimum value for this integer property.
     *
     * @return the minimum value, or {@code null} if none is provided
     */
    Integer getMinValue();

    /**
     * The maximum value for this integer property.
     *
     * @return the maximum value, or {@code null} if none is provided
     */
    Integer getMaxValue();

    /**
     * The maximum length of this string property.
     *
     * @return the maximum length, or -1 if none is provided
     */
    int getMaxLength();

    /**
     * The URI of the XML schema for this XML property.
     * <p>
     * This provides the URI location of an XML schema to which the property
     * value must conform.
     *
     * @return the URI of the XML schema for this property
     */
    URI getSchemaURI();

    /**
     * The encoding for this XML property.
     * <p>
     * This specifies the encoding used for the property value (e.g. UTF-8,
     * etc.).
     *
     * @return the encoding for this property
     */
    String getEncoding();

    /**
     * Checks if a value can be set in this property.
     *
     * @param value the candidate value
     * @return {@code true} if the value can be set, {@code false} if not
     */
    boolean validates(Serializable value);

}
