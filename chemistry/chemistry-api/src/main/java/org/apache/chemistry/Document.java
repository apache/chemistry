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

/**
 * A CMIS Document.
 *
 * @author Florent Guillaume
 */
public interface Document extends CMISObject {

    /**
     * The folder in which the document is filed.
     * <p>
     * If the document is unfiled, {@code null} is returned. If the document is
     * filed in multiple folders, an exception is raised.
     * <p>
     * This is a convenience method for the common case where documents are not
     * multi-filed.
     *
     * @return the parent folder, or {@code null}.
     */
    Folder getParent();

    /**
     * Gets the byte stream for this document.
     *
     * @return the byte stream
     */
    InputStream getStream() throws IOException;

    /**
     * Gets the content stream for this document.
     *
     * @return the content stream
     */
    ContentStream getContentStream();

    /**
     * Sets the content stream for this document.
     *
     * @param contentStream
     * @throws IOException if the stream could not be read
     */
    void setContentStream(ContentStream contentStream) throws IOException;

}
