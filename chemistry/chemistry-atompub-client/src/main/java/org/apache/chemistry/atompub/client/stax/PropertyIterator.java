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
package org.apache.chemistry.atompub.client.stax;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.xml.stax.ChildrenIterator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Iterate over property elements in the current CMIS object on the stream.
 * <p>
 * Each iteration produces a {@link XmlProperty} object. The returned object is
 * not yet ready to use since it has a null property definition. The property
 * definition cannot be known before the object type is detected. This means the
 * caller is responsible for filling in the property definition as soon as it
 * detects the object type. To be able to choose the right property definition
 * the caller must know the name of the property. This name will be passed
 * through the {@link XmlProperty#value} member. The caller must reset this
 * member to {@link XmlProperty#NULL} after the property definition has been set
 * on the property.
 */
public class PropertyIterator extends ChildrenIterator<XmlProperty> {

    public PropertyIterator(StaxReader sr) throws XMLStreamException {
        super(sr);
    }

    @Override
    protected boolean accept() {
        return reader.getNamespaceURI().equals(CMIS.CMIS_NS)
                && reader.getLocalName().startsWith("property");
    }

    @Override
    protected XmlProperty createValue() throws XMLStreamException {
        String key = reader.getAttributeValue(CMIS.NAME);
        if (key == null) {
            throw new XMLStreamException(
                    "Parse error. Invalid CMIS property at line: "
                            + reader.getLocation().getLineNumber()
                            + ". No name specified");
        }
        ValueIterator vi = new ValueIterator(reader);
        XmlProperty xp = new XmlProperty();
        xp.value = key; // use value to temporary store the key
        if (!vi.hasNext()) {
            return xp;
        }
        String val = vi.next();
        if (!vi.hasNext()) {
            xp.xmlValue = val;
            return xp;
        }
        ArrayList<String> vals = new ArrayList<String>();
        vals.add(val);
        do {
            val = vi.next();
            vals.add(val);
        } while (vi.hasNext());
        xp.xmlValue = vals;
        return xp;
    }

}
