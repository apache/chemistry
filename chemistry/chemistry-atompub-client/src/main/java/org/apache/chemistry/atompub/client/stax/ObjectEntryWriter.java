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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub.client.stax;

import java.io.IOException;
import java.util.Date;

import org.apache.chemistry.CMIS;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.xml.stax.XMLWriter;

/**
 *
 */
public abstract class ObjectEntryWriter extends
        AbstractXmlObjectWriter<ObjectEntry> {

    @Override
    public String getContentType() {
        return AtomPub.MEDIA_TYPE_ATOM_ENTRY;
    }

    @Override
    public void write(ObjectEntry object, XMLWriter xw) throws IOException {
        try {
            xw.start();
            xw.element("entry");
            xw.xmlns(AtomPub.ATOM_NS);
            xw.xmlns(CMIS.CMIS_PREFIX, CMIS.CMIS_NS);
            xw.xmlns(AtomPubCMIS.CMISRA_PREFIX, AtomPubCMIS.CMISRA_NS);
            xw.start();
            // atom requires an ID to be set even on new created entries ..
            xw.element("id").content("urn:uuid:" + object.getId());
            // TODO hardcoded title properties...
            String title = (String) object.getValue("title");
            if (title == null) {
                title = (String) object.getValue("dc:title");
            }
            if (title == null) {
                title = (String) object.getValue(Property.NAME);
            }
            xw.element("title").content(title);
            xw.element("updated").content(new Date());
            writeContent(object, xw);
            writeCmisObject(object, xw);
            xw.end();
            xw.end();
        } catch (Exception e) {
            e.printStackTrace(); // TODO
            throw new RuntimeException(e);
        } finally {
            xw.close();
        }
    }

    protected abstract void writeContent(ObjectEntry object, XMLWriter xw)
            throws IOException;

    protected abstract void writeCmisObject(ObjectEntry object, XMLWriter xw)
            throws IOException;

}
