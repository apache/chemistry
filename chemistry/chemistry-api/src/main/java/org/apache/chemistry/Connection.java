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

import java.util.Collection;

/**
 * A Connection to a CMIS Repository.
 * <p>
 * The connection can be used to interact with all the services provided by the
 * CMIS repository.
 */
public interface Connection {

    /**
     * Gets the SPI for this connection.
     * <p>
     * The SPI is used to access lower-level features of the connection.
     *
     * @return the SPI connection
     */
    SPI getSPI();

    /**
     * Closes the connection to the repository and releases all associated
     * resources.
     */
    void close();

    /**
     * Gets the repository to which this connection is associated.
     */
    Repository getRepository();

    /**
     * Gets the root folder for this repository.
     */
    Folder getRootFolder();

    /*
     * ----- Factories -----
     */

    /**
     * Creates a new, unsaved document.
     * <p>
     * If a folder is provided, it is used as the future parent for the
     * document. Otherwise if it is {@code null}, the document will be unfiled.
     *
     * @param typeId the type ID
     * @param folder the parent folder, or {@code null}
     * @see CMISObject#save
     */
    Document newDocument(String typeId, Folder folder);

    /**
     * Creates a new, unsaved folder.
     *
     * @param typeId the type ID
     * @param folder the parent folder
     * @see CMISObject#save
     */
    Folder newFolder(String typeId, Folder folder);

    /**
     * Creates a new, unsaved relationship.
     *
     * @param typeId the type ID
     * @see CMISObject#save
     */
    Relationship newRelationship(String typeId);

    /**
     * Creates a new, unsaved policy.
     * <p>
     * If a folder is provided, it is used as the future parent for the policy.
     * Otherwise if it is {@code null}, the policy will be unfiled.
     *
     * @param typeId the type ID
     * @param folder the parent folder, or {@code null}
     * @see CMISObject#save
     */
    Policy newPolicy(String typeId, Folder folder);

    /*
     * ----- Object Services -----
     */

    /**
     * Gets an object given its ID.
     *
     * @param object the object ID
     * @param returnVersion the version to be returned
     * @return the object, or {@code null} if it is not found
     */
    CMISObject getObject(ObjectId object, ReturnVersion returnVersion);

    /*
     * ----- Discovery Services -----
     */

    /**
     * Queries the repository for queryable objects.
     * <p>
     * The query is based on properties or an optional full-text string.
     * <p>
     * Content-streams are not returned as part of the query.
     * <p>
     * If searchAllVersions is {@code true}, and {@code CONTAINS()} is used in
     * the query, an exception will be thrown if full-text search is not
     * supported or if the repository does not have previous versions in the
     * full-text index.
     * <p>
     * Returns a result table produced by the query statement. Typically each
     * row of this table corresponds to an object, and each column corresponds
     * to a property or a computed value as specified by the {@code SELECT}
     * clause of the query statement.
     *
     * @param statement the SQL statement
     * @param searchAllVersions {@code true} if all versions (not only that
     *            latest) must be searched
     * @return the matching objects
     */
    // TODO returns a result set actually, there may be computed values
    Collection<CMISObject> query(String statement, boolean searchAllVersions);

}
