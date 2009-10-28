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
 *     Ugo Cei, Sourcesense
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.client.stax.AbstractEntryReader;
import org.apache.chemistry.atompub.client.stax.ReadContext;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class TypeEntryReader extends AbstractEntryReader<APPType> {

    private static final Log log = LogFactory.getLog(TypeEntryReader.class);

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
            String rel = reader.getAttributeValue(AtomPub.ATOM_NS, "rel");
            String href = reader.getAttributeValue(AtomPub.ATOM_NS, "href");
            object.addLink(rel, href);
        }
    }

    @Override
    protected void readCmisElement(ReadContext context, StaxReader reader,
            APPType entry) throws XMLStreamException {
        if (AtomPubCMIS.TYPE.getLocalPart().equals(reader.getLocalName())) {
            ChildrenNavigator children = reader.getChildren();
            Map<String, String> map = new HashMap<String, String>();
            Map<String, PropertyDefinition> pdefs = null;
            while (children.next()) {
                String name = reader.getLocalName();
                if (name.startsWith("property")) {
                    if (pdefs == null) {
                        pdefs = new HashMap<String, PropertyDefinition>();
                    }
                    PropertyDefinition pdef = readPropertyDef(reader);
                    if (pdef.getId() == null) {
                        throw new IllegalArgumentException(
                                "Invalid property definition: no id given");
                    }
                    pdefs.put(pdef.getId(), pdef);
                } else {
                    String text;
                    try {
                        text = reader.getElementText();
                    } catch (XMLStreamException e) {
                        log.error("Cannot read text element for: " + name, e);
                        continue;
                    }
                    map.put(name, text);
                }
            }
            // check some mandatory properties
            // TODO also for documents: versionable, contentStreamAllowed
            // TODO also for relationships: allowedSourceTypes,
            // allowedTargetTypes
            for (QName qname : Arrays.asList( //
                    CMIS.ID, //
                    CMIS.LOCAL_NAME, //
                    CMIS.QUERY_NAME, //
                    CMIS.DISPLAY_NAME, //
                    CMIS.BASE_ID, //
                    CMIS.PARENT_ID, //
                    CMIS.DESCRIPTION, //
                    CMIS.CREATABLE, //
                    CMIS.FILEABLE, //
                    CMIS.QUERYABLE, //
                    CMIS.CONTROLLABLE_POLICY, //
                    CMIS.CONTROLLABLE_ACL, //
                    CMIS.FULLTEXT_INDEXED, //
                    CMIS.INCLUDED_IN_SUPERTYPE_QUERY //
            )) {
                if (!map.containsKey(qname.getLocalPart())) {
                    throw new IllegalArgumentException(
                            "Invalid type definition: missing "
                                    + qname.getPrefix() + ':'
                                    + qname.getLocalPart());
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
