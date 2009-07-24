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

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Feed reader that returns a generic type T.
 */
public interface FeedReader<T> {

    T read(ReadContext ctx, InputStream in) throws XMLStreamException;

    T read(ReadContext ctx, Reader reader) throws XMLStreamException;

    T read(ReadContext ctx, StaxReader reader) throws XMLStreamException;

}
