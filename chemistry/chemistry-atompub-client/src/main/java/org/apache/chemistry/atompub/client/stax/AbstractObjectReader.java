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
 *     Ugo Cei, Sourcesense
 */
package org.apache.chemistry.atompub.client.stax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.ParseException;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public abstract class AbstractObjectReader<T> extends AbstractEntryReader<T> {

    protected abstract void readProperty(ReadContext ctx, StaxReader reader,
            T object, XmlProperty p);

    @Override
    protected void readCmisElement(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        if (reader.getLocalName().equals(CMIS.OBJECT.getLocalPart())) {
            readCmisObject(ctx, reader, object);
        }
    }

    protected void readCmisObject(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            readObjectChildElement(ctx, reader, object);
        }
    }

    protected void readObjectChildElement(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        if (reader.getNamespaceURI().equals(CMIS.OBJECT.getNamespaceURI())) {
            if (reader.getLocalName().equals(CMIS.PROPERTIES.getLocalPart())) {
                readProperties(ctx, reader, object);
            } else if (reader.getLocalName().equals(
                    CMIS.ALLOWABLE_ACTIONS.getLocalPart())) {
                readAllowableActions(ctx, reader, object);
            } else { // unknown tag
                readOtherCmisElement(ctx, reader, object);
            }
        } else {
            readEntryElement(ctx, reader, object);
        }
    }

    /*
     * Reads the properties. Because the ObjectTypeId may not be known
     * initially, the properties' types cannot be computed on the fly. So the
     * properties are held until the type is found.
     */
    protected void readProperties(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        PropertyIterator it = new PropertyIterator(reader);
        List<XmlProperty> incomplete = null;
        Type entryType = ctx.getType();
        // find the type
        if (entryType == null) {
            incomplete = new ArrayList<XmlProperty>();
            while (it.hasNext()) {
                XmlProperty p = it.next();
                if (Property.TYPE_ID.equals(p.getId())) {
                    // type has been found
                    String v = (String) p.getXmlValue();
                    entryType = ctx.getRepository().getType(v);
                    if (entryType == null) {
                        throw new ParseException("No such type: " + v);
                    }
                    incomplete.add(p);
                    // stop looking for type
                    break;
                } else {
                    incomplete.add(p);
                }
            }
            if (entryType == null) {
                throw new IllegalStateException("Type not known");
            }
        }
        // fill in the type for incomplete properties
        if (incomplete != null) {
            for (XmlProperty p : incomplete) {
                readPropertyWithType(ctx, reader, object, p, entryType);
            }
        }
        // consume the rest of the stream
        while (it.hasNext()) {
            XmlProperty p = it.next();
            readPropertyWithType(ctx, reader, object, p, entryType);
        }
    }

    protected void readPropertyWithType(ReadContext ctx, StaxReader reader,
            T object, XmlProperty p, Type entryType) {
        String id = p.getId();
        PropertyDefinition def = entryType.getPropertyDefinition(id);
        if (def == null) {
            throw new ParseException("No such property definition: " + id
                    + " in type: " + entryType);
        }
        p.setDefinition(def);
        readProperty(ctx, reader, object, p);
    }

    protected void readAllowableActions(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // TODO not yet implemented
    }

    protected void readOtherCmisElement(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // do nothing
    }

}
