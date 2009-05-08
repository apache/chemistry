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
package org.apache.chemistry;

import java.util.HashMap;
import java.util.Map;

/**
 * The type of a CMIS property.
 *
 * @author Florent Guillaume
 */
public class PropertyType {

    public static final int STRING_ORD = 1;

    public static final int DECIMAL_ORD = 2;

    public static final int INTEGER_ORD = 3;

    public static final int BOOLEAN_ORD = 4;

    public static final int DATETIME_ORD = 5;

    public static final int URI_ORD = 6;

    public static final int ID_ORD = 7;

    public static final int XML_ORD = 8;

    public static final int HTML_ORD = 9;

    private static final Map<String, PropertyType> all = new HashMap<String, PropertyType>();

    /**
     * A string property, represented as a {@link String}.
     */
    public static final PropertyType STRING = register(new PropertyType(
            "string", STRING_ORD));

    /**
     * A decimal property, represented as a {@link java.math.BigDecimal
     * BigDecimal}.
     */
    public static final PropertyType DECIMAL = register(new PropertyType(
            "decimal", DECIMAL_ORD));

    /**
     * An integer property, represented as a {@link Integer}.
     */
    public static final PropertyType INTEGER = register(new PropertyType(
            "integer", INTEGER_ORD));

    /**
     * A boolean property, represented as a {@link Boolean}.
     */
    public static final PropertyType BOOLEAN = register(new PropertyType(
            "boolean", BOOLEAN_ORD));

    /**
     * A date-time property, represented as a {@link java.util.Calendar
     * Calendar}.
     */
    public static final PropertyType DATETIME = register(new PropertyType(
            "datetime", DATETIME_ORD));

    /**
     * A URI property, represented as a {@link java.net.URI URI}.
     */
    public static final PropertyType URI = register(new PropertyType("uri",
            URI_ORD));

    /**
     * An ID property, represented as a {@link String}.
     */
    public static final PropertyType ID = register(new PropertyType("id",
            ID_ORD));

    /**
     * An XML property, represented as a String.
     */
    public static final PropertyType XML = register(new PropertyType("xml",
            XML_ORD));

    /**
     * An HTML property, represented as a String.
     */
    public static final PropertyType HTML = register(new PropertyType("html",
            HTML_ORD));

    private final String name;

    private final int ordinal;

    /**
     * Protected constructor. The {@link #register} static method should be used
     * by implementors to register new property types or subclasses of it.
     */
    protected PropertyType(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    /**
     * Registers a new property type. To be used only by implementors.
     */
    protected static synchronized <T extends PropertyType> T register(
            T propertyType) {
        String n = propertyType.name();
        int o = propertyType.ordinal();
        for (PropertyType pt : all.values()) {
            if (n.equals(pt.name())) {
                throw new IllegalArgumentException("Name " + n
                        + " already registered for " + pt);
            }
            if (o == pt.ordinal()) {
                throw new IllegalArgumentException("Ordinal " + o
                        + " already registered for " + pt);
            }
        }
        all.put(n, propertyType);
        return propertyType;
    }

    /**
     * Gets the registered property type for a given name.
     *
     * @param name the name
     * @return the property type, or {@code null} if not found
     */
    public static synchronized PropertyType get(String name) {
        return all.get(name);
    }

    /**
     * The name associated to this property type.
     */
    public String name() {
        return name;
    }

    /**
     * The ordinal associated to this property type. This is a facility to allow
     * easier switch statements.
     */
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + name + ')';
    }
}
