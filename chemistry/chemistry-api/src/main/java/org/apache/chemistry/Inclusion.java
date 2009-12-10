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

/**
 * Additional information to include in a response.
 * <p>
 * <b>Note</b>: Not all methods taking an {@link Inclusion} parameter will use
 * all inclusion parameters, consult each method's documentation for details.
 */
public class Inclusion {

    /**
     * The property filter specifying all properties.
     * <p>
     * This is a convenience value, but implementations are required to use
     * {@link String#equals} for comparison, not {@code =}.
     */
    public static final String ALL_PROPERTIES = "*";

    /**
     * Filter indicating which properties must be returned.
     * <p>
     * This is either {@code "*"} ({@link Inclusion#ALL_PROPERTIES}) for all
     * properties, or a comma-separated list of property query names. The
     * {@code null} value is equivalent to {@code "*"}.
     */
    public final String properties;

    /**
     * Filter indicating which renditions must be returned.
     * <p>
     * This is either {@code "*"} ({@link Rendition#FILTER_ALL}), {@code
     * "cmis:none"} ({@link Rendition#FILTER_NONE}), or a comma-separated list
     * of either kinds of MIME types (which may have a subtype of {@code "*"}).
     * The {@code null} value is equivalent to {@code "cmis:none"}.
     */
    public final String renditions;

    /**
     * Which relationships to include.
     * <p>
     * Additional relationships may also be returned for each returned object,
     * according to the value of the parameter. If {@code null}, no
     * relationships are returned.
     */
    public final RelationshipDirection relationships;

    /**
     * Whether to include allowable actions.
     */
    public final boolean allowableActions;

    /**
     * Whether to include policies.
     */
    public final boolean policies;

    /**
     * Whether to include acls.
     */
    public final boolean acls;

    /**
     * Constructs inclusion information with the given parameters.
     *
     * @param properties the properties filter, or {@code null} for all
     *            properties
     * @param renditions the renditions filter, or {@code null} for no
     *            renditions
     * @param relationships the relationships to include, or {@code null} for no
     *            relationships
     * @param allowableActions {@code true} to include allowable actions
     * @param policies {@code true} to include policies
     * @param acls {@code true} to include ACLs
     */
    public Inclusion(String properties, String renditions,
            RelationshipDirection relationships, boolean allowableActions,
            boolean policies, boolean acls) {
        this.properties = properties;
        this.renditions = renditions;
        this.relationships = relationships;
        this.allowableActions = allowableActions;
        this.policies = policies;
        this.acls = acls;
    }

}
