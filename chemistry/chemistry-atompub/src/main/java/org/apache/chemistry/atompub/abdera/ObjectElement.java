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
import java.util.Set;

import javax.xml.namespace.QName;

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

    protected PropertiesElement properties;

    protected AllowableActionsElement allowableActions;

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
        setProperties(object.getValues(), type);
        setAllowableActions(object.getAllowableActions());
    }

    public Map<String, Serializable> getProperties(String typeId) {
        if (properties == null) {
            return Collections.emptyMap();
        }
        return properties.getProperties(typeId);
    }

    public void setProperties(Map<String, Serializable> values, Type type) {
        properties = new PropertiesElement(getFactory());
        addExtension(properties);
        properties.setProperties(values, type);
    }

    public void setAllowableActions(Set<QName> aa) {
        if (aa == null) {
            allowableActions = null;
        } else {
            allowableActions = new AllowableActionsElement(getFactory());
            addExtension(allowableActions);
            allowableActions.setAllowableActions(aa);
        }
    }

    // TODO change event info

}
