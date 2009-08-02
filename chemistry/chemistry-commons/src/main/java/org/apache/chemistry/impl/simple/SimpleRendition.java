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

import java.util.Map;

import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Rendition;

public class SimpleRendition implements Rendition {

    protected final ObjectId objectId;

    protected final String contentStreamId;

    protected final ObjectId renditionDocumentId;

    protected final String mimeType;

    protected final long length;

    protected final String title;

    protected final String kind;

    protected final long height;

    protected final long width;

    protected final Map<String, String> metadata;

    public SimpleRendition(ObjectId objectId, String contentStreamId,
            ObjectId renditionDocumentId, String mimeType, long length,
            String title, String kind, long height, long width,
            Map<String, String> metadata) {
        this.objectId = objectId;
        this.contentStreamId = contentStreamId;
        this.renditionDocumentId = renditionDocumentId;
        this.mimeType = mimeType;
        this.length = length;
        this.title = title;
        this.kind = kind;
        this.height = height;
        this.width = width;
        this.metadata = metadata;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public String getContentStreamId() {
        return contentStreamId;
    }

    public ObjectId getRenditionDocumentId() {
        return renditionDocumentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public String getKind() {
        return kind;
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

}
