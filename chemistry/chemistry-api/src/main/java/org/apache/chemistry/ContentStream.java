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
package org.apache.chemistry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * A CMIS Content Stream.
 */
public interface ContentStream {

    /**
     * The content stream length.
     */
    long getLength();

    /**
     * The content stream MIME type.
     */
    String getMimeType();

    /**
     * The content stream file name, or {@code null} if none is provided.
     */
    String getFilename();

    /**
     * The content stream URI.
     */
    URI getURI();

    /**
     * The actual byte stream for this content stream.
     *
     * @throws IOException
     */
    InputStream getStream() throws IOException;

}
