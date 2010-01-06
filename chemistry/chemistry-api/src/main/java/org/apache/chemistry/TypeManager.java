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
     * Gets the types.
     * <p>
     * If typeId is provided, only the specific type and its descendants are
     * returned, otherwise all types are returned.
     *
     * @param typeId the base type ID, or {@code null}
     * @return the types, or a subset of them
     */
    Collection<Type> getTypes(String typeId);

    /**
     * Gets the types.
     * <p>
     * If typeId is provided, only the specific type and its descendants are
     * returned, otherwise all types are returned.
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
     *
     * @param typeId the base type ID, or {@code null}
     * @param depth the number of levels of depth in the type hierarchy from
     *            which to return results
     * @param includePropertyDefinitions {@code false} to skip property
     *            definitions
     * @return the types, or a subset of them
     *
     * @throws IllegalArgumentException if the depth is invalid
     */
    Collection<Type> getTypes(String typeId, int depth,
            boolean includePropertyDefinitions);

}
