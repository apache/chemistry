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
import java.util.List;

/**
 * A CMIS Folder.
 */
public interface Folder extends CMISObject {

    /*
     * ----- Object Services -----
     */

    /**
     * Adds an existing non-folder, fileable object to this folder.
     *
     * @param object the object
     */
    void add(CMISObject object);

    /**
     * Removes a non-folder object from this folder.
     * <p>
     * This never deletes the object, which means that if unfiling is not
     * supported, and an object is to be removed from the last folder it exists
     * in, an exception will be thrown.
     *
     * @param object the object
     *
     * @see CMISObject#delete
     * @see CMISObject#unfile
     */
    void remove(CMISObject object);

    /**
     * Deletes a tree of objects.
     * <p>
     * Deletes the tree rooted at this folder (including the folder itself).
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
     * {@link Unfiling#DELETE_SINGLE_FILED} and some objects fail to delete,
     * then single-filed objects are either deleted or kept, never just unfiled.
     * This is so that user can call this method again to recover from the error
     * by using the same tree.
     * <p>
     * This method attempts to continue deleting objects on failure of deleting
     * one object.
     * <p>
     * The order in which deletion will happen is unspecified.
     * <p>
     * Returns the collection of IDs of objects that could not be deleted. If
     * all objects could be deleted, an empty collection is returned.
     *
     * @param unfiling how to unfile non-folder objects, if {@code null} then
     *            same as {@link Unfiling#DELETE}
     * @return the collection of IDs of objects that could not be deleted
     */
    Collection<ObjectId> deleteTree(Unfiling unfiling);

    /*
     * ----- Navigation Services -----
     */

    /**
     * Gets the ancestors of this folder.
     * <p>
     * Returns an ordered list of all ancestor folders from this specified
     * folder to the root folder. The resulting list is ordered by ancestry,
     * closest this folder first.
     * <p>
     * Returns an empty list if this folder is the root folder.
     *
     * @return the list of ancestors
     *
     * @see CMISObject#getParent
     * @see CMISObject#getParents
     */
    List<Folder> getAncestors();

    /**
     * Gets the direct children of this folder.
     * <p>
     * The order of returned children is implementation-dependant.
     *
     * @return the list of children
     */
    List<CMISObject> getChildren();

    // getDescendants kept on the SPI

    /*
     * ----- Factories -----
     */

    /**
     * Creates a new, unsaved document as a child of this folder.
     */
    Document newDocument(String typeId);

    /**
     * Creates a new, unsaved folder as a child of this folder.
     */
    Folder newFolder(String typeId);

}
