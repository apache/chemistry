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
package org.apache.chemistry.tck.atompub.client;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * CMIS Validator
 * 
 * Support for validating CMIS requests/responses against CMIS XSDs
 */
public class CMISValidator {
    
    /** XML Schema Validation */
    private DocumentBuilder documentBuilder = null;
    private Validator appValidator = null;
    private Validator atomValidator = null;

    /**
     * Gets document parser
     * 
     * @return document parser
     * @throws ParserConfigurationException
     */
    public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (documentBuilder == null) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            documentBuilder = builderFactory.newDocumentBuilder();
        }
        return documentBuilder;
    }

    /**
     * Gets CMIS Atom Publishing Protocol XML Validator
     * 
     * @return APP Validator
     * @throws IOException
     * @throws SAXException
     */
    public Validator getAppValidator() throws IOException, SAXException {
        if (appValidator == null) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source APPFile = new StreamSource(getClass().getResourceAsStream(
                    "/org/apache/chemistry/tck/atompub/xsd/APP.xsd"), getClass().getResource(
                    "/org/apache/chemistry/tck/atompub/xsd/APP.xsd").toExternalForm());
            Schema schema = factory.newSchema(APPFile);
            appValidator = schema.newValidator();
        }
        return appValidator;
    }

    /**
     * Gets CMIS Atom Validator
     * 
     * @return CMIS Atom Validator
     * @throws IOException
     * @throws SAXException
     */
    public Validator getCMISAtomValidator() throws IOException, SAXException {
        if (atomValidator == null) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source ATOMFile = new StreamSource(getClass().getResourceAsStream(
                    "/org/apache/chemistry/tck/atompub/xsd/ATOM.xsd"), getClass().getResource(
                    "/org/apache/chemistry/tck/atompub/xsd/ATOM.xsd").toExternalForm());
            Schema schema = factory.newSchema(ATOMFile);
            atomValidator = schema.newValidator();
        }

        return atomValidator;
    }

    /**
     * Asserts XML complies with specified Validator
     * 
     * @param xml
     *            xml to assert
     * @param validator
     *            validator to assert with
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void validateXML(String xml, Validator validator) throws IOException, ParserConfigurationException,
            SAXException {
        Document document = getDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        validator.validate(new DOMSource(document));
    }

    /**
     * Convert SAX Exception to String
     * 
     * @param e
     *            SAX Exception
     * @param xml
     *            related XML (if any)
     * @return description of SAX Exception
     */
    public String toString(SAXException e, String xml) {
        StringBuffer fail = new StringBuffer(e.toString());
        if (e instanceof SAXParseException) {
            fail.append("\n");
            fail.append("line: ").append(((SAXParseException) e).getLineNumber()).append("\n");
            fail.append("col: ").append(((SAXParseException) e).getColumnNumber()).append("\n");
        }
        if (xml != null) {
            fail.append("\n");
            fail.append(xml);
        }
        return fail.toString();
    }

}
