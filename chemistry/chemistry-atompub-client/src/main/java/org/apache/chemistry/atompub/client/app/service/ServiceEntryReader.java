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
package org.apache.chemistry.atompub.client.app.service;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.atompub.client.common.atom.AbstractEntryReader;
import org.apache.chemistry.atompub.client.common.atom.ReadContext;
import org.apache.chemistry.atompub.client.common.xml.ParseException;
import org.apache.chemistry.atompub.client.common.xml.StaxReader;

/**
 *
 */
public class ServiceEntryReader extends AbstractEntryReader<ServiceInfo> {

    @Override
    protected ServiceInfo createObject(ReadContext ctx) {
        return new ServiceInfo();
    }

    @Override
    protected void readAtomElement(ReadContext ctx, StaxReader reader,
            ServiceInfo object) throws XMLStreamException {
        String name = reader.getLocalName();
        if ("id".equals(name)) {
            String id = reader.getElementText();
            int p = id.lastIndexOf(':');
            if (p == -1) {
                throw new ParseException("Invalid service id. " + id);
            }
            id = id.substring(p + 1);
            object.setId(id);
        } else if ("link".equals(name)) {
            String rel = reader.getAttributeValue("rel");
            if ("edit".equals(rel)) {
                object.setHref(rel);
            }
        } else if ("title".equals(name)) {
            object.setTitle(reader.getElementText());
        } else if ("content".equals(name)) {
            object.setSummary(reader.getElementText());
        }
    }

}
