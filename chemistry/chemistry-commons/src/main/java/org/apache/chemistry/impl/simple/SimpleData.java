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
package org.apache.chemistry.impl.simple;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.chemistry.property.Property;

/**
 * The data held in the repository for one object.
 * <p>
 * This also holds data for objects not yet saved, in this case the ID is not
 * set, and the PARENT_ID is temporarily set to the parent's ID.
 *
 * @author Florent Guillaume
 */
public class SimpleData extends ConcurrentHashMap<String, Serializable> {

    private static final long serialVersionUID = 1L;

    public SimpleData(String typeId) {
        put(Property.TYPE_ID, typeId);
    }

}
