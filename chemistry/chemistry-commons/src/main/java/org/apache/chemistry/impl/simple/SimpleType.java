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
package org.apache.chemistry.impl.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Type;
import org.apache.chemistry.Updatability;

public class SimpleType implements Type {

    public static final SimplePropertyDefinition PROP_ID = new SimplePropertyDefinition(
            Property.ID, "def:id", "Id", "", false, PropertyType.ID, false,
            null, false, true, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_URI = new SimplePropertyDefinition(
            Property.URI, "def:uri", "URI", "", false, PropertyType.URI, false,
            null, false, false, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_TYPE_ID = new SimplePropertyDefinition(
            Property.TYPE_ID, "def:typeid", "Type ID", "", false,
            PropertyType.ID, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CREATED_BY = new SimplePropertyDefinition(
            Property.CREATED_BY, "def:createdby", "Created By", "", false,
            PropertyType.STRING, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CREATION_DATE = new SimplePropertyDefinition(
            Property.CREATION_DATE, "def:creationdate", "Creation Date", "",
            false, PropertyType.DATETIME, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_LAST_MODIFIED_BY = new SimplePropertyDefinition(
            Property.LAST_MODIFIED_BY, "def:lastmodifiedby",
            "Last Modified By", "", false, PropertyType.STRING, false, null,
            false, true, null, Updatability.READ_ONLY, true, true, 0, null,
            null, -1, null, null);

    public static final SimplePropertyDefinition PROP_LAST_MODIFICATION_DATE = new SimplePropertyDefinition(
            Property.LAST_MODIFICATION_DATE, "def:lastmodificationdate",
            "Last Modification Date", "", false, PropertyType.DATETIME, false,
            null, false, true, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CHANGE_TOKEN = new SimplePropertyDefinition(
            Property.CHANGE_TOKEN, "def:changetoken", "Change Token", "",
            false, PropertyType.STRING, false, null, false, false, null,
            Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_NAME = new SimplePropertyDefinition(
            Property.NAME, "def:name", "Name", "", false, PropertyType.STRING,
            false, null, false, true, null, Updatability.READ_WRITE, true,
            true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_IS_LATEST_VERSION = new SimplePropertyDefinition(
            Property.IS_LATEST_VERSION, "def:islatestversion",
            "Is Latest Version", "", false, PropertyType.BOOLEAN, false, null,
            false, true, null, Updatability.READ_ONLY, true, true, 0, null,
            null, -1, null, null);

    public static final SimplePropertyDefinition PROP_IS_MAJOR_VERSION = new SimplePropertyDefinition(
            Property.IS_MAJOR_VERSION, "def:ismajorversion",
            "Is Major Version", "", false, PropertyType.BOOLEAN, false, null,
            false, false, null, Updatability.READ_ONLY, true, true, 0, null,
            null, -1, null, null);

    public static final SimplePropertyDefinition PROP_IS_LATEST_MAJOR_VERSION = new SimplePropertyDefinition(
            Property.IS_LATEST_MAJOR_VERSION, "def:islatestmajorversion",
            "Is Latest Major Version", "", false, PropertyType.BOOLEAN, false,
            null, false, true, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_VERSION_LABEL = new SimplePropertyDefinition(
            Property.VERSION_LABEL, "def:versionlabel", "Version Label", "",
            false, PropertyType.STRING, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_VERSION_SERIES_ID = new SimplePropertyDefinition(
            Property.VERSION_SERIES_ID, "def:versionseriesid",
            "Version Series ID", "", false, PropertyType.ID, false, null,
            false, true, null, Updatability.READ_ONLY, true, true, 0, null,
            null, -1, null, null);

    public static final SimplePropertyDefinition PROP_IS_VERSION_SERIES_CHECKED_OUT = new SimplePropertyDefinition(
            Property.IS_VERSION_SERIES_CHECKED_OUT,
            "def:isversionseriescheckedout", "Is Version Series Checked Out",
            "", false, PropertyType.BOOLEAN, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_VERSION_SERIES_CHECKED_OUT_BY = new SimplePropertyDefinition(
            Property.VERSION_SERIES_CHECKED_OUT_BY,
            "def:versionseriescheckedoutby", "Version Series Checked Out By",
            "", false, PropertyType.STRING, false, null, false, false, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_VERSION_SERIES_CHECKED_OUT_ID = new SimplePropertyDefinition(
            Property.VERSION_SERIES_CHECKED_OUT_ID,
            "def:versionseriescheckedoutid", "Version Series Checked Out Id",
            "", false, PropertyType.ID, false, null, false, false, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CHECKIN_COMMENT = new SimplePropertyDefinition(
            Property.CHECKIN_COMMENT, "def:checkincomment", "Checkin Comment",
            "", false, PropertyType.STRING, false, null, false, false, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CONTENT_STREAM_LENGTH = new SimplePropertyDefinition(
            Property.CONTENT_STREAM_LENGTH, "def:contentstreamlength",
            "Content Stream Length", "", false, PropertyType.INTEGER, false,
            null, false, false, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CONTENT_STREAM_MIME_TYPE = new SimplePropertyDefinition(
            Property.CONTENT_STREAM_MIME_TYPE, "def:contentstreammimetype",
            "Content Stream MIME Type", "", false, PropertyType.STRING, false,
            null, false, false, null, Updatability.READ_ONLY, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CONTENT_STREAM_FILENAME = new SimplePropertyDefinition(
            Property.CONTENT_STREAM_FILENAME, "def:contentstreamfilename",
            "Content Stream Filename", "", false, PropertyType.STRING, false,
            null, false, false, null, Updatability.READ_WRITE, true, true, 0,
            null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_CONTENT_STREAM_URI = new SimplePropertyDefinition(
            Property.CONTENT_STREAM_URI, "def:contentstreamuri",
            "Content Stream URI", "", false, PropertyType.URI, false, null,
            false, false, null, Updatability.READ_ONLY, true, true, 0, null,
            null, -1, null, null);

    public static final SimplePropertyDefinition PROP_PARENT_ID = new SimplePropertyDefinition(
            Property.PARENT_ID, "def:parentid", "Parent Id", "", false,
            PropertyType.ID, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS = new SimplePropertyDefinition(
            Property.ALLOWED_CHILD_OBJECT_TYPE_IDS,
            "def:allowedchildobjecttypeids", "Allowed Child Object Type Ids",
            "", false, PropertyType.ID, true, null, false, false, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_SOURCE_ID = new SimplePropertyDefinition(
            Property.SOURCE_ID, "def:sourceid", "Source Id", "", false,
            PropertyType.ID, false, null, false, true, null,
            Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_TARGET_ID = new SimplePropertyDefinition(
            Property.TARGET_ID, "def:targetid", "Target Id", "", false,
            PropertyType.ID, false, null, false, true, null,
            Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_POLICY_NAME = new SimplePropertyDefinition(
            Property.POLICY_NAME, "def:policyname", "Policy Name", "", false,
            PropertyType.STRING, false, null, false, true, null,
            Updatability.READ_ONLY, true, true, 0, null, null, -1, null, null);

    public static final SimplePropertyDefinition PROP_POLICY_TEXT = new SimplePropertyDefinition(
            Property.POLICY_TEXT, "def:policytext", "Policy Text", "", false,
            PropertyType.STRING, false, null, false, true, null,
            Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);

    private static final PropertyDefinition[] PROPS_COMMON = {
            PROP_ID, //
            PROP_URI, //
            PROP_TYPE_ID, //
            PROP_CREATED_BY, //
            PROP_CREATION_DATE, //
            PROP_LAST_MODIFIED_BY, //
            PROP_LAST_MODIFICATION_DATE, //
            PROP_CHANGE_TOKEN };

    private static List<PropertyDefinition> commonPlus(
            PropertyDefinition... array) {
        List<PropertyDefinition> list = new ArrayList<PropertyDefinition>(
                PROPS_COMMON.length + array.length);
        list.addAll(Arrays.asList(PROPS_COMMON));
        list.addAll(Arrays.asList(array));
        return Collections.unmodifiableList(list);
    }

    public static final List<PropertyDefinition> PROPS_DOCUMENT_BASE = commonPlus(
            PROP_NAME, //
            PROP_IS_LATEST_VERSION, //
            PROP_IS_MAJOR_VERSION, //
            PROP_IS_LATEST_MAJOR_VERSION, //
            PROP_VERSION_LABEL, //
            PROP_VERSION_SERIES_ID, //
            PROP_IS_VERSION_SERIES_CHECKED_OUT, //
            PROP_VERSION_SERIES_CHECKED_OUT_BY, //
            PROP_VERSION_SERIES_CHECKED_OUT_ID, //
            PROP_CHECKIN_COMMENT, //
            PROP_CONTENT_STREAM_LENGTH, //
            PROP_CONTENT_STREAM_MIME_TYPE, //
            PROP_CONTENT_STREAM_FILENAME, //
            PROP_CONTENT_STREAM_URI);

    public static final List<PropertyDefinition> PROPS_FOLDER_BASE = commonPlus(
            PROP_NAME, //
            PROP_PARENT_ID, //
            PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS);

    public static final List<PropertyDefinition> PROPS_RELATIONSHIP_BASE = commonPlus(
            PROP_SOURCE_ID, //
            PROP_TARGET_ID);

    public static final List<PropertyDefinition> PROPS_POLICY_BASE = commonPlus(
            PROP_POLICY_NAME, //
            PROP_POLICY_TEXT);

    private final String id;

    private final String parentId;

    private final String queryName;

    private final String displayName;

    private final BaseType baseType;

    private final String description;

    private final boolean creatable;

    private final boolean queryable;

    private final boolean controllable;

    private final boolean includedInSuperTypeQuery;

    private final boolean fileable;

    private final boolean versionable;

    private final ContentStreamPresence contentStreamAllowed;

    private final String[] allowedSourceTypes;

    private final String[] allowedTargetTypes;

    private final Map<String, PropertyDefinition> propertyDefinitions;

    public SimpleType(String id, String parentId, String queryName,
            String displayName, BaseType baseType, String description,
            boolean creatable, boolean queryable, boolean controllable,
            boolean includedInSuperTypeQuery, boolean fileable,
            boolean versionable, ContentStreamPresence contentStreamAllowed,
            String[] allowedSourceTypes, String[] allowedTargetTypes,
            Collection<PropertyDefinition> propertyDefinitions) {
        this(id, parentId, queryName, displayName, baseType, description,
                creatable, queryable, controllable, includedInSuperTypeQuery,
                fileable, versionable, contentStreamAllowed,
                allowedSourceTypes, allowedTargetTypes);
        addPropertyDefinitions(getBasePropertyDefinitions(baseType));
        addPropertyDefinitions(propertyDefinitions);
    }

    public SimpleType(String id, String parentId, String queryName,
            String displayName, BaseType baseType, String description,
            boolean creatable, boolean queryable, boolean controllable,
            boolean includedInSuperTypeQuery, boolean fileable,
            boolean versionable, ContentStreamPresence contentStreamAllowed,
            String[] allowedSourceTypes, String[] allowedTargetTypes) {
        this.id = id;
        this.parentId = parentId;
        this.queryName = queryName;
        this.displayName = displayName;
        this.baseType = baseType;
        this.description = description;
        this.creatable = creatable;
        this.queryable = queryable;
        this.controllable = controllable;
        this.includedInSuperTypeQuery = includedInSuperTypeQuery;
        this.fileable = fileable;
        this.versionable = versionable;
        this.contentStreamAllowed = contentStreamAllowed;
        this.allowedSourceTypes = allowedSourceTypes;
        this.allowedTargetTypes = allowedTargetTypes;
        propertyDefinitions = new HashMap<String, PropertyDefinition>();
    }

    protected void addPropertyDefinitions(Collection<PropertyDefinition> defs) {
        for (PropertyDefinition def : defs) {
            String name = def.getName();
            if (propertyDefinitions.containsKey(name)) {
                throw new RuntimeException(
                        "Property already defined for name: " + name);
            }
            propertyDefinitions.put(name, def);
        }
    }

    protected static List<PropertyDefinition> getBasePropertyDefinitions(
            BaseType baseType) {
        switch (baseType) {
        case DOCUMENT:
            return PROPS_DOCUMENT_BASE;
        case FOLDER:
            return PROPS_FOLDER_BASE;
        case RELATIONSHIP:
            return PROPS_RELATIONSHIP_BASE;
        case POLICY:
            return PROPS_POLICY_BASE;
        default:
            throw new AssertionError();
        }
    }

    public String getId() {
        return id;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParentId() {
        return parentId;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public String getBaseTypeQueryName() {
        switch (baseType) {
        case DOCUMENT:
            return "Document";
        case FOLDER:
            return "Folder";
        case POLICY:
            return "Policy";
        case RELATIONSHIP:
            return "Relationship";
        }
        throw new UnsupportedOperationException(baseType.toString());
    }

    public String getDescription() {
        return description;
    }

    public boolean isCreatable() {
        return creatable;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public boolean isControllable() {
        return controllable;
    }

    public boolean isIncludedInSuperTypeQuery() {
        return includedInSuperTypeQuery;
    }

    public boolean isFileable() {
        return fileable;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public ContentStreamPresence getContentStreamAllowed() {
        return contentStreamAllowed;
    }

    public String[] getAllowedSourceTypes() {
        return allowedSourceTypes;
    }

    public String[] getAllowedTargetTypes() {
        return allowedTargetTypes;
    }

    public Collection<PropertyDefinition> getPropertyDefinitions() {
        return Collections.unmodifiableCollection(propertyDefinitions.values());
    }

    public PropertyDefinition getPropertyDefinition(String name) {
        return propertyDefinitions.get(name);
    }

}
