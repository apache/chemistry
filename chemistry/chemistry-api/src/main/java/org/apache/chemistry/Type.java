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

import java.net.URI;
import java.util.Collection;

/**
 * A CMIS Type Definition.
 */
public interface Type {

    /**
     * The type ID.
     * <p>
     * This uniquely identifies this type in the repository.
     *
     * @return the type ID
     */
    String getId();

    /**
     * The type's local name.
     * <p>
     * This is the underlying repository's name for the type. It is opaque and
     * has no uniqueness constraint.
     *
     * @return the type local name
     */
    String getLocalName();

    /**
     * The type's local namespace.
     * <p>
     * This is the underlying repository's internal namespace for the type local
     * name.
     *
     * @return the type local namespace, or {@code null} if not specified
     */
    URI getLocalNamespace();

    /**
     * The query name.
     * <p>
     * This is used as a table name in a SQL query. It maybe in mixed case, but
     * must uniquely identify this type within the repository
     * case-insensitively.
     *
     * @return the type's query name
     */
    String getQueryName();

    /**
     * The display name.
     * <p>
     * This is used for presentation by the application.
     *
     * @return the type's display name
     */
    String getDisplayName();

    /**
     * The parent type's ID, or {@code null} if it's a base type.
     *
     * @return type parent type's ID, or {@code null}
     */
    String getParentId();

    /**
     * The base type.
     * <p>
     * This can be only {@link BaseType#DOCUMENT DOCUMENT},
     * {@link BaseType#FOLDER FOLDER}, {@link BaseType#RELATIONSHIP
     * RELATIONSHIP} or {@link BaseType#POLICY POLICY}.
     */
    BaseType getBaseType();

    /**
     * The type's description.
     * <p>
     * This is an application's description of this type, such as the nature of
     * content, or its intended use.
     */
    String getDescription();

    /**
     * Is the type creatable.
     * <p>
     * This indicates whether new objects of this type can be created. If a type
     * is not creatable, the repository may contain objects of this type
     * already, but it is not possible to create new objects of this type.
     *
     * @return {@code true} if the type is creatable
     */
    boolean isCreatable();

    /**
     * Is the type queryable.
     * <p>
     * This indicates whether or not this type is queryable. A non-queryable
     * type is not visible through the relational view that is used for query,
     * and can not appear in the {@code FROM} clause of a query statement.
     * <p>
     * If this type is non-queryable and its super-type is queried, whether or
     * not objects of this type are included in the search scope is
     * repository-specific.
     * <p>
     * Document and Folder types should be queryable. Relationship types are not
     * queryable. Policy types may be queryable.
     *
     * @return {@code true} if the type is queryable
     */
    boolean isQueryable();

    /**
     * Is the type controllable.
     * <p>
     * This indicates whether or not objects of this type are controllable.
     * Policy objects can only be applied to controllable objects.
     * <p>
     * All types may be controllable.
     *
     * @return {@code true} if the type is controllable.
     */
    boolean isControllable();

    /**
     * Is the type included in a query of a super-type.
     * <p>
     * This controls whether this type and its subtypes appear in a query of a
     * super-type of this type.
     *
     * @return {@code true} if the type is included in a query of a super-type
     */
    boolean isIncludedInSuperTypeQuery();

    /**
     * Is the type fileable.
     * <p>
     * This indicates whether or not objects of this type are fileable. A
     * fileable object is allowed to be a child object of a Folder.
     * <p>
     * Folder types are always fileable. Document types should be fileable, and
     * are fileable if unfiling is not supported. Relationship types are not
     * fileable. Policy types may be fileable.
     *
     * @return {@code true} if the type is fileable
     */
    boolean isFileable();

    /**
     * Is the type versionable.
     * <p>
     * This indicates whether or not objects of this type are versionable.
     * <p>
     * Only Document types may be versionable.
     *
     * @return {@code true} if the type is versionable
     */
    boolean isVersionable();

    /**
     * Is a content-stream allowed or required for this type.
     * <p>
     * This indicates whether a content-stream is not allowed, allowed, or
     * required for objects of this type.
     * <p>
     * This is always {@link ContentStreamPresence#NOT_ALLOWED NOT_ALLOWED} for
     * non-Document types.
     */
    ContentStreamPresence getContentStreamAllowed();

    /**
     * The allowed source types for this relationship type.
     * <p>
     * This is a list of type IDs. The source object of a relationship object of
     * this type is constrained be of one of these listed types. If this is
     * {@code null}, then the source object can be of any type.
     * <p>
     * This is {@code null} for non-Relationship types.
     *
     * @return a list of type IDs, or {@code null}
     */
    String[] getAllowedSourceTypes();

    /**
     * The allowed target types for this relationship type.
     * <p>
     * This is a list of type IDs. The target object of a relationship object of
     * this type is constrained be of one of these listed types. If this is
     * {@code null}, then the target object can be of any type.
     * <p>
     * This is {@code null} for non-Relationship types.
     *
     * @return a list of type IDs, or {@code null}
     */
    String[] getAllowedTargetTypes();

    /**
     * The property definitions for this type.
     * <p>
     * If this type was retrieved through a call specifying {@code
     * returnPropertyDefinitions = false}, then this will return {@code null}.
     *
     * @return a collection of property definitions, or {@code null} when
     *         omitted
     */
    Collection<PropertyDefinition> getPropertyDefinitions();

    /**
     * Gets one property definition.
     * <p>
     * If this type was retrieved through a call specifying {@code
     * returnPropertyDefinitions = false}, then this will return {@code null}.
     *
     * @param id the property ID
     * @return a property definition, or {@code null} when omitted
     */
    PropertyDefinition getPropertyDefinition(String id);

}
