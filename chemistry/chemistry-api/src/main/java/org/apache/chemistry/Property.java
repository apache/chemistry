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

import java.io.Serializable;

/**
 * CMIS Object Property.
 */
public interface Property {

    /*
     * ----- Object -----
     */

    String ID = "cmis:objectId";

    String TYPE_ID = "cmis:objectTypeId";

    String BASE_TYPE_ID = "cmis:baseTypeId";

    String NAME = "cmis:name";

    String CREATED_BY = "cmis:createdBy";

    String CREATION_DATE = "cmis:creationDate";

    String LAST_MODIFIED_BY = "cmis:lastModifiedBy";

    String LAST_MODIFICATION_DATE = "cmis:lastModificationDate";

    String CHANGE_TOKEN = "cmis:changeToken";

    /*
     * ----- Document -----
     */

    String IS_IMMUTABLE = "cmis:isImmutable";

    String IS_LATEST_VERSION = "cmis:isLatestVersion";

    String IS_MAJOR_VERSION = "cmis:isMajorVersion";

    String IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";

    String VERSION_LABEL = "cmis:versionLabel";

    String VERSION_SERIES_ID = "cmis:versionSeriesId";

    String IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";

    String VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";

    String VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";

    String CHECK_IN_COMMENT = "cmis:checkinComment";

    String CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";

    String CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";

    String CONTENT_STREAM_FILE_NAME = "cmis:contentStreamFileName";

    String CONTENT_STREAM_ID = "cmis:contentStreamId";

    /*
     * ----- Folder -----
     */

    String PARENT_ID = "cmis:parentId";

    String PATH = "cmis:path";

    String ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";

    /*
     * ----- Relationship -----
     */

    String SOURCE_ID = "cmis:sourceId";

    String TARGET_ID = "cmis:targetId";

    /*
     * ----- Policy -----
     */

    String POLICY_TEXT = "cmis:policyText";

    /**
     * The property definition.
     */
    PropertyDefinition getDefinition();

    /**
     * Gets the property value.
     */
    Serializable getValue();

    /**
     * Sets the property value.
     */
    // for connection-tied live objects
    void setValue(Serializable value);

}
