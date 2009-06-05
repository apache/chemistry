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
package org.apache.chemistry.atompub.client.app.model;

import java.io.IOException;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.atompub.client.common.atom.ObjectEntryWriter;
import org.apache.chemistry.atompub.client.common.xml.XMLWriter;

/**
 *
 */
public class APPObjectEntryWriter extends ObjectEntryWriter {

    @Override
    protected void writeCmisObject(ObjectEntry object, XMLWriter xw)
            throws IOException {
        ((APPObjectEntry) object).writeObjectTo(xw);
    }

}
