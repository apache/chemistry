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
package org.apache.chemistry.atompub.client.common.atom;

import org.apache.chemistry.atompub.client.common.xml.StaxReader;

/**
 *
 */
public class DefaultEntryReader extends AbstractObjectReader<XmlObject> {

    @Override
    protected XmlObject createObject(ReadContext ctx) {
        return new XmlObject(ctx.getConnection());
    }

    @Override
    protected void readProperty(ReadContext ctx, StaxReader reader,
            XmlObject object, XmlProperty p) {
        object.properties.put(p.getName(), p.getValue());
    }

}
