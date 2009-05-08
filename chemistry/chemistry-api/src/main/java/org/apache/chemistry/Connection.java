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
import java.util.List;

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

    /**
     * Moves the specified filed object from one folder to another.
     * <p>
     * The target folder is that into which the object has to be moved. When the
     * object is multi-filed, a source folder to be moved out of must be
     * specified.
     *
     * @param object the object
     * @param targetFolder the target folder
     * @param sourceFolder the source folder, or {@code null}
     */
    void moveObject(CMISObject object, Folder targetFolder, Folder sourceFolder);

    /**
     * Deletes the specified object.
     * <p>
     * When a filed object is deleted, it is removed from all folders it is
     * filed in.
     * <p>
     * This service deletes a specific version of a document object. To delete
     * all versions, use {@link #deleteAllVersions}.
     * <p>
     * Deletion of a private working copy (checked out version) is the same as
     * to cancel checkout.
     *
     * @param object the object
     */
    void deleteObject(CMISObject object);

    /**
     * Deletes a tree of objects.
     * <p>
     * Deletes the tree rooted at the specified folder (including that folder).
     * <p>
     * If a non-folder object is removed from the last folder it is filed in, it
     * can continue to survive outside of the folder structure if the repository
     * supports unfiling; this is controlled based on the unfiling parameter.
     * <p>
     * For repositories that support version-specific filing, this may delete
     * some versions of a document but not necessarily all versions. For
     * repositories that do not support version-specific filing, if a document
     * is to be deleted, all versions are deleted.
     * <p>
     * This method is not transactional. However, if
     * {@link Unfiling#DELETE_SINGLE_FILED DELETE_SINGLE_FILED} and some objects
     * fail to delete, then single-filed objects are either deleted or kept,
     * never just unfiled. This is so that user can call this method again to
     * recover from the error by using the same tree.
     * <p>
     * The order in which deletion will happen is unspecified; however, any
     * objects that are not deleted (e.g., because a previous object failed to
     * delete) remain valid objects (including any applicable filing
     * constraint).
     * <p>
     * Returns the collection of IDs of objects that could not be deleted. If
     * all objects could be deleted, an empty collection is returned. If at
     * least one object could not be deleted, then if continueOnFailure is
     * {@code false} that single object ID is returned, otherwise all IDs of
     * objects that could not be deleted are returned.
     *
     * @param folder the folder
     * @param unfiling how to unfile non-folder objects, if {@code null} then
     *            same as {@link Unfiling#DELETE}
     * @param continueOnFailure {@code true} if failure to delete one object
     *            should not stop deletion of other objects
     * @return the collection of IDs of objects that could not be deleted
     */
    Collection<String> deleteTree(Folder folder, Unfiling unfiling,
            boolean continueOnFailure);

    /**
     * Adds an existing non-folder, fileable object to a folder.
     *
     * @param object the object
     * @param folder the folder
     */
    void addObjectToFolder(CMISObject object, Folder folder);

    /**
     * Removes a non-folder object from a folder or from all folders.
     * <p>
     * If folderId is {@code null}, then the the object is removed from all
     * folders.
     * <p>
     * This never deletes the object, which means that if unfiling is not
     * supported, and an object is to be removed from the last folder it exists
     * in, or is to be removed from all folders, an exception will be thrown.
     *
     * @param object the object
     * @param folder the folder, or {@code null} for all folders
     */
    void removeObjectFromFolder(CMISObject object, Folder folder);

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

    /*
     * ----- Versioning Services -----
     */

    /**
     * Checks out a document.
     * <p>
     * Creates a private working copy of the document, copies the metadata and
     * optionally content.
     * <p>
     * It is up to the repository to determine if updates to the current version
     * (not private working copy) and prior versions are allowed if checked out.
     * <p>
     * Some repositories may not support updating of private working copies and
     * the updates must then be supplied via {@link #checkIn}.
     * <p>
     * This method may remove update permission on prior versions.
     * <p>
     * The ID of the private working copy is returned.
     * <p>
     * The return value contentCopied[0] is set to {@code true} if the content
     * is copied, {@code false} if not. Whether the content is copied on
     * check-out or not is repository-specific.
     *
     * @param document the document
     * @return the private working copy
     */
    Document checkOut(Document document);

    /**
     * Cancels a check-out.
     * <p>
     * Reverses the effect of a check-out. Removes the private working copy of
     * the checked-out document, allowing other documents in the version series
     * to be checked out again.
     *
     * @param document the private working copy
     */
    void cancelCheckOut(Document document);

    /**
     * Checks in a private working copy.
     * <p>
     * Makes the private working copy the current version of the document.
     *
     * @param document the private working copy
     * @param major {@code true} if the version is a major version
     * @param comment a check-in comment, or {@code null}
     * @return the the new version of the document
     */
    Document checkIn(Document document, boolean major, String comment);

    /**
     * Gets the latest version.
     * <p>
     * Returns the latest version, or the latest major version, of the specified
     * document.
     * <p>
     * If the latest major version is requested and the series has no major
     * version, an exception is thrown.
     *
     * @param document the document
     * @param major {@code true} if the last major version is requested
     * @return the latest version or latest major version
     */
    Document getLatestVersion(Document document, boolean major);

    /**
     * Gets all the versions of a document.
     * <p>
     * Returns the list of all document versions for the specified version
     * series, sorted by CREATION_DATE descending. All versions that the user
     * can access, including checked-out version and private working copy, are
     * returned.
     *
     * @param document the document
     * @param filter the properties filter, or {@code null} for all properties
     */
    Collection<Document> getAllVersions(Document document, String filter);

    /**
     * Deletes all the versions of a document.
     * <p>
     * Deletes all document versions in the version series of the specified
     * document.
     *
     * @param document the document
     */
    void deleteAllVersions(Document document);

    /*
     * ----- Relationship Services -----
     */

    /**
     * Gets the relationships having as source or target a given document.
     * <p>
     * Returns a list of relationships associated with the given object,
     * optionally of a specified relationship type, and optionally in a
     * specified direction.
     * <p>
     * If typeId is {@code null}, returns relationships of any type.
     * <p>
     * Ordering is repository specific but consistent across requests.
     *
     * @param object the object
     * @param direction the direction of relationships to include
     * @param typeId the type ID, or {@code null}
     * @param includeSubRelationshipTypes {@code true} if relationships of any
     *            sub-type of typeId are to be returned as well
     * @return the list of relationships
     */
    List<Relationship> getRelationships(CMISObject object,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes);

    /*
     * ----- Policy Services -----
     */

    /**
     * Applies a policy to an object.
     * <p>
     * The target object must be controllable.
     *
     * @param policy the policy
     * @param object the target object
     */
    void applyPolicy(Policy policy, CMISObject object);

    /**
     * Removes a policy from an object.
     * <p>
     * Removes a previously applied policy from a target object. The policy is
     * not deleted, and may still be applied to other objects.
     * <p>
     * The target object must be controllable.
     *
     * @param policy the policy
     * @param object the target object
     */
    void removePolicy(Policy policy, CMISObject object);

    /**
     * Gets the policies applied to an object.
     * <p>
     * Returns the list of policy objects currently applied to a target object.
     * Only policies that are directly (explicitly) applied to the target object
     * are returned.
     * <p>
     * The target object must be controllable.
     *
     * @param object the target object
     */
    Collection<Policy> getAppliedPolicies(CMISObject object);

}
