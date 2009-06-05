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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.chemistry.atompub.client.common.xml.XMLWriter;

/**
 *
 */
public abstract class AbstractXmlObjectWriter<T> implements XmlObjectWriter<T> {

    public abstract String getContentType();

    public void write(T object, File file) throws IOException {
        FileWriter w = new FileWriter(file);
        try {
            write(object, w);
        } finally {
            w.close();
        }
    }

    public void write(T object, OutputStream out) throws IOException {
        write(object, new XMLWriter(new OutputStreamWriter(out)));
    }

    public void write(T object, Writer writer) throws IOException {
        write(object, new XMLWriter(writer));
    }

    public abstract void write(T object, XMLWriter writer) throws IOException;

}
