/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.impl.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.type.ContentStreamPresence;

public class SimpleDocument extends SimpleObject implements Document {

    public SimpleDocument(SimpleData data, SimpleConnection connection) {
        super(data, connection);
    }

    protected static final String CONTENT_BYTES_KEY = "__content__";

    protected byte[] getContentBytes() {
        return (byte[]) data.get(CONTENT_BYTES_KEY);
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
        try {
            return new SimpleContentStream(contentBytes, mimeType, filename,
                    uri);
        } catch (IOException e) {
            // cannot happen, reading from ByteArrayInputStream
            return null;
        }
    }

    public void setContentStream(ContentStream contentStream)
            throws IOException {
        ContentStreamPresence csa = getType().getContentStreamAllowed();
        if (csa == ContentStreamPresence.NOT_ALLOWED && contentStream != null) {
            throw new RuntimeException("Content stream not allowed"); // TODO
        } else if (csa == ContentStreamPresence.REQUIRED
                && contentStream == null) {
            throw new RuntimeException("Content stream required"); // TODO
        }
        if (contentStream == null) {
            _setValue(Property.CONTENT_STREAM_LENGTH, null);
            _setValue(Property.CONTENT_STREAM_MIME_TYPE, null);
            _setValue(Property.CONTENT_STREAM_FILENAME, null);
            _setValue(Property.CONTENT_STREAM_URI, null);
            data.remove(CONTENT_BYTES_KEY);
        } else {
            _setValue(Property.CONTENT_STREAM_LENGTH,
                    Integer.valueOf((int) contentStream.getLength())); // cast?
            _setValue(Property.CONTENT_STREAM_MIME_TYPE,
                    contentStream.getMimeType());
            _setValue(Property.CONTENT_STREAM_FILENAME,
                    contentStream.getFilename());
            _setValue(Property.CONTENT_STREAM_URI, contentStream.getURI());
            data.put(CONTENT_BYTES_KEY,
                    SimpleContentStream.getBytes(contentStream.getStream()));
        }
    }

}
