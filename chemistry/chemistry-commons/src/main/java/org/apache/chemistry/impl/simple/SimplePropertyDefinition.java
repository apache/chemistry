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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.Choice;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Updatability;

public class SimplePropertyDefinition implements PropertyDefinition {

    private final String name;

    private final String id;

    private final String displayName;

    private final String description;

    private final boolean inherited;

    private final PropertyType type;

    private final boolean multiValued;

    private final List<Choice> choices;

    private final boolean openChoice;

    private final boolean required;

    private final Serializable defaultValue;

    private final Updatability updatability;

    private final boolean queryable;

    private final boolean orderable;

    private final int precision;

    private final Number minValue;

    private final Number maxValue;

    private final int maxLength;

    private final URI schemaURI;

    public SimplePropertyDefinition(String name, String id, String displayName,
            String description, boolean inherited, PropertyType type,
            boolean multiValued, List<Choice> choices, boolean openChoice,
            boolean required, Serializable defaultValue,
            Updatability updatability, boolean queryable, boolean orderable,
            int precision, Integer minValue, Integer maxValue, int maxLength,
            URI schemaURI) {
        super();
        if (name.equals(SimpleProperty.CONTENT_BYTES_KEY)) {
            throw new IllegalArgumentException(SimpleProperty.CONTENT_BYTES_KEY
                    + " is a reserved name");
        }
        this.name = name;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.inherited = inherited;
        this.type = type;
        this.multiValued = multiValued;
        this.choices = choices == null ? null
                : Collections.unmodifiableList(choices);
        this.openChoice = openChoice;
        this.required = required;
        this.defaultValue = defaultValue;
        this.updatability = updatability;
        this.queryable = queryable;
        this.orderable = orderable;
        this.precision = precision;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.maxLength = maxLength;
        this.schemaURI = schemaURI;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInherited() {
        return inherited;
    }

    public PropertyType getType() {
        return type;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public boolean isOpenChoice() {
        return openChoice;
    }

    public boolean isRequired() {
        return required;
    }

    public Serializable getDefaultValue() {
        return defaultValue;
    }

    public Updatability getUpdatability() {
        return updatability;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public int getPrecision() {
        return precision;
    }

    public Number getMinValue() {
        return minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public URI getSchemaURI() {
        return schemaURI;
    }

    public boolean validates(Serializable value) {
        return validationError(value) == null;
    }

    public String validationError(Serializable value) {
        if (getUpdatability() == Updatability.READ_ONLY) {
            // TODO Updatability.WHEN_CHECKED_OUT
            return "Property is read-only";
        }
        if (value == null) {
            if (isRequired()) {
                return "Property is required";
            }
            return null;
        }
        boolean multi = isMultiValued();
        if (multi != value.getClass().isArray()) {
            return multi ? "Property is multi-valued"
                    : "Property is single-valued";
        }
        Class<?> klass = type.klass();
        if (klass == null) {
            throw new UnsupportedOperationException(type.toString());
        }
        if (multi) {
            for (int i = 0; i < Array.getLength(value); i++) {
                Object v = Array.get(value, i);
                if (v == null) {
                    return "Array value cannot contain null elements";
                }
                if (!klass.isInstance(v)) {
                    return "Array value has type " + v.getClass()
                            + " instead of " + klass.getName();
                }
            }
        } else {
            if (!klass.isInstance(value)) {
                return "Value has type " + value.getClass() + " instead of "
                        + klass.getName();
            }
        }
        return null;
    }

}
