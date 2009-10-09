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
package org.apache.chemistry.abdera.ext;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.axiom.attachments.utils.DataHandlerUtils;


/**
 * CMIS Content for the Abdera ATOM library.
 * 
 *  <xs:complexType name="cmisContentType">
 *      <xs:sequence>
 *         <xs:element name="mediatype" type="xs:string" />
 *         <xs:element name="base64" type="xs:string" />
 *         <xs:any processContents="lax" namespace="##other" minOccurs="0" maxOccurs="unbounded" />
 *      </xs:sequence>
 *  </xs:complexType>
 *
 */
public class CMISContent extends ExtensibleElementWrapper {
    
    /**
     * @param internal
     */
    public CMISContent(Element internal) {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISContent(Factory factory, QName qname) {
        super(factory, qname);
    }

    /**
     * Gets Content Media Type
     * 
     * @return content media type (or null, if not specified)
     */
    public MimeType getMediaType() {
        MimeType type = null;
        Element mediaType = getFirstChild(CMISConstants.CONTENT_MEDIATYPE);
        if (mediaType != null) {
            String mediaTypeVal = mediaType.getText();
            if (mediaTypeVal != null) {
                try {
                    type = new MimeType(mediaTypeVal);
                } catch (Exception e) {}
            }
        }
        return type;
    }

    /**
     * Gets Base64 encoded content
     * 
     * @return  content (base64 encoded)
     */
    public String getBase64() {
        String base64Str = null;
        Element base64 = getFirstChild(CMISConstants.CONTENT_BASE64);
        if (base64 != null) {
            base64Str = base64.getText();
        }
        return base64Str;
    }
    
    /**
     * Gets Content
     * 
     * @return  content (base64 decoded)
     * @throws IOException
     */
    public InputStream getContentStream() throws IOException {
        MimeType type = getMediaType();
        String base64 = getBase64();
        DataHandler dh = (DataHandler)DataHandlerUtils.getDataHandlerFromText(base64, (type != null) ? type.toString() : null);
        return dh.getInputStream();
    }
    
}
