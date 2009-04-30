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
package org.apache.chemistry.atompub;

import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.property.Property;

/**
 * Abdera ElementWrapper for an AtomPub cmis:object element.
 *
 * @author Florent Guillaume
 */
public class ObjectElement extends ExtensibleElementWrapper {

    public ObjectElement(Factory factory, ObjectEntry object,
            String contentStreamURI) {
        super(factory, CMIS.OBJECT);
        setProperties(object.getProperties(), contentStreamURI);
    }

    public void setProperties(Map<String, Property> properties,
            String contentStreamURI) {
        PropertiesElement el = new PropertiesElement(getFactory(),
                contentStreamURI);
        addExtension(el);
        el.setProperties(properties);
    }

    // TODO allowable actions

}
