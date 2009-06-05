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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.atompub.client.common.xml.ChildrenNavigator;
import org.apache.chemistry.atompub.client.common.xml.StaxReader;

/**
 *
 */
public abstract class AbstractEntryReader<T> implements EntryReader<T>, ATOM {

    protected abstract T createObject(ReadContext ctx);

    public T read(ReadContext ctx, File file) throws XMLStreamException,
            IOException {
        InputStream in = new FileInputStream(file);
        try {
            return read(ctx, in);
        } finally {
            in.close();
        }
    }

    public T read(ReadContext ctx, URL url) throws XMLStreamException,
            IOException {
        InputStream in = url.openStream();
        try {
            return read(ctx, in);
        } finally {
            in.close();
        }
    }

    public T read(ReadContext ctx, InputStream in) throws XMLStreamException {
        return read(ctx, StaxReader.newReader(in));
    }

    public T read(ReadContext ctx, XMLStreamReader reader)
            throws XMLStreamException {
        return read(ctx, StaxReader.newReader(reader));
    }

    public T read(ReadContext ctx, StaxReader reader) throws XMLStreamException {
        if (!reader.getFirstTag(ATOM.ENTRY)) {
            return null;
        }
        T object = createObject(ctx);
        ChildrenNavigator children = reader.getChildren();
        while (children.next()) {
            if (reader.getNamespaceURI().equals(CMIS.CMIS_NS)) {
                readCmisElement(ctx, reader, object);
            } else {
                readEntryElement(ctx, reader, object);
            }
        }
        return object;
    }

    protected void readCmisElement(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        // do nothing
    }

    protected void readEntryElement(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        if (reader.getNamespaceURI().equals(ATOM.ATOM_NS)) {
            readAtomElement(ctx, reader, object);
        } else {
            readExtensionElement(ctx, reader, object);
        }
    }

    protected void readAtomElement(ReadContext ctx, StaxReader reader, T object)
            throws XMLStreamException {
        // do nothing
    }

    protected void readExtensionElement(ReadContext ctx, StaxReader reader,
            T object) throws XMLStreamException {
        // do nothing
    }

}
