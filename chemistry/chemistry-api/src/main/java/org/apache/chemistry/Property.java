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

    String ID = "cmis:ObjectId";

    String TYPE_ID = "cmis:ObjectTypeId";

    String BASE_TYPE_ID = "cmis:BaseTypeId";

    String NAME = "cmis:Name";

    String CREATED_BY = "cmis:CreatedBy";

    String CREATION_DATE = "cmis:CreationDate";

    String LAST_MODIFIED_BY = "cmis:LastModifiedBy";

    String LAST_MODIFICATION_DATE = "cmis:LastModificationDate";

    String CHANGE_TOKEN = "cmis:ChangeToken";

    /*
     * ----- Document -----
     */

    String IS_IMMUTABLE = "cmis:IsImmutable";

    String IS_LATEST_VERSION = "cmis:IsLatestVersion";

    String IS_MAJOR_VERSION = "cmis:IsMajorVersion";

    String IS_LATEST_MAJOR_VERSION = "cmis:IsLatestMajorVersion";

    String VERSION_LABEL = "cmis:VersionLabel";

    String VERSION_SERIES_ID = "cmis:VersionSeriesId";

    String IS_VERSION_SERIES_CHECKED_OUT = "cmis:IsVersionSeriesCheckedOut";

    String VERSION_SERIES_CHECKED_OUT_BY = "cmis:VersionSeriesCheckedOutBy";

    String VERSION_SERIES_CHECKED_OUT_ID = "cmis:VersionSeriesCheckedOutId";

    String CHECK_IN_COMMENT = "cmis:CheckinComment"; // TODO-0.63

    String CONTENT_STREAM_LENGTH = "cmis:ContentStreamLength";

    String CONTENT_STREAM_MIME_TYPE = "cmis:ContentStreamMimeType";

    String CONTENT_STREAM_FILE_NAME = "cmis:ContentStreamFileName";

    String CONTENT_STREAM_ID = "cmis:ContentStreamId";

    /*
     * ----- Folder -----
     */

    String PARENT_ID = "cmis:ParentId";

    String ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:AllowedChildObjectTypeIds";

    /*
     * ----- Relationship -----
     */

    String SOURCE_ID = "cmis:SourceId";

    String TARGET_ID = "cmis:TargetId";

    /*
     * ----- Policy -----
     */

    String POLICY_TEXT = "cmis:PolicyText";

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
