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

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.Entry;


/**
 * CMIS Children for the Abdera ATOM library.
 * 
 * Encapsulates access to nested children..
 */
public class CMISChildren extends ElementWrapper /* implements Feed */ {
    
    public CMISChildren(Element internal) {
        super(internal);
    }

    public CMISChildren(Factory factory) {
        super(factory, CMISConstants.CHILDREN);
    }

    /**
     * Gets count of child entries
     * 
     * @return
     */
    public int size() {
        return getEntries().size();
    }

    /**
     * Gets all entries of child feed
     * 
     * @return
     */
    public List<Entry> getEntries() {
        List<Element> elements = getElements();
        List<Entry> entries = new ArrayList<Entry>(elements.size());
        for (Element element : elements) {
            if (element instanceof Entry) {
                entries.add((Entry) element);
            }
        }
        return entries;
    }

    /**
     * Gets entry by id
     * 
     * @param id
     * @return entry (or null, if not found)
     */
    public Entry getEntry(String id) {
        List<Element> elements = getElements();
        for (Element element : elements) {
            if (element instanceof Entry) {
                Entry entry = (Entry) element;
                IRI entryId = entry.getId();
                if (entryId != null && entryId.equals(new IRI(id))) {
                    return entry;
                }
            }
        }
        return null;
    }

}
