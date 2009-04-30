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
package org.apache.chemistry.property;

import java.io.Serializable;

/**
 * CMIS Object Property.
 *
 * @author Florent Guillaume
 */
public interface Property {

    /*
     * ----- Object -----
     */

    String ID = "ObjectId";

    String URI = "Uri";

    String TYPE_ID = "ObjectTypeId";

    String CREATED_BY = "CreatedBy";

    String CREATION_DATE = "CreationDate";

    String LAST_MODIFIED_BY = "LastModifiedBy";

    String LAST_MODIFICATION_DATE = "LastModificationDate";

    String CHANGE_TOKEN = "ChangeToken";

    /*
     * ----- Document -----
     */

    String NAME = "Name";

    String IS_IMMUTABLE = "IsImmutable";

    String IS_LATEST_VERSION = "IsLatestVersion";

    String IS_MAJOR_VERSION = "IsMajorVersion";

    String IS_LATEST_MAJOR_VERSION = "IsLatestMajorVersion";

    String VERSION_LABEL = "VersionLabel";

    String VERSION_SERIES_ID = "VersionSeriesId";

    String IS_VERSION_SERIES_CHECKED_OUT = "IsVersionSeriesCheckedOut";

    String VERSION_SERIES_CHECKED_OUT_BY = "VersionSeriesCheckedOutBy";

    String VERSION_SERIES_CHECKED_OUT_ID = "VersionSeriesCheckedOutId";

    String CHECKIN_COMMENT = "CheckinComment";

    String CONTENT_STREAM_ALLOWED = "ContentStreamAllowed";

    String CONTENT_STREAM_LENGTH = "ContentStreamLength";

    String CONTENT_STREAM_MIME_TYPE = "ContentStreamMimeType";

    String CONTENT_STREAM_FILENAME = "ContentStreamFilename";

    String CONTENT_STREAM_URI = "ContentStreamUri";

    /*
     * ----- Folder -----
     */

    // NAME as well
    String PARENT_ID = "ParentId";

    String ALLOWED_CHILD_OBJECT_TYPE_IDS = "AllowedChildObjectTypeIds";

    /*
     * ----- Relationship -----
     */

    String SOURCE_ID = "SourceId";

    String TARGET_ID = "TargetId";

    /*
     * ----- Policy -----
     */

    String POLICY_NAME = "PolicyName";

    String POLICY_TEXT = "PolicyText";

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
