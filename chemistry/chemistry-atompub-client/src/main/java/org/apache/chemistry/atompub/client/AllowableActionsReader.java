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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.atompub.client.stax.EntryReader;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Reader for AllowableActions objects.
 */
public class AllowableActionsReader implements EntryReader<Set<QName>> {

    private static final String AA_PREFIX = "can";

    public Set<QName> read(APPContext ctx, InputStream in)
            throws XMLStreamException {
        return read(ctx, StaxReader.newReader(in));
    }

    public Set<QName> read(APPContext ctx, XMLStreamReader reader)
            throws XMLStreamException {
        return read(ctx, StaxReader.newReader(reader));
    }

    public Set<QName> read(APPContext ctx, StaxReader reader)
            throws XMLStreamException {
        if (!reader.getFirstTag(CMIS.ALLOWABLE_ACTIONS)) {
            return null;
        }
        Set<QName> set = new HashSet<QName>();
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            if (reader.getNamespaceURI().equals(CMIS.CMIS_NS)
                    || reader.getLocalName().startsWith(AA_PREFIX)) {
                boolean bool = Boolean.parseBoolean(reader.getElementText());
                if (bool) {
                    set.add(reader.getName());
                }
            }
        }
        return set;
    }

}
