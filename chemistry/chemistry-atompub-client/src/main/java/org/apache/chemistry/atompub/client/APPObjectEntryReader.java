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
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.client.stax.AbstractObjectReader;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.atompub.client.stax.XmlProperty;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class APPObjectEntryReader extends AbstractObjectReader<APPObjectEntry> {

    private static APPObjectEntryReader builder = new APPObjectEntryReader();

    public static APPObjectEntryReader getBuilder() {
        return builder;
    }

    @Override
    protected APPObjectEntry createObject(ReadContext ctx) {
        Type type = ctx.getType();
        APPConnection connection = (APPConnection) ctx.getConnection();
        if (type == null) {
            return new APPObjectEntry(connection,
                    new HashMap<String, XmlProperty>(), null);
        } else {
            return connection.newObjectEntry(type.getId());
        }
    }

    @Override
    protected void readProperty(ReadContext ctx, StaxReader reader,
            APPObjectEntry object, XmlProperty p) {
        object.properties.put(p.getId(), p);
    }

    @Override
    protected void readAllowableActions(ReadContext ctx, StaxReader reader,
            APPObjectEntry object, Map<QName, Boolean> allowableActions) {
        object.allowableActions = Collections.unmodifiableMap(allowableActions);
    }

    @Override
    protected void readAtomElement(ReadContext ctx, StaxReader reader,
            APPObjectEntry object) throws XMLStreamException {
        String name = reader.getLocalName();
        if ("link".equals(name)) {
            String rel = reader.getAttributeValue(AtomPub.ATOM_NS, "rel");
            String href = reader.getAttributeValue(AtomPub.ATOM_NS, "href");
            object.addLink(rel, href);
            // } else if ("id".equals(name)) {
            // object.id = new URI(id);
        }
    }

}
