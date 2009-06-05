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
package org.apache.chemistry.atompub.client.app.httpclient;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.chemistry.atompub.client.common.atom.XmlObjectWriter;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 *
 */
public class ObjectRequestEntity<T> implements RequestEntity {

    protected XmlObjectWriter<T> writer;

    protected T obj;

    public ObjectRequestEntity(XmlObjectWriter<T> writer, T obj) {
        this.writer = writer;
        this.obj = obj;
    }

    public long getContentLength() {
        return -1;
    }

    public String getContentType() {
        return writer.getContentType();
    }

    public boolean isRepeatable() {
        return false;
    }

    public void writeRequest(OutputStream out) throws IOException {
        writer.write(obj, out);
    }

}
