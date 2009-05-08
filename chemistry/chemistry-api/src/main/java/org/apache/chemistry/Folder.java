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

import java.util.List;


/**
 * A CMIS Folder.
 */
public interface Folder extends CMISObject {

    /**
     * Gets the direct children of a folder.
     *
     * @param type the base type, or {@code null} for all types
     * @param orderBy an {@code ORDER BY} clause, or {@code null}
     * @return the list of children
     */
    List<CMISObject> getChildren(BaseType type, String orderBy);

    // getDescendants and getParents kept on the Connection

    /**
     * Creates a new, unsaved document as a child of this folder.
     */
    Document newDocument(String typeId);

    /**
     * Creates a new, unsaved folder as a child of this folder.
     */
    Folder newFolder(String typeId);

}
