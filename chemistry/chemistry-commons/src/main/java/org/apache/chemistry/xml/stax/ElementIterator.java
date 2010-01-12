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
package org.apache.chemistry.xml.stax;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;

/**
 * An iterator over the XML elements in the stream that creates objects each
 * time {@link #next} method is called.
 */
public abstract class ElementIterator<T> implements Iterator<T> {

    protected final StaxReader reader;

    /** If null, then the state is not known. */
    protected Boolean hasNext;

    public ElementIterator(StaxReader sr) {
        this.reader = sr;
    }

    /** Gets the value from the element in the reader. */
    protected abstract T getValue() throws XMLStreamException;

    protected boolean forward() throws XMLStreamException {
        return reader.fwd();
    }

    protected boolean accept() {
        return true;
    }

    public boolean hasNext() {
        if (hasNext == null) {
            try {
                while (forward()) {
                    if (accept()) {
                        hasNext = Boolean.TRUE;
                        return true;
                    }
                }
            } catch (Exception e) {
                throw new ParseException(e);
            }
            hasNext = Boolean.FALSE;
            return false;
        }
        return hasNext.booleanValue();
    }

    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in stream");
        }
        hasNext = null; // value will be consumed by getValue()
        try {
            return getValue();
        } catch (XMLStreamException e) {
            throw new ParseException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }

}
