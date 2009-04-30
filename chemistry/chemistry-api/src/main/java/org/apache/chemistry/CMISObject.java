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
import java.util.Map;

/**
 * A CMIS Object.
 * <p>
 * This differs from an {@link ObjectEntry} by the fact that it is a "live"
 * object: it will automatically fetch any missing information from the
 * repository if needed, and the values set on this object will be stored into
 * the repository when the object is saved.
 *
 * @author Florent Guillaume
 */
public interface CMISObject extends ObjectEntry {

    /**
     * The parent folder, or the single folder in which the object is filed.
     * <p>
     * For a folder, returns the parent folder, or {@code null} if there is no
     * parent (for the root folder).
     * <p>
     * For a non-folder, if the object is single-filed then the folder in which
     * it is filed is returned, otherwise if the folder is unfiled then {@code
     * null} is returned. An exception is raised if the object is multi-filed.
     *
     * @return the parent folder, or {@code null}.
     */
    Folder getParent();

    /**
     * Sets a property value.
     * <p>
     * Setting a {@code null} value removes the property.
     * <p>
     * Whether the value is saved immediately or not is repository-specific, see
     * {@link #save()}.
     *
     * @param name the property name
     * @param value the property value, or {@code null}
     */

    void setValue(String name, Serializable value);

    /**
     * Sets several property values.
     * <p>
     * Setting a {@code null} value removes a property.
     * <p>
     * Whether the values are saved immediately or not is repository-specific,
     * see {@link #save()}.
     *
     * @param values the property values
     */
    void setValues(Map<String, Serializable> values);

    /**
     * Saves the modifications done to the object through {@link #setValue},
     * {@link #setValues} and {@link Document#setContentStream}.
     * <p>
     * Note that a repository is not required to wait until a {@code save()} is
     * called to actually save the modifications, it may do so as soon as
     * {@code setValue()} is called.
     * <p>
     * Calling {@code save()} is needed for objects newly created through
     * {@link Connection#newDocument} and similar methods.
     */
    void save();

    /*
     * ----- convenience methods for specific properties -----
     */

    void setName(String name);

}
