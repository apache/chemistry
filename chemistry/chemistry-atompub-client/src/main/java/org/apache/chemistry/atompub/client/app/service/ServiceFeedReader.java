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
package org.apache.chemistry.atompub.client.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.atompub.client.common.atom.AbstractFeedReader;
import org.apache.chemistry.atompub.client.common.xml.StaxReader;

/**
 *
 */
public class ServiceFeedReader extends
        AbstractFeedReader<Map<String, ServiceInfo>, ServiceInfo> {

    private final static ServiceFeedReader builder = new ServiceFeedReader();

    public static ServiceFeedReader getBuilder() {
        return builder;
    }

    public ServiceFeedReader() {
        super(new ServiceEntryReader());
    }

    @Override
    protected void addEntry(Map<String, ServiceInfo> feed, ServiceInfo entry) {
        feed.put(entry.id, entry);
    }

    @Override
    protected Map<String, ServiceInfo> createFeed(StaxReader reader) {
        return new HashMap<String, ServiceInfo>();
    }

}
