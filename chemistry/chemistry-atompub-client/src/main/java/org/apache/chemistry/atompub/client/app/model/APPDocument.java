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
package org.apache.chemistry.atompub.client.app.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Type;

/**
 *
 */
public class APPDocument extends APPObject implements Document {

    public APPDocument(APPObjectEntry entry, Type type) {
        super(entry, type);
    }

    public ContentStream getContentStream() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public InputStream getStream() throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void cancelCheckOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkIn(boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Document> getAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document getLatestVersion(boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
