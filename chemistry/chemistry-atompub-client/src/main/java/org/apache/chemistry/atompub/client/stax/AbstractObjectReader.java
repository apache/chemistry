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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.ParseException;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public abstract class AbstractObjectReader<T> extends AbstractEntryReader<T> {

    protected abstract void readProperty(ReadContext ctx, StaxReader reader,
            T object, XmlProperty p);

    protected abstract void readAllowableActions(ReadContext ctx,
            StaxReader reader, T object, Map<QName, Boolean> allowableActions);

    @Override
    protected void readCmisElement(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        if (reader.getLocalName().equals(AtomPubCMIS.OBJECT.getLocalPart())) {
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
        if (reader.getNamespaceURI().equals(CMIS.CMIS_NS)) {
            String localName = reader.getLocalName();
            if (localName.equals(CMIS.PROPERTIES.getLocalPart())) {
                readProperties(ctx, reader, object);
            } else if (localName.equals(CMIS.ALLOWABLE_ACTIONS.getLocalPart())) {
                readAllowableActions(ctx, reader, object);
            } else if (localName.equals(CMIS.CHANGE_EVENT_INFO.getLocalPart())) {
                readChangeEventInfo(ctx, reader, object);
            } else { // unknown tag
                readOtherCmisElement(ctx, reader, object);
            }
        } else {
            readEntryElement(ctx, reader, object);
        }
    }

    protected void readProperties(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        for (PropertyIterator it = new PropertyIterator(reader); it.hasNext();) {
            XmlProperty p = it.next();
            readPropertyWithType(ctx, reader, object, p);
        }
    }

    protected void readPropertyWithType(ReadContext ctx, StaxReader reader,
            T object, XmlProperty p) {
        String id = p.getId();
        PropertyDefinition def = ctx.getRepository().getPropertyDefinition(id);
        if (def == null) {
            throw new ParseException("No such property definition: " + id);
        }
        p.setDefinition(def);
        readProperty(ctx, reader, object, p);
    }

    protected void readAllowableActions(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        Map<QName, Boolean> allowableActions = new HashMap<QName, Boolean>();
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            QName qname = reader.getName();
            Boolean bool = Boolean.valueOf(reader.getElementText());
            allowableActions.put(qname, bool);
        }
        readAllowableActions(ctx, reader, object, allowableActions);
    }

    protected void readChangeEventInfo(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // TODO not yet implemented
    }

    protected void readOtherCmisElement(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // do nothing
    }

}
