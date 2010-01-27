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
 *     David Ward, Alfresco
 */
package org.apache.chemistry.tck.atompub.fixture;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.chemistry.abdera.ext.CMISCapabilities;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKSkipCapabilityException;
import org.apache.chemistry.tck.atompub.client.CMISClient;
import org.apache.chemistry.tck.atompub.fixture.EntryTree.TreeVisitor;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.junit.Assert;

/**
 * A visitor that fetches all renditions for an {@link EntryTree}. When called
 * through the {@link #testRenditions(EntryTree, EntryGenerator)} method, will
 * test a supplied {@link EntryGenerator} with a variety of rendition filters
 * and ensure the renditions returned match those expected.
 */
public class GatherRenditionsVisitor implements TreeVisitor {

    private CMISClient client;
    private Set<MimeType> renditionMimeTypes = new HashSet<MimeType>(5);
    private Set<String> renditionKinds = new HashSet<String>(5);
    private Map<String, Map<String, Set<IRI>>> entryRenditionMap = new HashMap<String, Map<String, Set<IRI>>>(5);

    public GatherRenditionsVisitor(CMISClient client) {
        this.client = client;
    }

    private String getObjectId(Entry entry) {
        Assert.assertNotNull(entry);
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        Assert.assertNotNull(object);
        String objectId = object.getObjectId().getStringValue();
        Assert.assertNotNull(objectId);
        return objectId;
    }

    public void visit(EntryTree entry) throws Exception {
        // Get the object ID
        String objectId = getObjectId(entry.entry);

        // get the entry, this time with renditions (if any)
        CMISUriTemplate objectByIdTemplate = client.getObjectByIdUriTemplate(client.getWorkspace());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("id", objectId);
        variables.put("renditionFilter", "*");
        IRI objectByIdRequest = objectByIdTemplate.generateUri(variables);

        Entry documentById = client.getEntry(objectByIdRequest);
        Assert.assertNotNull(documentById);
        String newObjectId = getObjectId(documentById);
        Assert.assertEquals(objectId, newObjectId);

        Map<String, Set<IRI>> entryRenditions = new HashMap<String, Set<IRI>>();
        this.entryRenditionMap.put(objectId, entryRenditions);
        
        // Get each of the renditions
        List<Link> renditions = this.client.getRenditionLinks(documentById);

        for (Link rendition : renditions) {

            // Check the rendition link is well formed
            MimeType mimeType = rendition.getMimeType();
            Assert.assertNotNull(mimeType);
            String renditionKind = rendition.getAttributeValue(CMISConstants.RENDITION_KIND);
            Assert.assertNotNull(renditionKind);
            IRI renditionLink = rendition.getHref();
            Assert.assertNotNull(renditionLink);

            // Check the rendition can be fetched
            this.client.executeRequest(new GetRequest(renditionLink.toString()), 200);

            // Add the rendition link to our maps
            this.renditionMimeTypes.add(mimeType);
            this.renditionKinds.add(renditionKind);

            Set<IRI> typeRenditions = entryRenditions.get(mimeType.getBaseType());
            if (typeRenditions == null) {
                typeRenditions = new HashSet<IRI>(5);
                entryRenditions.put(mimeType.getBaseType(), typeRenditions);
            }
            typeRenditions.add(renditionLink);

            typeRenditions = entryRenditions.get(renditionKind);
            if (typeRenditions == null) {
                typeRenditions = new HashSet<IRI>(5);
                entryRenditions.put(renditionKind, typeRenditions);
            }
            typeRenditions.add(renditionLink);
        }
    }

    public void testRenditions(EntryTree fixture, EntryGenerator entryGenerator) throws Exception {
        CMISCapabilities capabilities = client.getCapabilities();
        if (capabilities.getRenditions().equals("none")) {
            throw new TCKSkipCapabilityException("Renditions", "read", "none");
        }

        // Gather together all the renditions in the tree
        fixture.walkTree(this);

        // Choose a mime type and a kind for testing with
        MimeType mimeType = null;
        Iterator<MimeType> mimeTypes = this.renditionMimeTypes.iterator();
        if (mimeTypes.hasNext()) {
            mimeType = mimeTypes.next();
        }
        String kind = null;
        Iterator<String> renditionKinds = this.renditionKinds.iterator();
        if (renditionKinds.hasNext()) {
            kind = renditionKinds.next();
        }

        // Test none
        getEntriesWithRenditionFilter(entryGenerator, "cmis:none");

        // Test all
        getEntriesWithRenditionFilter(entryGenerator, "*");

        // Test retrieval by specific mimetype and primary type
        if (mimeType != null) {
            getEntriesWithRenditionFilter(entryGenerator, mimeType.getBaseType());
            getEntriesWithRenditionFilter(entryGenerator, mimeType.getPrimaryType() + "/*");
        }
        if (kind != null) {
            // Test retrieval by kind
            getEntriesWithRenditionFilter(entryGenerator, kind);
            // Test retrieval by kind and mime type
            if (mimeType != null) {
                getEntriesWithRenditionFilter(entryGenerator, kind + ',' + mimeType.getBaseType());
                getEntriesWithRenditionFilter(entryGenerator, kind + ',' + mimeType.getPrimaryType() + "/*");
            }
        }
    }

    private void getEntriesWithRenditionFilter(EntryGenerator entryGenerator, String renditionFilter) throws Exception {
        EntryTree toValidate = entryGenerator.getEntries(renditionFilter);
        toValidate.walkTree(getRenditionCheckingVisitor(renditionFilter));
    }

    private TreeVisitor getRenditionCheckingVisitor(final String renditionFilter) {
        return new TreeVisitor() {

            private Set<IRI> predictRenditionsForFilter(Entry entry) {
                if (renditionFilter.equals("cmis:none")) {
                    return Collections.emptySet();
                } else if (renditionFilter.equals("*")) {
                    Set<IRI> result = new HashSet<IRI>(5);
                    Map<String, Set<IRI>> entryRenditions = GatherRenditionsVisitor.this.entryRenditionMap
                            .get(getObjectId(entry));

                    for (Set<IRI> renditions : entryRenditions.values()) {
                        result.addAll(renditions);
                    }
                    return result;
                }
                Set<IRI> result = new HashSet<IRI>(5);
                StringTokenizer tkn = new StringTokenizer(renditionFilter, ",");
                Map<String, Set<IRI>> entryRenditions = GatherRenditionsVisitor.this.entryRenditionMap
                        .get(getObjectId(entry));
                while (tkn.hasMoreTokens()) {
                    String token = tkn.nextToken();
                    MimeType mimeType;
                    try {
                        mimeType = new MimeType(token);
                        if (mimeType.getSubType().equals("*")) {
                            String primaryType = mimeType.getPrimaryType();
                            for (Map.Entry<String, Set<IRI>> typeEntry : entryRenditions.entrySet()) {
                                try {
                                    MimeType candidateType = new MimeType(typeEntry.getKey());
                                    if (candidateType.getPrimaryType().equals(primaryType)) {
                                        result.addAll(typeEntry.getValue());
                                    }
                                } catch (MimeTypeParseException e) {
                                }
                            }
                            continue;
                        }
                    } catch (MimeTypeParseException e) {
                    }
                    Set<IRI> renditions = entryRenditions.get(token);
                    if (renditions != null) {
                        result.addAll(renditions);
                    }
                }
                return result;
            }

            public void visit(EntryTree entry) throws Exception {
                Set<IRI> predictedRenditions = predictRenditionsForFilter(entry.entry);
                Set<IRI> actualRenditions = new HashSet<IRI>(predictedRenditions.size() * 2);

                // Get each of the renditions
                List<Link> renditions = GatherRenditionsVisitor.this.client.getRenditionLinks(entry.entry);

                for (Link rendition : renditions) {
                    // Check the rendition link is well formed
                    MimeType mimeType = rendition.getMimeType();
                    Assert.assertNotNull(mimeType);
                    String renditionKind = rendition.getAttributeValue(CMISConstants.RENDITION_KIND);
                    Assert.assertNotNull(renditionKind);
                    IRI renditionLink = rendition.getHref();
                    Assert.assertNotNull(renditionLink);

                    // Check the rendition can be fetched
                    GatherRenditionsVisitor.this.client.executeRequest(new GetRequest(renditionLink.toString()), 200);

                    // Add to our rendition set
                    actualRenditions.add(renditionLink);
                }

                Assert.assertEquals(predictedRenditions, actualRenditions);

            }
        };
    }

    public interface EntryGenerator {
        public EntryTree getEntries(String renditionFilter) throws Exception;
    }
}
