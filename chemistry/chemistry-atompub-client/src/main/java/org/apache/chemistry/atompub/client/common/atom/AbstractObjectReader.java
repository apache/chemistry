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
package org.apache.chemistry.atompub.client.common.atom;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.Property;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.common.xml.ChildrenNavigator;
import org.apache.chemistry.atompub.client.common.xml.ParseException;
import org.apache.chemistry.atompub.client.common.xml.StaxReader;

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

    protected void readProperties(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        PropertyIterator it = new PropertyIterator(reader);
        ArrayList<XmlProperty> prefetch = null;
        Type entryType = ctx.getType();
        if (entryType == null) {
            prefetch = new ArrayList<XmlProperty>();
            while (it.hasNext()) {
                XmlProperty p = it.next();
                // System.out.println(" prefetch >>>>> "+reader.getName()+" -> "+p.value);
                if (Property.TYPE_ID.equals(p.value)) {
                    entryType = ctx.getRepository().getType(
                            (String) p.getXmlValue());
                    if (entryType == null) {
                        throw new ParseException("No such type: " + p.value);
                    }
                    prefetch.add(p);
                    break;
                } else {
                    prefetch.add(p);
                }
            }
            if (entryType == null) {
                throw new IllegalStateException("Type not known");
            }
        }
        if (prefetch != null) {
            for (XmlProperty p : prefetch) {
                p.def = entryType.getPropertyDefinition((String) p.value);
                if (p.def == null) {
                    throw new ParseException("No such property definition: "
                            + p.value + " in type " + entryType);
                }
                p.value = XmlProperty.NULL;
                // System.out.println("adding prefetched >>>>> "+reader.getName()+" -> "+p.getXmlValue());
                readProperty(ctx, reader, object, p);
            }
        }
        // consume the rest of the stream
        while (it.hasNext()) {
            XmlProperty p = it.next();
            p.def = entryType.getPropertyDefinition((String) p.value);
            if (p.def == null) {
                throw new ParseException("No such property definition: "
                        + p.value + " in type " + entryType);
            }
            p.value = XmlProperty.NULL;
            // System.out.println("adding non prefetched >>>>> "+reader.getName()+" -> "+p.getXmlValue());
            readProperty(ctx, reader, object, p);
        }
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
