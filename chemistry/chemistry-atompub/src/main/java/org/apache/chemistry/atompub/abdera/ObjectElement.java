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
import java.util.Collections;
import java.util.Map;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.AtomPubCMIS;

/**
 * Abdera ElementWrapper for an AtomPub cmis:object element.
 */
public class ObjectElement extends ExtensibleElementWrapper {

    protected final PropertiesElement properties;

    /**
     * Constructor used when parsing XML.
     */
    public ObjectElement(Element internal, Repository repository) {
        super(internal);
        Element propsel = getFirstChild(CMIS.PROPERTIES);
        properties = propsel == null ? null : new PropertiesElement(propsel,
                repository);
    }

    /**
     * Constructor used when generating XML.
     */
    public ObjectElement(Factory factory, ObjectEntry object, Type type) {
        super(factory, AtomPubCMIS.OBJECT);
        properties = new PropertiesElement(getFactory());
        addExtension(properties);
        setProperties(object.getValues(), type);
    }

    public Map<String, Serializable> getProperties() {
        if (properties == null) {
            return Collections.emptyMap();
        }
        return properties.getProperties();
    }

    public void setProperties(Map<String, Serializable> values, Type type) {
        properties.setProperties(values, type);
    }

    // TODO allowable actions

    // TODO change event info

}
