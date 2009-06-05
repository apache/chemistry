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
package org.apache.chemistry.atompub.client.common.xml;

import javax.xml.stream.XMLStreamReader;

/**
 *
 */
public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ParseException(XMLStreamReader reader, String message) {
        this("Parse Error at " + reader.getLocation().getLineNumber() + ":"
                + reader.getLocation().getColumnNumber() + ". " + message);
    }

    public ParseException(XMLStreamReader reader, Throwable cause) {
        this("Parse Error at " + reader.getLocation().getLineNumber() + ":"
                + reader.getLocation().getColumnNumber(), cause);
    }

    public ParseException(XMLStreamReader reader) {
        this(reader, "");
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

}
