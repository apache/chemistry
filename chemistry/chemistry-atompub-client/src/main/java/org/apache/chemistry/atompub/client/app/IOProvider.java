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
import org.apache.chemistry.atompub.client.common.atom.EntryReader;
import org.apache.chemistry.atompub.client.common.atom.FeedReader;
import org.apache.chemistry.atompub.client.common.atom.ServiceDocumentReader;
import org.apache.chemistry.atompub.client.common.atom.XmlObjectWriter;

/**
 *
 */
public interface IOProvider {

    EntryReader<? extends ObjectEntry> getObjectEntryReader();

    EntryReader<? extends Type> getTypeEntryReader();

    ServiceDocumentReader<?> getServiceDocumentReader();

    FeedReader<List<ObjectEntry>> getObjectFeedReader();

    FeedReader<Map<String, Type>> getTypeFeedReader();

    XmlObjectWriter<ObjectEntry> getObjectEntryWriter();

    XmlObjectWriter<String> getQueryWriter();

}
