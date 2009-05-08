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
import java.util.List;

/**
 * A CMIS Property Definition Choice.
 */
public interface Choice {

    /**
     * The choice name.
     * <p>
     * The name is used for presentation purpose.
     *
     * @return the name
     */
    String getName();

    /**
     * The choice value.
     * <p>
     * The value will be stored in the property when selected.
     * <p>
     * If {@code null}, then the name is displayed but not selectable.
     *
     * @return the value, or {@code null}
     */
    Serializable getValue();

    /**
     * The choice index.
     * <p>
     * The index provides guidance for the ordering of names when presented.
     *
     * @return the index
     */
    int getIndex();

    /**
     * The sub-choices, if hierarchical.
     * <p>
     * The sub-choices are returnd ordered by index.
     *
     * @return the collection of sub-choices, or {@code null} if none are
     *         provided
     */
    List<Choice> getSubChoices();

}
