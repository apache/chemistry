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
 *
 */
public class ChildrenNavigator {

    protected StaxReader sr;

    protected int depth;

    public ChildrenNavigator(StaxReader sr) throws XMLStreamException {
        this.sr = sr;
        int tok = sr.getEventType();
        if (tok == StaxReader.END_ELEMENT) {
            depth = sr.depth + 1;
        } else if (tok == StaxReader.START_ELEMENT) {
            depth = sr.depth;
        } else {
            throw new XMLStreamException(
                    "Ilegal state: current event must be START_ELEMENT or END_ELEMENT");
        }
    }

    public ChildrenNavigator(StaxReader sr, int depth) {
        this.sr = sr;
        this.depth = depth;
    }

    public boolean next() throws XMLStreamException {
        return sr.fwdSibling(depth);
    }

}
