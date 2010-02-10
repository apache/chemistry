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
 *     Michael Durig, Day
 */
package org.apache.chemistry.xml.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

/**
 *
 */
public class StaxReader extends StreamReaderDelegate {

    protected static final XMLInputFactory factory = XMLInputFactory.newInstance();
    static {
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        // network detached mode
        // copied form apache axiom
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                Boolean.FALSE);
        // Some StAX parser such as Woodstox still try to load the external DTD
        // subset,
        // even if IS_SUPPORTING_EXTERNAL_ENTITIES is set to false. To work
        // around this,
        // we add a custom XMLResolver that returns empty documents. See
        // WSTX-117 for
        // an interesting discussion about this.
        factory.setXMLResolver(new XMLResolver() {
            public Object resolveEntity(String publicID, String systemID,
                    String baseURI, String namespace) throws XMLStreamException {
                return new ByteArrayInputStream(new byte[0]);
            }
        });
    }

    public static final XMLInputFactory getFactory() {
        return factory;
    }

    public static StaxReader newReader(InputStream in)
            throws XMLStreamException {
        return new StaxReader(factory.createXMLStreamReader(in));
    }

    public static StaxReader newReader(Reader reader) throws XMLStreamException {
        return new StaxReader(factory.createXMLStreamReader(reader));
    }

    public static StaxReader newReader(XMLStreamReader reader)
            throws XMLStreamException {
        return new StaxReader(reader);
    }

    public StaxReader(XMLStreamReader reader) {
        super(reader);
    }

    protected int depth;

    protected String defNsUri;

    public int getDepth() {
        return depth;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        depth--;
        return super.getElementText();
    }

    public final int getElementDepth() throws XMLStreamException {
        return getEventType() == END_ELEMENT ? depth + 1 : depth;
    }

    public final int getChildrenDepth() throws XMLStreamException {
        return getElementDepth() + 1;
    }

    public boolean fwd() throws XMLStreamException {
        if (!hasNext()) {
            return false;
        }
        int tok = next();
        if (tok == START_ELEMENT) {
            depth++;
        } else if (tok == END_ELEMENT) {
            depth--;
        }
        return true;
    }

    public boolean fwdTag() throws XMLStreamException {
        // we need to test first hasNext to be sure we fwd in the stream
        // this way we are sure we didn't end in the same element (without
        // forwarding the stream)
        while (hasNext() && fwd()) {
            if (getEventType() == START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    public boolean fwdTag(String localName) throws XMLStreamException {
        // we need to test first hasNext to be sure we fwd in the stream
        // this way we are sure we didn't end in the same element (without
        // forwarding the stream)
        while (hasNext() && fwd()) {
            if (getEventType() == START_ELEMENT
                    && localName.equals(getLocalName())) {
                return true;
            }
        }
        return false;
    }

    public boolean fwdTag(String nsUri, String localName)
            throws XMLStreamException {
        // we need to test first hasNext to be sure we fwd in the stream
        // this way we are sure we didn't end in the same element (without
        // forwarding the stream)
        while (hasNext() && fwd()) {
            if (getEventType() == START_ELEMENT
                    && localName.equals(getLocalName())
                    && nsUri.equals(getNamespaceURI())) {
                return true;
            }
        }
        return false;
    }

    public boolean getFirstTag(QName name) throws XMLStreamException {
        if (getEventType() == START_ELEMENT && getName().equals(name)) {
            return true;
        }
        return fwdTag(name);
    }

    public boolean getFirstTag(String localName) throws XMLStreamException {
        if (getEventType() == START_ELEMENT && getLocalName().equals(localName)) {
            return true;
        }
        return fwdTag(localName);
    }

    public boolean getFirstTag(String nsUri, String localName)
            throws XMLStreamException {
        if (getEventType() == START_ELEMENT && getLocalName().equals(localName)
                && getNamespaceURI().equals(nsUri)) {
            return true;
        }
        return fwdTag(nsUri, localName);
    }

    public boolean fwdTag(QName name) throws XMLStreamException {
        // we need to test first hasNext to be sure we fwd in the stream
        // this way we are sure we didn't end in the same element (without
        // forwarding the stream)
        while (hasNext() && fwd()) {
            if (getEventType() == START_ELEMENT && name.equals(getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean fwdSibling() throws XMLStreamException {
        int tok = getEventType();
        if (tok == END_ELEMENT) {
            return fwdSibling(depth + 1);
        } else if (tok == START_ELEMENT) {
            return fwdSibling(depth);
        } else {
            throw new XMLStreamException(
                    "Ilegal state: current event must be START_ELEMENT or END_ELEMENT");
        }
    }

    public boolean fwdSibling(int cdepth) throws XMLStreamException {
        // we need to test first hasNext to be sure we fwd in the stream
        // this way we are sure we didn't end in the same element (without
        // forwarding the stream)
        while (hasNext() && fwd()) {
            if (depth < cdepth - 1) {
                return false;
            } else if (depth > cdepth) {
                continue;
            } else if (getEventType() == START_ELEMENT) { // on same level
                if (depth == cdepth) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean fwdSibling(String localName) throws XMLStreamException {
        while (fwdSibling()) {
            if (localName.equals(getLocalName())) {
                return true;
            }
        }
        return false;
    }

    public boolean fwdSibling(QName name) throws XMLStreamException {
        while (fwdSibling()) {
            if (name.equals(getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean fwdSibling(String nsUri, String localName)
            throws XMLStreamException {
        while (fwdSibling()) {
            if (localName.equals(getLocalName())
                    && nsUri.equals(getNamespaceURI())) {
                return true;
            }
        }
        return false;
    }

    public ChildrenNavigator getChildren() throws XMLStreamException {
        return new ChildrenNavigator(this, depth + 1);
    }

    public ChildrenNavigator getChildren(final String localName)
            throws XMLStreamException {
        return new FilteredChildrenNavigator(this, depth + 1) {
            @Override
            protected boolean accept() {
                return localName.equals(getLocalName());
            }
        };
    }

    public ChildrenNavigator getChildren(final String nsUri,
            final String localName) throws XMLStreamException {
        return new FilteredChildrenNavigator(this, depth + 1) {
            @Override
            protected boolean accept() {
                return localName.equals(getLocalName())
                        && nsUri.equals(getNamespaceURI());
            }
        };
    }

    public ChildrenNavigator getChildren(final QName name)
            throws XMLStreamException {
        return new FilteredChildrenNavigator(this, depth + 1) {
            @Override
            protected boolean accept() {
                return name.equals(getName());
            }
        };
    }

    public String getAttributeValue(String localName) {
        int cnt = getAttributeCount();
        for (int i = 0; i < cnt; i++) {
            if (localName.equals(getAttributeName(i).getLocalPart())) {
                return getAttributeValue(i);
            }
        }
        return null;
    }

    public String getDefaultNamespaceURI() {
        if (defNsUri == null) {
            defNsUri = getNamespaceURI("");
            if (defNsUri == null) {
                defNsUri = "";
            }
        }
        return defNsUri;
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        String val;
        if (namespaceURI == null || "".equals(namespaceURI)) {
            val = getAttributeValue(localName);
        } else {
            val = super.getAttributeValue(namespaceURI, localName);
            if (val == null && namespaceURI.equals(getDefaultNamespaceURI())) {
                // try without namespace
                val = getAttributeValue(localName);
            }
        }
        return val;
    }

    public String getAttributeValue(QName name) {
        return getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
    }

}
