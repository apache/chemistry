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
package org.apache.chemistry;

import java.util.Map;

/**
 * A rendition is an alternative representation for a document or folder.
 * <p>
 * Renditions could be image previews, thumbnails, format-converted versions,
 * etc.
 * <p>
 * The server is responsible for determining the number, type and availability
 * of renditions present for a given document or folder. A rendition may not be
 * immediately available after document creation checkin. Renditions are
 * specific to the version of the document and may differ between document
 * versions.
 */
public interface Rendition {

    /**
     * The rendition filter specifying all renditions.
     */
    String FILTER_ALL = "*";

    /**
     * The rendition filter specifying no rendition.
     */
    String FILTER_NONE = "cmis:none";

    /**
     * The thumbnail rendition kind.
     */
    String KIND_THUMBNAIL = "cmis:thumbnail";

    /**
     * The object ID of the base document or folder that this rendition is
     * about.
     */
    ObjectId getObjectId();

    /**
     * The rendition content stream ID.
     * <p>
     * The content stream ID has meaning only in the context of the rendition's
     * base document or folder. It may be passed to {@link SPI#getContentStream}
     * to retrieve the rendition stream.
     */
    String getContentStreamId();

    /**
     * The rendition document object ID (optional).
     * <p>
     * This is only available if this rendition is represented as a document by
     * the repository, otherwise it is {@code null}.
     */
    ObjectId getRenditionDocumentId();

    /**
     * The rendition stream MIME type.
     */
    String getMimeType();

    /**
     * The rendition stream length.
     */
    long getLength();

    /**
     * The rendition title, or {@code null}.
     */
    String getTitle();

    /**
     * The rendition kind.
     * <p>
     * The only predefined value is {@link #KIND_THUMBNAIL}.
     */
    String getKind();

    /**
     * The height, or -1 if not specified.
     */
    long getHeight();

    /**
     * The width, or -1 if not specified.
     */
    long getWidth();

    /**
     * The rendition metadata.
     * <p>
     * If specified, height and width are also in this map (represented as
     * strings).
     */
    Map<String, String> getMetadata();

}
