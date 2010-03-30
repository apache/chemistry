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
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.abdera.ext.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;

import org.apache.abdera.model.Entry;
import org.apache.chemistry.abdera.ext.CMISChoice;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition;
import org.apache.chemistry.abdera.ext.CMISTypeDefinition;
import org.apache.chemistry.abdera.ext.CMISChoice.CMISChoiceString;
import org.apache.chemistry.abdera.ext.CMISPropertyDefinition.CMISPropertyStringDefinition;
import org.apache.chemistry.abdera.ext.utils.CMISAppModel;

/**
 * 
 */

/**
 * Test for Abdera Extension functionalities. Takes a local CMIS compliant
 * atompub entries and validates the parsing of those via the CmisAppModel and
 * Abdera extensions
 * 
 * @author gabriele
 * 
 */
public class TestAppModel extends TestCase {

    CMISAppModel model = null;

    @Override
    protected void setUp() throws Exception {
        model = new CMISAppModel();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests constants for the type definition entry parsing
     */
    public void testTypeDefinitionConstants() {
        CMISTypeDefinition type = loadCustomTypeDefinition();
        assertNotNull(type);
        assertTrue(type.getCreatable());
        assertTrue(type.getFileable());
        assertTrue(type.getQueryable());
        assertTrue(type.getVersionable());
        assertTrue(type.getFullTextIndexed());
        assertTrue(type.getIncludeInSupertypeQuery());
        assertFalse(type.getControllablePolicy());
        assertTrue(type.getControllableACL());
        assertEquals("", type.getDescription());
        assertEquals("My doc", type.getDisplayName());
        assertEquals("mycm:doc", type.getQueryName());
        assertEquals("http://www.alfresco.org/model/mycontent/1.0", type.getLocalNamespace());
        assertEquals("doc", type.getLocalName());
        assertEquals("cmis:document", type.getBaseId());
        assertEquals("cmis:document", type.getParentId());
        assertEquals("D:mycm:doc", type.getId());
        assertEquals("allowed", type.getContentStreamAllowed());
    }

    /**
     * Tests constants for the property definition entry parsing
     */
    public void testPropertyDefinitionConstants() {
        CMISTypeDefinition type = loadCustomTypeDefinition();
        assertNotNull(type);
        CMISPropertyDefinition property = type.getPropertyDefinition("mycm:privacy");
        assertNotNull(property);
        assertTrue(property instanceof CMISPropertyStringDefinition);
        assertTrue(property.getQueryable());
        assertFalse(property.getOpenChoice());
        assertFalse(property.getInherited());
        assertFalse(property.getRequired());
        assertFalse(property.getOrderable());
        // Description is an optional attribute
        assertNull(property.getDescription());
        assertEquals("Privacy", property.getDisplayName());
        assertEquals("mycm:privacy", property.getQueryName());
        assertEquals("mycm:privacy", property.getId());
        assertEquals("http://www.alfresco.org/model/mycontent/1.0", property.getLocalNamespace());
        assertEquals("privacy", property.getLocalName());
        assertEquals("single", property.getCardinality());
        assertEquals("readwrite", property.getUpdatability());
        assertEquals(CMISConstants.PROP_TYPE_STRING, property.getPropertyType());
    }

    /**
     * Tests property definition choices parsing with no nested choices
     */
    public void testGetChoices() {
        CMISTypeDefinition typeDefinition = loadCustomTypeDefinition();
        assertNotNull(typeDefinition);
        CMISPropertyDefinition propertyDefinition = typeDefinition.getPropertyDefinition("mycm:privacy");
        assertNotNull(propertyDefinition);
        // Gets top level choices
        List<CMISChoice> choices = propertyDefinition.getChoices(false);
        assertNotNull(choices);
        assertEquals(choices.size(), 3);
        assertEquals("Company private", choices.get(0).getValue());
        assertEquals("Company private", choices.get(0).getDisplayName());
        assertEquals("Public", choices.get(1).getValue());
        assertEquals("Public", choices.get(1).getDisplayName());
        // Tries to get nested choices...
        List<CMISChoice> allChoices = propertyDefinition.getChoices(true);
        // ...but there are no nested choices in this entry
        assertEquals(choices.size(), allChoices.size());
    }

    /**
     * Tests property definition choices parsing with with nested choices
     */
    public void testGetNestedChoices() {
        CMISTypeDefinition typeDefinition = loadTypeDefinitionWithNestedChoices();
        assertNotNull(typeDefinition);
        CMISPropertyDefinition propertyDefinition = typeDefinition.getPropertyDefinition("mycm:privacy");
        assertNotNull(propertyDefinition);
        // Only gets top level choices
        List<CMISChoice> choices = propertyDefinition.getChoices(false);
        assertNotNull(choices);
        // Top level choices are only 3
        assertEquals(choices.size(), 3);
        CMISChoice europe = choices.get(0);
        assertNotNull(europe);
        assertTrue(europe instanceof CMISChoiceString);
        assertEquals("EU", europe.getValue());
        assertEquals("Europe", europe.getDisplayName());
        CMISChoice australia = choices.get(1);
        assertNotNull(australia);
        assertTrue(australia instanceof CMISChoiceString);
        assertEquals("AU", australia.getValue());
        assertEquals("Australia", australia.getDisplayName());
        CMISChoice america = choices.get(2);
        assertNotNull(america);
        assertTrue(america instanceof CMISChoiceString);
        assertEquals("AM", america.getValue());
        assertEquals("America", america.getDisplayName());
        // Verifies nested choices behavior
        assertNotNull(europe.getChoices());
        assertEquals(3, europe.getChoices().size());
        assertNotNull(america.getChoices());
        assertEquals(2, america.getChoices().size());
        CMISChoice northAmerica = america.getChoices().get(0);
        assertNotNull(northAmerica);
        assertEquals("NA", northAmerica.getValue());
        assertEquals("North America", northAmerica.getDisplayName());
        assertEquals(3, northAmerica.getChoices().size());
        CMISChoice southAmerica = america.getChoices().get(1);
        assertNotNull(southAmerica);
        assertEquals("SA", southAmerica.getValue());
        assertEquals("South America", southAmerica.getDisplayName());
        assertEquals(2, southAmerica.getChoices().size());
        // Gets all choices flattened directly from property definition
        choices = propertyDefinition.getChoices(true);
        assertNotNull(choices);
        // All choices node are 13 at any level of depth
        assertEquals(choices.size(), 13);
    }

    private CMISTypeDefinition loadCustomTypeDefinition() {
        String typeDefinitionEntry = null;
        try {
            typeDefinitionEntry = load("/org/apache/chemistry/abdera/ext/test/D_mycm_doc-type-definition-entry.xml");
        } catch (IOException e) {
            fail("Test atom entry not found");
        }
        Entry entry = model.parseEntry(new StringReader(typeDefinitionEntry), null);
        CMISTypeDefinition type = entry.getExtension(CMISConstants.TYPE_DEFINITION);
        return type;
    }

    private CMISTypeDefinition loadTypeDefinitionWithNestedChoices() {
        String typeDefinitionEntry = null;
        try {
            typeDefinitionEntry = load("/org/apache/chemistry/abdera/ext/test/D_mycm_doc-type-definition-nested-choices-entry.xml");
        } catch (IOException e) {
            fail("Test atom entry not found");
        }
        Entry entry = model.parseEntry(new StringReader(typeDefinitionEntry), null);
        CMISTypeDefinition type = entry.getExtension(CMISConstants.TYPE_DEFINITION);
        return type;
    }

    /**
     * Load text from file specified by class path
     * 
     * @param classPath
     *            XML file
     * @return XML
     * @throws IOException
     */
    private String load(String path) throws IOException {
        InputStream input = getClass().getResourceAsStream(path);
        if (input == null) {
            throw new IOException(path + " not found.");
        }

        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
        StringWriter writer = new StringWriter();

        try {
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            writer.flush();
        } finally {
            reader.close();
            writer.close();
        }

        return writer.toString();
    }

}
