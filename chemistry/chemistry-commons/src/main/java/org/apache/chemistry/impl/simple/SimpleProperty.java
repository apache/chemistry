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

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.property.PropertyDefinition;

/**
 * A live property of an object.
 *
 * @author Florent Guillaume
 */
public class SimpleProperty implements Property {

    protected final ObjectEntry entry;

    protected final String name;

    public SimpleProperty(ObjectEntry entry, String name) {
        this.entry = entry;
        this.name = name;
    }

    public PropertyDefinition getDefinition() {
        return entry.getType().getPropertyDefinition(name);
    }

    public Serializable getValue() {
        return entry.getValue(name);
    }

    public void setValue(Serializable value) {
        // TODO XXX
        throw new UnsupportedOperationException();
    }

}
