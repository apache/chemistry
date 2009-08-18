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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.chemistry.abdera.ext.CMISExtensionFactory;


/**
 * Create / Read CMIS AtomPub Service, Feeds and Entries
 */
public class CMISAppModel {
    
    private Abdera abdera;
    private Parser parser;
    private Factory factory;

    public CMISAppModel() {
        // construct Abdera Service
        abdera = new Abdera();
        factory = abdera.getFactory();
        factory.registerExtension(new CMISExtensionFactory());
        parser = factory.newParser();
    }

    public Entry createEntry() {
        return factory.newEntry();
    }

    public Feed createFeed() {
        return factory.newFeed();
    }

    public Element parse(InputStream doc, String base) {
        Reader inputReader = new InputStreamReader(doc);
        return parse(inputReader, base);
    }

    public Element parse(Reader doc, String base) {
        Document<Element> entryDoc;
        if (base != null && base.length() > 0) {
            entryDoc = parser.parse(doc, base);
        } else {
            entryDoc = parser.parse(doc);
        }

        Element root = entryDoc.getRoot();
        return root;
    }

    public Service parseService(InputStream doc, String base) {
        Reader inputReader = new InputStreamReader(doc);
        return parseService(inputReader, base);
    }

    public Service parseService(Reader doc, String base) {
        Element root = parse(doc, base);
        if (!Service.class.isAssignableFrom(root.getClass())) {
            throw new RuntimeException(HttpServletResponse.SC_BAD_REQUEST + "Expected APP Service, but recieved " + root.getClass());
        }

        return (Service) root;
    }

    public Entry parseEntry(InputStream doc, String base) {
        Reader inputReader = new InputStreamReader(doc);
        return parseEntry(inputReader, base);
    }

    public Entry parseEntry(Reader doc, String base) {
        Element root = parse(doc, base);
        if (!Entry.class.isAssignableFrom(root.getClass())) {
            throw new RuntimeException(HttpServletResponse.SC_BAD_REQUEST + "Expected Atom Entry, but recieved " + root.getClass());
        }

        return (Entry) root;
    }

    public Feed parseFeed(InputStream doc, String base) {
        Reader inputReader = new InputStreamReader(doc);
        return parseFeed(inputReader, base);
    }

    public Feed parseFeed(Reader doc, String base) {
        Element root = parse(doc, base);
        if (!Feed.class.isAssignableFrom(root.getClass())) {
            throw new RuntimeException(HttpServletResponse.SC_BAD_REQUEST + "Expected Atom Feed, but recieved " + root.getClass());
        }

        return (Feed) root;
    }

}
