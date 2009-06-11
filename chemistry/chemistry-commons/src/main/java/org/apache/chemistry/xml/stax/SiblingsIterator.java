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

import javax.xml.stream.XMLStreamException;

/**
 * An element iterator that iterates only over the sibling elements.
 */
public abstract class SiblingsIterator<T> extends ElementIterator<T> {

    protected int depth;

    public SiblingsIterator(StaxReader sr) throws XMLStreamException {
        this(sr, sr.getElementDepth());
    }

    public SiblingsIterator(StaxReader sr, int depth) {
        super(sr);
        this.depth = depth;
    }

    @Override
    protected boolean forward() throws XMLStreamException {
        return reader.fwdSibling(depth);
    }

}
