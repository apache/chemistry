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
 *     Emanuele Lombardi
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.client;

import java.io.IOException;
import java.util.Collection;

import org.apache.chemistry.CMISRuntimeException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.NameConstraintViolationException;
import org.apache.chemistry.Type;

/**
 *
 */
public class APPDocument extends APPObject implements Document {

    public APPDocument(APPObjectEntry entry, Type type) {
        super(entry, type);
    }

    public ContentStream getContentStream() throws IOException {
        return getContentStream(null);
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {
        entry.setContentStream(contentStream);
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

    public Document copy(Folder folder) throws NameConstraintViolationException {
        // TODO implement copy "by hand" or using extensions when available
        throw new CMISRuntimeException("AtomPub bindings do not support copy");
    }

}
