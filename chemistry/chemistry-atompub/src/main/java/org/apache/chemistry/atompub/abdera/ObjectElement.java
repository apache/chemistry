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
package org.apache.chemistry.atompub.abdera;

import java.io.Serializable;
import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;

/**
 * Abdera ElementWrapper for an AtomPub cmis:object element.
 */
public class ObjectElement extends ExtensibleElementWrapper {

    public ObjectElement(Factory factory, ObjectEntry object, Type type,
            String contentStreamURI) {
        super(factory, CMIS.OBJECT);
        setProperties(object.getValues(), type, contentStreamURI);
    }

    public void setProperties(Map<String, Serializable> values, Type type,
            String contentStreamURI) {
        PropertiesElement el = new PropertiesElement(getFactory(),
                contentStreamURI);
        addExtension(el);
        el.setProperties(values, type);
    }

    // TODO allowable actions

}
