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

/**
 * The type of a CMIS property.
 *
 * @author Florent Guillaume
 */
public enum PropertyType {

    /**
     * A string property, represented as a {@link String}.
     */
    STRING("string"),

    /**
     * A decimal property, represented as a {@link java.math.BigDecimal
     * BigDecimal}.
     */
    DECIMAL("decimal"),

    /**
     * An integer property, represented as a {@link Integer}.
     */
    INTEGER("integer"),

    /**
     * A boolean property, represented as a {@link Boolean}.
     */
    BOOLEAN("boolean"),

    /**
     * A date-time property, represented as a {@link java.util.Calendar
     * Calendar}.
     */
    DATETIME("datetime"),

    /**
     * A URI property, represented as a {@link java.net.URI URI}.
     */
    URI("uri"),

    /**
     * An ID property, represented as a {@link String}.
     */
    ID("id"),

    /**
     * An XML property, represented as a String.
     */
    XML("xml"),

    /**
     * An HTML property, represented as a String.
     */
    HTML("html");

    private final String value;

    private PropertyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
