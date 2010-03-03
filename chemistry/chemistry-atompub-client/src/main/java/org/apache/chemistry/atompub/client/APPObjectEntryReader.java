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
 *     Bogdan Stefanescu, Nuxeo
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Tree;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.client.stax.AbstractObjectReader;
import org.apache.chemistry.atompub.client.stax.XmlProperty;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class APPObjectEntryReader extends AbstractObjectReader<APPObjectEntry> {

    @Override
    protected APPObjectEntry createObject(APPContext ctx) {
        APPConnection connection = (APPConnection) ctx.getConnection();
        return new APPObjectEntry(connection,
                new HashMap<String, XmlProperty>(), null);
    }

    @Override
    protected void setProperty(APPObjectEntry object, XmlProperty p) {
        object.properties.put(p.getId(), p);
    }

    @Override
    protected void setAllowableActions(APPObjectEntry object,
            Set<QName> allowableActions) {
        object.allowableActions = Collections.unmodifiableSet(allowableActions);
    }

    @Override
    protected void setPathSegment(APPObjectEntry object, String pathSegment) {
        object.pathSegment = pathSegment;
    }

    @Override
    protected void setChildren(APPObjectEntry object,
            List<Tree<ObjectEntry>> list) {
        object.children = list;
    }

    @Override
    protected void readAtomElement(APPContext ctx, StaxReader reader,
            APPObjectEntry object) throws XMLStreamException {
        QName name = reader.getName();
        if (AtomPub.ATOM_CONTENT.equals(name)) {
            String href = reader.getAttributeValue(AtomPub.ATOM_NS, "src");
            String type = reader.getAttributeValue(AtomPub.ATOM_NS, "type");
            object.addContentHref(href, type);
        } else if (AtomPub.ATOM_LINK.equals(name)) {
            String rel = reader.getAttributeValue(AtomPub.ATOM_NS, "rel");
            String href = reader.getAttributeValue(AtomPub.ATOM_NS, "href");
            String type = reader.getAttributeValue(AtomPub.ATOM_NS, "type");
            object.addLink(rel, href, type);
            // } else if ("id".equals(name)) {
            // object.id = new URI(id);
        }
    }

}
