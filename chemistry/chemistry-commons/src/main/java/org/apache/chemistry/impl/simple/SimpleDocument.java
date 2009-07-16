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
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.impl.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Property;

public class SimpleDocument extends SimpleObject implements Document {

    public SimpleDocument(SimpleObjectEntry entry) {
        super(entry);
    }

    protected byte[] getContentBytes() {
        return (byte[]) entry.data.get(SimpleProperty.CONTENT_BYTES_KEY);
    }

    public Document checkOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void cancelCheckOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document checkIn(boolean major, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document getLatestVersion(boolean major) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Document> getAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void deleteAllVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public InputStream getStream() {
        byte[] contentBytes = getContentBytes();
        if (contentBytes == null) {
            return null;
        }
        return new ByteArrayInputStream(contentBytes);
    }

    public ContentStream getContentStream() {
        byte[] contentBytes = getContentBytes();
        if (contentBytes == null) {
            return null;
        }
        // length is recomputed, no need to read it
        String mimeType = getString(Property.CONTENT_STREAM_MIME_TYPE);
        String filename = getString(Property.CONTENT_STREAM_FILENAME);
        URI uri = getURI(Property.CONTENT_STREAM_URI);
        return new SimpleContentStream(contentBytes, mimeType, filename, uri);
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {
        ContentStreamPresence csa = getType().getContentStreamAllowed();
        if (csa == ContentStreamPresence.NOT_ALLOWED && contentStream != null) {
            throw new IllegalStateException("Content stream not allowed"); // TODO
        } else if (csa == ContentStreamPresence.REQUIRED
                && contentStream == null) {
            throw new IllegalStateException("Content stream required"); // TODO
        }
        if (contentStream == null) {
            entry.setValue(Property.CONTENT_STREAM_LENGTH, null);
            entry.setValue(Property.CONTENT_STREAM_MIME_TYPE, null);
            entry.setValue(Property.CONTENT_STREAM_FILENAME, null);
            entry.setValue(Property.CONTENT_STREAM_URI, null);
            entry.setValue(SimpleProperty.CONTENT_BYTES_KEY, null);
        } else {
            entry.setValue(Property.CONTENT_STREAM_LENGTH,
                    Integer.valueOf((int) contentStream.getLength())); // TODO
                                                                       // Long
            entry.setValue(Property.CONTENT_STREAM_MIME_TYPE,
                    contentStream.getMimeType());
            entry.setValue(Property.CONTENT_STREAM_FILENAME,
                    contentStream.getFilename());
            entry.setValue(Property.CONTENT_STREAM_URI, contentStream.getURI());
            entry.setValue(SimpleProperty.CONTENT_BYTES_KEY,
                    SimpleContentStream.getBytes(contentStream.getStream()));
        }
    }

}
