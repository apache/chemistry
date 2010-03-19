/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.chemistry.jcr;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.ContentStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

/**
 * Implementation of a content stream.
 */
class JcrContentStream implements ContentStream {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(JcrContentStream.class);

    /**
     * Content node of type <b>nt:resource</b>.
     */
    private final Node content;

    /**
     * Filename.
     */
    private final String filename;

    /**
     * Create a new instance of this class.
     *
     * @param content content node
     * @param filename content stream file name
     */
    public JcrContentStream(Node content, String filename) {
        this.content = content;
        this.filename = filename;
    }

    /**
     * {@inheritDoc}
     */
    public String getFileName() {
        return filename;
    }

    /**
     * {@inheritDoc}
     */
    public long getLength() {
        try {
            javax.jcr.Property data = content.getProperty(JcrConstants.JCR_DATA);
            return data.getLength();
        } catch (RepositoryException e) {
            String msg = "Unable to get length.";
            log.error(msg, e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public String getMimeType() {
        try {
            javax.jcr.Property mimetype = content.getProperty(JcrConstants.JCR_MIMETYPE);
            return mimetype.getString();
        } catch (RepositoryException e) {
            String msg = "Unable to get mime type.";
            log.error(msg, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getStream() {
        try {
            javax.jcr.Property data = content.getProperty(JcrConstants.JCR_DATA);
            return data.getBinary().getStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get stream.";
            log.error(msg, e);
        }
        return null;
    }

}
