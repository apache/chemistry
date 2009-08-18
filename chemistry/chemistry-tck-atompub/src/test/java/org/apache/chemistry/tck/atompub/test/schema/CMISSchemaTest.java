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
 *     David Caruana, Alfresco
 */
package org.apache.chemistry.tck.atompub.test.schema;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.chemistry.tck.atompub.client.CMISValidator;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test CMIS Examples against CMIS XSDs
 */
public class CMISSchemaTest extends TestCase {
    private CMISValidator cmisValidator;
    private ResourceLoader examples;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cmisValidator = new CMISValidator();
        examples = new ResourceLoader("/org/apache/chemistry/tck/atompub/examples/");
    }

    public void testAllowableActions() throws Exception {
        String xml = examples.load("AllowableActions.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testChangeLog() throws Exception {
        String xml = examples.load("ChangeLog.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testDocumentEntry() throws Exception {
        String xml = examples.load("DocumentEntry.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testDocumentEntryPWC() throws Exception {
        String xml = examples.load("DocumentEntryPWC.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testDocumentEntryWithChanges() throws Exception {
        String xml = examples.load("DocumentEntryWithChanges.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testFolderChildren() throws Exception {
        String xml = examples.load("FolderChildren.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testFolderDescendants() throws Exception {
        String xml = examples.load("FolderDescendants.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testFolderEntry() throws Exception {
        String xml = examples.load("FolderEntry.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testPolicyEntry() throws Exception {
        String xml = examples.load("PolicyEntry.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testQuery() throws Exception {
        String xml = examples.load("Query.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testRelationshipEntry() throws Exception {
        String xml = examples.load("RelationshipEntry.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testService() throws Exception {
        String xml = examples.load("Service.xml");
        assertValidXML(xml, cmisValidator.getAppValidator());
    }

    public void testTypeDocumentWith() throws Exception {
        String xml = examples.load("TypeDocumentWith.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testTypeDocumentWithout() throws Exception {
        String xml = examples.load("TypeDocumentWithout.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testTypeFolderWith() throws Exception {
        String xml = examples.load("TypeFolderWith.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testTypeFolderWithOut() throws Exception {
        String xml = examples.load("TypeFolderWithOut.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testTypeRelationshipWith() throws Exception {
        String xml = examples.load("TypeRelationshipWith.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    public void testTypeRelationshipWithOut() throws Exception {
        String xml = examples.load("TypeRelationshipWithout.xml");
        assertValidXML(xml, cmisValidator.getCMISAtomValidator());
    }

    /**
     * Assert XML is valid according to specified validator
     * 
     * @param xml
     *            document to test
     * @param validator
     *            validator to test with
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private void assertValidXML(String xml, Validator validator) throws IOException, ParserConfigurationException {
        try {
            Document document = cmisValidator.getDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {
            fail(cmisValidator.toString(e, xml));
        }
    }

}
