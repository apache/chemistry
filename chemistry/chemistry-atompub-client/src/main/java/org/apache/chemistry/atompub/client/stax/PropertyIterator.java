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
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.CMIS;
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
    protected XmlProperty getValue() throws XMLStreamException {
        String id = reader.getAttributeValue(CMIS.PDID);
        if (id == null) {
            throw new XMLStreamException(
                    "Parse error. Invalid CMIS property at line: "
                            + reader.getLocation().getLineNumber()
                            + ". No propertyDefinitionId specified");
        }
        ValueIterator vi = new ValueIterator(reader);
        if (!vi.hasNext()) {
            return new XmlProperty(id);
        }
        String val = vi.next();
        if (!vi.hasNext()) {
            return new XmlProperty(id, val);
        }
        List<String> vals = new ArrayList<String>();
        vals.add(val);
        do {
            vals.add(vi.next());
        } while (vi.hasNext());
        return new XmlProperty(id, vals);
    }

}
