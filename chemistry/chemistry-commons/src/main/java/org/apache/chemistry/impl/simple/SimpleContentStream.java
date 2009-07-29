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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.chemistry.ContentStream;

/**
 * Simple {@link ContentStream} storing the stream in memory.
 */
public class SimpleContentStream implements ContentStream {

    protected final String mimeType;

    protected final String filename;

    protected final byte[] bytes;

    protected final long length;

    public SimpleContentStream(byte[] bytes, String mimeType, String filename) {
        this.mimeType = mimeType;
        this.filename = filename;
        this.bytes = bytes;
        length = bytes.length;
    }

    public SimpleContentStream(InputStream stream, String mimeType,
            String filename) throws IOException {
        this.mimeType = mimeType;
        this.filename = filename;
        bytes = getBytes(stream);
        length = bytes.length;
    }

    protected static byte[] getBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = stream.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    public long getLength() {
        return length;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream(bytes);
    }

}
