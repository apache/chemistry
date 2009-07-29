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

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.chemistry.ContentStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.JcrConstants;

public class JcrContentStream implements ContentStream {

    private static final Log log = LogFactory.getLog(JcrFolder.class);

    private final Node content;

    public JcrContentStream(Node content) {
        this.content = content;
    }

    public String getFilename() {
        try {
            return content.getName();
        } catch (RepositoryException e) {
            String msg = "Unable to get node name.";
            log.error(msg, e);
        }
        return null;
    }

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

    public InputStream getStream() throws IOException {
        try {
            javax.jcr.Property data = content.getProperty(JcrConstants.JCR_DATA);
            return data.getStream();
        } catch (RepositoryException e) {
            String msg = "Unable to get stream.";
            log.error(msg, e);
        }
        return null;
    }

}
