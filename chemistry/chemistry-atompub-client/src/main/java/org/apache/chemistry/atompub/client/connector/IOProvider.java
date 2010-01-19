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
 *     Ugo Cei, Sourcesense
 */
package org.apache.chemistry.atompub.client.connector;

import org.apache.chemistry.Inclusion;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;
import org.apache.chemistry.Type;
import org.apache.chemistry.TypeManager;
import org.apache.chemistry.atompub.client.stax.EntryReader;
import org.apache.chemistry.atompub.client.stax.FeedReader;
import org.apache.chemistry.atompub.client.stax.ServiceDocumentReader;
import org.apache.chemistry.atompub.client.stax.XmlObjectWriter;

/**
 * This abstracts the operations used to read and write objects from XML
 * streams.
 */
public interface IOProvider {

    EntryReader<? extends ObjectEntry> getObjectEntryReader();

    EntryReader<? extends Type> getTypeEntryReader(
            boolean includePropertyDefinitions);

    ServiceDocumentReader<?> getServiceDocumentReader();

    FeedReader<ListPage<ObjectEntry>> getObjectFeedReader();

    FeedReader<TypeManager> getTypeFeedReader(boolean includePropertyDefinitions);

    XmlObjectWriter<ObjectEntry> getObjectEntryWriter();

    XmlObjectWriter<String> getQueryWriter(boolean searchAllVersions,
            Inclusion inclusion, Paging paging);

}
