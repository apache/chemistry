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
package org.apache.chemistry.atompub.client.app;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.app.model.APPObjectEntryReader;
import org.apache.chemistry.atompub.client.app.model.APPObjectEntryWriter;
import org.apache.chemistry.atompub.client.app.model.APPObjectFeedReader;
import org.apache.chemistry.atompub.client.app.model.APPServiceDocumentReader;
import org.apache.chemistry.atompub.client.app.model.TypeEntryReader;
import org.apache.chemistry.atompub.client.app.model.TypeFeedReader;
import org.apache.chemistry.atompub.client.common.atom.EntryReader;
import org.apache.chemistry.atompub.client.common.atom.FeedReader;
import org.apache.chemistry.atompub.client.common.atom.ServiceDocumentReader;
import org.apache.chemistry.atompub.client.common.atom.XmlObjectWriter;

/**
 *
 */
public class DefaultIOProvider implements IOProvider {

    protected APPObjectEntryReader objectReader = new APPObjectEntryReader();

    protected APPObjectFeedReader objectFeedReader = new APPObjectFeedReader(
            objectReader);

    protected TypeEntryReader typeReader = new TypeEntryReader();

    protected TypeFeedReader typeFeedReader = new TypeFeedReader(typeReader);

    protected APPServiceDocumentReader serviceDocumentReader = new APPServiceDocumentReader();

    protected APPObjectEntryWriter objectWriter = new APPObjectEntryWriter();

    public EntryReader<? extends ObjectEntry> getObjectEntryReader() {
        return objectReader;
    }

    public FeedReader<List<ObjectEntry>> getObjectFeedReader() {
        return objectFeedReader;
    }

    public ServiceDocumentReader<?> getServiceDocumentReader() {
        return serviceDocumentReader;
    }

    public FeedReader<Map<String, Type>> getTypeFeedReader() {
        return typeFeedReader;
    }

    public EntryReader<? extends Type> getTypeEntryReader() {
        return typeReader;
    }

    public XmlObjectWriter<ObjectEntry> getObjectEntryWriter() {
        return objectWriter;
    }

    public XmlObjectWriter<String> getQueryWriter() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
