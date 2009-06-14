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
 */
package org.apache.chemistry.atompub.client;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.atompub.Atom;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.stax.AbstractEntryReader;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public class TypeEntryReader extends AbstractEntryReader<APPType> {

    public static final TypeEntryReader INSTANCE = new TypeEntryReader();

    @Override
    protected APPType createObject(ReadContext ctx) {
        APPType type = new APPType((APPConnection) ctx.getConnection());
        return type;
    }

    @Override
    protected void readAtomElement(ReadContext ctx, StaxReader reader,
            APPType object) throws XMLStreamException {
        // read only links - optimization to avoid useless operations
        if ("link".equals(reader.getLocalName())) {
            String rel = reader.getAttributeValue(Atom.ATOM_NS, "rel");
            String href = reader.getAttributeValue(Atom.ATOM_NS, "href");
            object.addLink(rel, href);
        }
    }

    @Override
    protected void readCmisElement(ReadContext context, StaxReader reader,
            APPType entry) throws XMLStreamException {
        if (CMIS.DOCUMENT_TYPE.getLocalPart().equals(reader.getLocalName())) {
            ChildrenNavigator children = reader.getChildren();
            Map<String, String> map = new HashMap<String, String>();
            Map<String, PropertyDefinition> pdefs = null;
            while (children.next()) {
                if (reader.getLocalName().startsWith("property")) {
                    if (pdefs == null) {
                        pdefs = new HashMap<String, PropertyDefinition>();
                    }
                    PropertyDefinition pdef = readPropertyDef(reader);
                    if (pdef.getName() == null) {
                        throw new IllegalArgumentException(
                                "Invalid property definition: no name given");
                    }
                    pdefs.put(pdef.getName(), pdef);
                } else {
                    try {
                        map.put(reader.getLocalName(), reader.getElementText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            entry.init(map, pdefs);
        }
    }

    protected PropertyDefinition readPropertyDef(StaxReader reader)
            throws XMLStreamException {
        return APPPropertyDefinition.fromXml(reader);
    }
}
