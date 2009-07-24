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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.atompub.Atom;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Feed reader that returns a generic type T built of entries of generic type E.
 */
public abstract class AbstractFeedReader<T, E> implements FeedReader<T> {

    protected EntryReader<E> entryBuilder;

    protected abstract T createFeed(StaxReader reader);

    protected abstract void addEntry(T feed, E entry);

    protected AbstractFeedReader(EntryReader<E> entryBuilder) {
        this.entryBuilder = entryBuilder;
    }

    public EntryReader<E> getEntryBuilder() {
        return entryBuilder;
    }

    public void setEntryBuilder(EntryReader<E> entryBuilder) {
        this.entryBuilder = entryBuilder;
    }

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
        StaxReader xr = StaxReader.newReader(in);
        try {
            return read(ctx, xr);
        } finally {
            xr.close();
        }
    }

    public T read(ReadContext ctx, Reader reader) throws XMLStreamException {
        StaxReader xr = StaxReader.newReader(reader);
        try {
            return read(ctx, xr);
        } finally {
            xr.close();
        }
    }

    public T read(ReadContext ctx, StaxReader reader) throws XMLStreamException {
        if (!reader.getFirstTag(Atom.ATOM_FEED)) {
            throw new XMLStreamException("Parse error: Not an atom feed");
        }
        // create a new feed object to be filled
        T feed = createFeed(reader);
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next() && !isDone(ctx, reader)) {
            String nsUri = reader.getNamespaceURI();
            if (Atom.ATOM_NS.equals(nsUri)) {
                if ("entry".equals(reader.getLocalName())) {
                    addEntry(feed, entryBuilder.read(ctx, reader));
                } else {
                    readAtomElement(ctx, reader, nsUri, feed);
                }
            } else {
                readExtensionElement(ctx, reader, nsUri, feed);
            }
        }
        return feed;
    }

    protected boolean isDone(ReadContext ctx, StaxReader reader)
            throws XMLStreamException {
        return false;
    }

    protected void readAtomElement(ReadContext ctx, StaxReader reader,
            String nsUri, T feed) throws XMLStreamException {
    }

    protected void readExtensionElement(ReadContext ctx, StaxReader reader,
            String nsUri, T feed) throws XMLStreamException {
    }

    /** entry builder */

}
