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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A SPI connection to a CMIS Repository.
 * <p>
 * This API contains low-level methods that mirror the CMIS specification.
 */
public interface SPI {

    /**
     * Gets the repository for this SPI connection.
     *
     * @return the repository
     */
    Repository getRepository();

    /**
     * Closes this SPI connection.
     */
    void close();

    /*
     * ----- Factories -----
     */

    /**
     * Creates a new reference to a given object.
     *
     * @param id the object ID
     * @return a reference to that object
     */
    ObjectId newObjectId(String id);

    /**
     * Creates a new empty object entry.
     *
     * @param typeId the object type ID
     * @return a new object entry
     */
    ObjectEntry newObjectEntry(String typeId);

    /*
     * ----- Navigation Services -----
     */

    /**
     * Gets the descendants of a folder.
     * <p>
     * Returns the descendant objects contained at one or more levels in the
     * tree rooted at the specified folder. The ordering and tree walk algorithm
     * is repository-specific, but should be consistent. A depth of 1 means
     * returning only the direct children (same as {@link #getChildren}).
     * <p>
     * Only the filter-selected properties associated with each object are
     * returned. The content stream is not returned.
     * <p>
     * For a repository that supports version-specific filing, this will return
     * the version of the documents in the folder specified by the user filing
     * the documents. Otherwise, the latest version of the documents will be
     * returned.
     * <p>
     * As relationships are not fileable, type cannot be
     * {@link BaseType#RELATIONSHIP}. However, if includeRelationships is set
     * then relationships are also returned for each returned object, according
     * to the value of the parameter.
     * <p>
     * If includeAllowableActions is {@code true}, the repository will return
     * the allowable actions for the current user for each descendant object as
     * part of the results.
     * <p>
     * When returning more than one level, the objects are nested.
     *
     * @param folder the folder
     * @param depth the depth, or {@code -1} for all levels
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @param orderBy an {@code ORDER BY} clause, or {@code null}
     */
    // TODO return type for a tree
    List<ObjectEntry> getDescendants(ObjectId folder, int depth, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            String orderBy);

    /**
     * Gets the direct children of a folder.
     * <p>
     * Only the filter-selected properties associated with each object are
     * returned. The content stream is not returned.
     * <p>
     * For a repository that supports version-specific filing, this will return
     * the version of the documents in the folder specified by the user filing
     * the documents. Otherwise, the latest version of the documents will be
     * returned.
     * <p>
     * Ordering is repository-specific, except that if the repository state has
     * not changed then the ordering remains consistent across invocations.
     * <p>
     * If includeRelationships is set, relationships are also returned for each
     * returned object, according to the value of the parameter.
     * <p>
     * If includeAllowableActions is {@code true}, the repository will return
     * the allowable actions for the current user for object as part of the
     * results.
     * <p>
     * The return value hasMoreItems is filled if {@code maxItems > 0}.
     *
     * @param folder the folder
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @param maxItems the maximum number of objects to returned, or {@code 0}
     *            for a repository-specific default
     * @param skipCount the skip count
     * @param orderBy an {@code ORDER BY} clause, or {@code null}
     * @param hasMoreItems a 1-value boolean array to return a flag stating if
     *            there are more items
     */
    List<ObjectEntry> getChildren(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            int maxItems, int skipCount, String orderBy, boolean[] hasMoreItems);

    /**
     * Gets the parent of a folder.
     * <p>
     * Returns {@code null} if the specified folder is the root folder.
     * <p>
     * To find the parent of a non-folder, use {@link #getObjectParents}.
     * <p>
     * If returnToRoot is {@code false}, returns only the immediate parent of
     * the folder. If {@code true}, return an ordered list of all ancestor
     * folders from the specified folder to the root folder. The resulting list
     * is ordered by ancestry, closest to specified folder first. However, as
     * XML clients may not always respect ordering, repositories should always
     * include the parent and the ObjectID property in the filter to allow
     * re-ordering if necessary.
     *
     * @param folder the folder
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @param returnToRoot {@code true} if all ancestors must be returned
     * @return the parents and optionally relationships
     */
    List<ObjectEntry> getFolderParent(ObjectId folder, String filter,
            boolean includeAllowableActions, boolean includeRelationships,
            boolean returnToRoot);

    /**
     * Gets the direct parents of an object.
     * <p>
     * The object must be a non-folder, fileable object.
     * <p>
     * To find the parent of a folder, use {@link #getFolderParent}.
     *
     * @param object the object
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @return the collection of parent folders
     */
    Collection<ObjectEntry> getObjectParents(ObjectId object, String filter,
            boolean includeAllowableActions, boolean includeRelationships);

    /**
     * Gets the list of documents that are checked out that the user has access
     * to.
     * <p>
     * Most likely this will be the set of documents checked out by the user, bu
     * the repository may also include checked-out objects that the calling user
     * has access to, but did not check out.
     * <p>
     * If folder is not {@code null}, then the results include only direct
     * children of that folder.
     * <p>
     * The return value hasMoreItems is filled if {@code maxItems > 0}.
     *
     * @param folder the folder, or {@code null}
     * @param filter
     * @param includeAllowableActions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @param maxItems
     * @param hasMoreItems a 1-value boolean array to return a flag stating if
     *            there are more items
     * @param skipCount
     */
    Collection<ObjectEntry> getCheckedoutDocuments(ObjectId folder,
            String filter, boolean includeAllowableActions,
            boolean includeRelationships, int maxItems, int skipCount,
            boolean[] hasMoreItems);

    /*
     * ----- Object Services -----
     */

    /**
     * Creates a document.
     * <p>
     * Creates a document of the specified type, and optionally adds the
     * document to a folder.
     * <p>
     * The versioningState input is used to create a document in a
     * {@link VersioningState#CHECKED_OUT CHECKED_OUT} state, or as a checked-in
     * {@link VersioningState#MINOR MINOR} version, or as a checked-in
     * {@link VersioningState#MAJOR MAJOR} version. If created in a
     * {@link VersioningState#CHECKED_OUT CHECKED_OUT} state, the object is a
     * private working copy and there is no corresponding checked out document.
     *
     * @param typeId the document type ID
     * @param properties the properties
     * @param folder the containing folder, or {@code null}
     * @param contentStream the content stream, or {@code null}
     * @param versioningState the versioning state
     * @return the ID of the created document
     */
    ObjectId createDocument(String typeId,
            Map<String, Serializable> properties, ObjectId folder,
            ContentStream contentStream, VersioningState versioningState);

    /**
     * Creates a folder.
     * <p>
     * Creates a folder object of the specified type.
     *
     * @param typeId the folder type ID
     * @param properties the properties
     * @param folder the containing folder
     * @return the ID of the created folder
     */
    ObjectId createFolder(String typeId, Map<String, Serializable> properties,
            ObjectId folder);

    /**
     * Creates a relationship.
     * <p>
     * Creates a relationship of the specified type.
     *
     * @param typeId the relationship type ID
     * @param properties the properties
     * @param source the source
     * @param target the target
     * @return the ID of the created relationship
     */
    ObjectId createRelationship(String typeId,
            Map<String, Serializable> properties, ObjectId source,
            ObjectId target);

    /**
     * Creates a policy.
     * <p>
     * Creates a policy of the specified type, and optionally adds the policy to
     * a folder.
     *
     * @param typeId the relationship type ID
     * @param properties the properties
     * @param folder the containing folder, or {@code null}
     * @return the ID of the created policy
     */
    ObjectId createPolicy(String typeId, Map<String, Serializable> properties,
            ObjectId folder);

    /**
     * Gets the allowable actions.
     * <p>
     * Returns the list of allowable actions for an object based on the current
     * user's context, subject to any access constraints that are currently
     * imposed by the repository.
     *
     * @param object the object
     * @param asUser the user for which the check should be made, or {@code
     *            null} for the current user
     * @return the allowable actions
     */
    Collection<String> getAllowableActions(ObjectId object, String asUser);

    /**
     * Gets the properties of an object.
     * <p>
     * Returns the properties of an object, and optionally the operations that
     * the user is allowed to perform on the object.
     * <p>
     * The content stream of the object is not returned, use
     * {@link #getContentStream} for that.
     * <p>
     * If the passed object is an {@link ObjectEntry}, the repository
     * implementation may fill it with additional data and return the same
     * object.
     *
     * @param object the object
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @return the properties of the object, or {@code null} if the object is
     *         not found
     */
    ObjectEntry getProperties(ObjectId object, String filter,
            boolean includeAllowableActions, boolean includeRelationships);

    /**
     * Checks if the document has an associated content stream.
     * <p>
     * Note that the content stream may be present but still have length 0.
     *
     * @return {@code true} if the document has an associated content stream
     */
    boolean hasContentStream(ObjectId document);

    /**
     * Gets the content stream for a document.
     *
     * @param document the document
     * @param offset the offset into the content stream
     * @param length the length of stream to return, or {@code -1} for all
     * @return the specified part of the content stream
     * @throws IOException
     */
    InputStream getContentStream(ObjectId document, int offset, int length)
            throws IOException;

    /**
     * Sets the content stream for a document.
     * <p>
     * Sets (creates or replaces) the content stream for the specified document.
     * This is considered an update of the document object.
     * <p>
     * If the document is a private working copy, some repositories may not
     * support updates.
     * <p>
     * Because repositories may automatically create new document versions on a
     * user's behalf, the document returned may have a different ID from the one
     * provided as a parameter to this method.
     *
     * @param document the document
     * @param overwrite {@code true} if an already-existing content stream must
     *            be overwritten
     * @param contentStream the content stream to set
     * @return the resulting document, which may differ from the one passed as
     *         input
     */
    // TODO return ObjectId or ObjectEntry?
    ObjectId setContentStream(ObjectId document, boolean overwrite,
            ContentStream contentStream);

    /**
     * Deletes the content stream for a document.
     * <p>
     * This does not delete properties. If there are other versions this does
     * not affect them, their properties or their content streams. This does not
     * change the ID of the document.
     * <p>
     * This is considered an update of the document object.
     * <p>
     * If the document is a private working copy, some repositories may not
     * support updates.
     *
     * @param document the document
     */
    ObjectId deleteContentStream(ObjectId document);

    /**
     * Updates the properties of an object.
     * <p>
     * To remove a property, specify property with a {@code null} value. For
     * multi-value properties, the whole list of values must be provided on
     * every update. Properties not specified are not changed.
     * <p>
     * If the object is a private working copy, some repositories may not
     * support updates.
     * <p>
     * If a changeToken was provided when the object was retrieved, the change
     * token must be included as-is when calling this method.
     * <p>
     * Because repositories may automatically create new document versions on a
     * user's behalf, the object returned may have a different ID from the one
     * provided as a parameter to this method.
     *
     * @param object the object
     * @param changeToken the change token, or {@code null}
     * @param properties the properties to change
     * @return the resulting object, which may differ from the one passed as
     *         input
     */
    // TODO return ObjectId or ObjectEntry?
    ObjectId updateProperties(ObjectId object, String changeToken,
            Map<String, Serializable> properties);

    /**
     * Moves the specified filed object from one folder to another.
     * <p>
     * The targetFolder is the target folder into which the object has to be
     * moved. When the object is multi-filed, a source folder to be moved out of
     * must be specified.
     *
     * @param object the object to move
     * @param targetFolder the target folder
     * @param sourceFolder the source folder, or {@code null}
     */
    void moveObject(ObjectId object, ObjectId targetFolder,
            ObjectId sourceFolder);

    /**
     * Deletes the specified object.
     * <p>
     * When a filed object is deleted, it is removed from all folders it is
     * filed in.
     * <p>
     * If allVersions is {@code false}, this deletes a specific version of a
     * document object. If allVersions is {@code true}, then the whole version
     * series is deleted.
     * <p>
     * Deletion of a private working copy (checked out version) is the same as
     * the cancelling of a checkout.
     *
     * @param object the object to delete
     * @param allVersions if {@code true}, then delete all versions as well
     */
    void deleteObject(ObjectId object, boolean allVersions);

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
     * @param folder the folder to delete
     * @param unfiling how to unfile non-folder objects, if {@code null} then
     *            same as {@link Unfiling#DELETE}
     * @param continueOnFailure {@code true} if failure to delete one object
     *            should not stop deletion of other objects
     * @return the collection of IDs of objects that could not be deleted
     */
    Collection<ObjectId> deleteTree(ObjectId folder, Unfiling unfiling,
            boolean continueOnFailure);

    /**
     * Adds an existing non-folder, fileable object to a folder.
     *
     * @param object the object
     * @param folder the folder
     */
    void addObjectToFolder(ObjectId object, ObjectId folder);

    /**
     * Removes a non-folder object from a folder or from all folders.
     * <p>
     * If folder is {@code null}, then the the object is removed from all
     * folders.
     * <p>
     * This never deletes the object, which means that if unfiling is not
     * supported, and an object is to be removed from the last folder it exists
     * in, or is to be removed from all folders, an exception will be thrown.
     *
     * @param object the object
     * @param folder the folder, or {@code null} for all folders
     */
    void removeObjectFromFolder(ObjectId object, ObjectId folder);

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
     * <p>
     * If includeAllowableActions is {@code true}, the repository will return
     * the allowable actions for the current user for each result object in the
     * output table as an additional multi-valued column containing computed
     * values of type String, provided that each row in the output table indeed
     * corresponds to one object (which is true for a query without {@code JOIN}
     * ). If each row in the output table does not correspond to a specific
     * object and includeAllowableActions is {@code true}, then an exception
     * will be thrown.
     * <p>
     * The return value hasMoreItems is filled if {@code maxItems > 0}.
     *
     * @param statement the SQL statement
     * @param searchAllVersions {@code true} if all versions (not only that
     *            latest) must be searched
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param includeRelationships {@code true} if relationships should be
     *            included as well
     * @param maxItems the maximum number of objects to returned, or {@code 0}
     *            for a repository-specific default
     * @param skipCount the skip count
     * @param hasMoreItems
     * @return the matching objects
     */
    // TODO returns a result set actually, there may be computed values
    Collection<ObjectEntry> query(String statement, boolean searchAllVersions,
            boolean includeAllowableActions, boolean includeRelationships,
            int maxItems, int skipCount, boolean[] hasMoreItems);

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
     * @param contentCopied a return array of size 1
     * @return a reference to the private working copy
     */
    ObjectId checkOut(ObjectId document, boolean[] contentCopied);

    /**
     * Cancels a check-out.
     * <p>
     * Reverses the effect of a check-out. Removes the private working copy of
     * the checked-out document, allowing other documents in the version series
     * to be checked out again.
     *
     * @param document the private working copy
     */
    void cancelCheckOut(ObjectId document);

    /**
     * Checks in a private working copy.
     * <p>
     * Makes the private working copy the current version of the document.
     *
     * @param document the private working copy
     * @param major {@code true} if the version is a major version
     * @param properties the properties to set on the document, or {@code null}
     * @param contentStream the content stream to set on the document, or
     *            {@code null}
     * @param comment a check-in comment, or {@code null}
     * @return a reference to the new version of the document
     */
    ObjectId checkIn(ObjectId document, boolean major,
            Map<String, Serializable> properties, ContentStream contentStream,
            String comment);

    /**
     * Gets the properties of the latest version.
     * <p>
     * Returns the properties of the latest version, or the latest major
     * version, of the specified version series.
     * <p>
     * If the latest major version is requested and the series has no major
     * version, an exception is thrown.
     *
     * @param versionSeriesId the version series ID
     * @param major {@code true} if the last major version is requested
     * @param filter the properties filter, or {@code null} for all properties
     * @return a collection of properties
     */
    Map<String, Serializable> getPropertiesOfLatestVersion(
            String versionSeriesId, boolean major, String filter);

    /**
     * Gets all the versions of a document.
     * <p>
     * Returns the list of all document versions for the specified version
     * series, sorted by CREATION_DATE descending. All versions that the user
     * can access, including checked-out version and private working copy, are
     * returned.
     *
     * @param versionSeriesId the version series ID
     * @param filter the properties filter, or {@code null} for all properties
     */
    Collection<ObjectEntry> getAllVersions(String versionSeriesId, String filter);

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
     * @param filter the properties filter, or {@code null} for all properties
     * @param includeAllowableActions {@code true} to include allowable actions
     * @param maxItems the maximum number of objects to returned, or {@code 0}
     *            for a repository-specific default
     * @param skipCount the skip count
     * @param hasMoreItems a 1-value boolean array to return a flag stating if
     *            there are more items
     * @return the list of relationships
     */
    List<ObjectEntry> getRelationships(ObjectId object,
            RelationshipDirection direction, String typeId,
            boolean includeSubRelationshipTypes, String filter,
            String includeAllowableActions, int maxItems, int skipCount,
            boolean[] hasMoreItems);

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
    void applyPolicy(ObjectId policy, ObjectId object);

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
    void removePolicy(ObjectId policy, ObjectId object);

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
     * @param filter the properties filter, or {@code null} for all properties
     */
    Collection<ObjectEntry> getAppliedPolicies(ObjectId object, String filter);

}
