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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.Tree;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.client.APPContext;
import org.apache.chemistry.atompub.client.APPObjectFeedTreeReader;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.ParseException;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 *
 */
public abstract class AbstractObjectReader<T> extends AbstractEntryReader<T> {

    protected abstract void setProperty(T object, XmlProperty p);

    protected abstract void setAllowableActions(T object,
            Set<QName> allowableActions);

    protected abstract void setPathSegment(T object, String pathSegment);

    // TODO better use of generics
    protected abstract void setChildren(T object, List<Tree<ObjectEntry>> tree);

    @Override
    protected void readCmisElement(APPContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        QName name = reader.getName();
        if (AtomPubCMIS.OBJECT.equals(name)) {
            readCmisObject(ctx, reader, object);
        } else if (AtomPubCMIS.PATH_SEGMENT.equals(name)) {
            readPathSegment(ctx, reader, object);
        } else if (AtomPubCMIS.RELATIVE_PATH_SEGMENT.equals(name)) {
            // stored in the same property as PATH_SEGMENT
            readPathSegment(ctx, reader, object);
        } else if (AtomPubCMIS.CHILDREN.equals(name)) {
            readChildren(ctx, reader, object);
        }
    }

    protected void readCmisObject(APPContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            readObjectChildElement(ctx, reader, object);
        }
    }

    protected void readObjectChildElement(APPContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        if (reader.getNamespaceURI().equals(CMIS.CMIS_NS)) {
            QName name = reader.getName();
            if (CMIS.PROPERTIES.equals(name)) {
                readProperties(ctx, reader, object);
            } else if (CMIS.ALLOWABLE_ACTIONS.equals(name)) {
                readAllowableActions(ctx, reader, object);
            } else if (CMIS.CHANGE_EVENT_INFO.equals(name)) {
                readChangeEventInfo(ctx, reader, object);
            } else { // unknown tag
                readOtherCmisElement(ctx, reader, object);
            }
        } else {
            readEntryElement(ctx, reader, object);
        }
    }

    protected void readProperties(APPContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        for (PropertyIterator it = new PropertyIterator(reader); it.hasNext();) {
            XmlProperty p = it.next();
            readPropertyWithType(ctx, reader, object, p);
        }
    }

    protected void readPropertyWithType(APPContext ctx, StaxReader reader,
            T object, XmlProperty p) {
        String id = p.getId();
        PropertyDefinition def = ctx.getRepository().getPropertyDefinition(id);
        if (def == null) {
            throw new ParseException("No such property definition: " + id);
        }
        p.setDefinition(def);
        setProperty(object, p);
    }

    protected void readAllowableActions(APPContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        Set<QName> allowableActions = new HashSet<QName>();
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            if (Boolean.parseBoolean(reader.getElementText())) {
                allowableActions.add(reader.getName());
            }
        }
        setAllowableActions(object, allowableActions);
    }

    protected void readChangeEventInfo(APPContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // TODO not yet implemented
    }

    protected void readOtherCmisElement(APPContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // do nothing
    }

    protected void readPathSegment(APPContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        setPathSegment(object, reader.getElementText());
    }

    protected void readChildren(APPContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        // TODO better use of generics
        List<Tree<ObjectEntry>> list = new APPObjectFeedTreeReader().read(ctx,
                reader);
        setChildren(object, list);
    }

}
