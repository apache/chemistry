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

import java.util.Collection;

/**
 * A manager of types.
 */
public interface TypeManager {

    /**
     * Adds a new type to the type manager.
     * <p>
     * In most cases, this method will throw
     * {@link UnsupportedOperationException}.
     *
     * @param type the new type.
     */
    void addType(Type type);

    /**
     * Gets the specified type.
     *
     * @param typeId the type ID
     * @return the type, or {@code null} if not found
     */
    Type getType(String typeId);

    /**
     * Gets the specified property definition.
     *
     * @param id the property ID
     * @return the property definition, or {@code null} if not found
     */
    PropertyDefinition getPropertyDefinition(String id);

    /**
     * Gets all the types.
     *
     * @return all the types
     */
    Collection<Type> getTypes();

    /**
     * Gets a type and all its descendants.
     * <p>
     * Note that contrary to {@link #getTypeDescendants(String, int, boolean)}
     * the type itself is also returned.
     *
     * @param typeId the type ID, or {@code null} for all types
     * @return the type and its descendants, or {@code null} if not found
     *
     * @throws IllegalArgumentException if the type does not exist
     */
    Collection<Type> getTypeDescendants(String typeId);

    /**
     * Gets the children of a given type.
     * <p>
     * If typeId is {@code null} then the supported base types are returned.
     *
     * @param typeId the type ID
     * @param includePropertyDefinitions {@code false} to skip property
     *            definitions
     * @param paging paging information, or {@code null} for a
     *            repository-specific default
     * @return the children types
     *
     * @throws IllegalArgumentException if the type does not exist
     */
    ListPage<Type> getTypeChildren(String typeId,
            boolean includePropertyDefinitions, Paging paging);

    /**
     * Gets the descendants of a given type.
     * <p>
     * If typeId is provided only its descendants are returned (up to the
     * specified depth), otherwise if typeId is {@code null} all the types are
     * returned (and depth is ignored).
     * <p>
     * The depth parameter controls the number of levels of the type hierarchy
     * to return:
     * <ul>
     * <li>-1: all descendants at all depths,</li>
     * <li>1: only children of the type,</li>
     * <li>more than 1: grand-children up to that depth,</li>
     * </ul>
     * If includePropertyDefinitions is {@code false}, then the
     * {@link PropertyDefinition}s will not be returned in each {@link Type}.
     * <p>
     * Note that contrary to {@link #getTypeDescendants(String)} if a typeId is
     * passed, the type itself is not returned.
     *
     * @param typeId the base type ID, or {@code null}
     * @param depth the number of levels of depth in the type hierarchy from
     *            which to return results
     * @param includePropertyDefinitions {@code false} to skip property
     *            definitions
     * @return the types, or a subset of them, or {@code null} if not found
     *
     * @throws IllegalArgumentException if the depth is invalid or the type does
     *             not exist
     */
    Collection<Type> getTypeDescendants(String typeId, int depth,
            boolean includePropertyDefinitions);

}
