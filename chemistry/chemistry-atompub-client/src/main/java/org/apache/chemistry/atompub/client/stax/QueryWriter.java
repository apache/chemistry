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
package org.apache.chemistry.atompub.client.stax;

import java.io.IOException;

import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.xml.stax.XMLWriter;

/**
 * Writer for the body of a CMIS query.
 */
public class QueryWriter extends AbstractXmlObjectWriter<String> {

    @Override
    public String getContentType() {
        return "application/cmisquery+xml";
    }

    @Override
    public void write(String statement, XMLWriter xw) throws IOException {
        xw.start().putXmlns(CMIS.CMIS_PREFIX, CMIS.CMIS_NS);
        xw.element(CMIS.QUERY);
        xw.start();
        xw.element(CMIS.STATEMENT).econtent(statement);
        xw.element(CMIS.SEARCH_ALL_VERSIONS).content("false");
        xw.element(CMIS.PAGE_SIZE).content("0");
        xw.element(CMIS.SKIP_COUNT).content("0");
        xw.end();
        xw.end();
    }

}
