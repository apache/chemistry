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
package org.apache.chemistry.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.Connection;
import org.apache.chemistry.property.PropertyDefinition;
import org.apache.chemistry.type.Type;

/**
 * A CMIS Repository.
 *
 * @author Florent Guillaume
 * @author Bogdan Stefanescu
 */
public interface Repository extends RepositoryEntry {

    /**
     * Gets a connection to the repository.
     * <p>
     * This connection can be used to use the other services offered by the
     * repository.
     * <p>
     * The connection parameters are repository-dependent; they can be used for
     * instance to authenticate a user.
     *
     * @param parameters connection parameters, or {@code null}
     */
    Connection getConnection(Map<String, Serializable> parameters);

    /*
     * ----- Repository Services -----
     */

    /**
     * Returns information about the repository and the capabilities it
     * supports.
     *
     * @return information about the repository
     */
    RepositoryInfo getInfo();

    /**
     * Gets the type definitions of the repository.
     * <p>
     * If typeId is provided, only the specific type and its descendants are
     * returned, otherwise all types are returned.
     * <p>
     * If returnPropertyDefinitions is {@code false}, then the
     * {@link PropertyDefinition}s will not be returned in each {@link Type}.
     *
     * @param typeId the base type ID, or {@code null}
     * @return the repository's types
     */
    Collection<Type> getTypes(String typeId, boolean returnPropertyDefinitions);

    /**
     * Gets the type definitions of the repository.
     * <p>
     * If typeId is provided, only the specific type and its descendants are
     * returned, otherwise all types are returned.
     * <p>
     * If returnPropertyDefinitions is {@code false}, then the
     * {@link PropertyDefinition}s will not be returned in each {@link Type}.
     * <p>
     * If maxItems is {@code 0} then a repository-specific maximum will be used.
     *
     * @param typeId the base type ID, or {@code null}
     * @param returnPropertyDefinitions {@code false} to skip property
     *            definitions
     * @param maxItems the maximum number of items, or {@code 0}
     * @param skipCount the number of results to skip in the list
     * @param hasMoreItems a 1-value boolean array to return a flag stating if
     *            there are more items
     * @return the repository's types, or a subset of them
     */
    // this API is present to mirror the wire protocol
    List<Type> getTypes(String typeId, boolean returnPropertyDefinitions,
            int maxItems, int skipCount, boolean[] hasMoreItems);

    /**
     * Gets the definition for the specified type.
     *
     * @param typeId the type ID
     * @return the type definition
     */
    Type getType(String typeId);

 
    /**
     * Get an extension service on this repository.
     * This is an optional operation and may always return null if not supported. 
     * @param <T>
     * @param klass the interface for the requested extension
     * @return the extension instance if any implementation was found
     */
    <T> T getExtension(Class<T> klass);
    
}
