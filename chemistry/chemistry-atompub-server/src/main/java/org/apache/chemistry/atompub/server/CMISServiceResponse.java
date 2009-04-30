/*
 * Copyright 2009 Nuxeo SA <http://nuxeo.com>
 *
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
 *     Florent Guillaume
 */
package org.apache.chemistry.atompub.server;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.abdera.parser.stax.StaxStreamWriter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.writer.StreamWriter;
import org.apache.chemistry.atompub.CMIS;
import org.apache.chemistry.repository.QueryCapability;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.repository.RepositoryCapabilities;
import org.apache.chemistry.repository.RepositoryInfo;
import org.w3c.dom.Document;

/**
 * AtomPub response for the CMIS service document. This adds CMIS-specific
 * elements to the response.
 *
 * @author Florent Guillaume
 */
public class CMISServiceResponse extends StreamWriterResponseContext {

    protected final CMISProvider provider;

    protected final RequestContext request;

    public CMISServiceResponse(CMISProvider provider, RequestContext request) {
        super(request.getAbdera());
        this.provider = provider;
        this.request = request;
    }

    @Override
    protected void writeTo(StreamWriter sw) throws IOException {
        sw.startDocument();
        sw.startService();
        ((StaxStreamWriter) sw).writeNamespace(CMIS.CMIS_PREFIX, CMIS.CMIS_NS);
        for (WorkspaceInfo wi : provider.getWorkspaceManager(request).getWorkspaces(
                request)) {
            sw.startWorkspace();
            sw.writeTitle(wi.getTitle(request));
            RepositoryInfoWriter.write(sw, provider);
            for (CollectionInfo ci : wi.getCollections(request)) {
                sw.startCollection(ci.getHref(request));
                sw.writeAttribute(CMIS.COLLECTION_TYPE,
                        ((CMISCollection<?>) ci).getType());
                sw.writeTitle(ci.getTitle(request));
                sw.writeAccepts(ci.getAccepts(request));
                // no AtomPub categories
                sw.endCollection();
            }
            sw.endWorkspace();
        }
        sw.endService();
        sw.endDocument();
    }

    public static class RepositoryInfoWriter {

        public final StreamWriter sw;

        public final CMISProvider provider;

        public RepositoryInfoWriter(StreamWriter sw, CMISProvider provider) {
            this.sw = sw;
            this.provider = provider;
        }

        public static void write(StreamWriter sw, CMISProvider provider)
                throws IOException {
            new RepositoryInfoWriter(sw, provider).write();
        }

        public void write() throws IOException {
            Repository repository = provider.getRepository();
            RepositoryInfo info = repository.getInfo();
            RepositoryCapabilities cap = info.getCapabilities();

            sw.startElement(CMIS.REPOSITORY_INFO);
            write(CMIS.REPOSITORY_ID, repository.getId());
            write(CMIS.REPOSITORY_NAME, repository.getName());
            write(CMIS.REPOSITORY_RELATIONSHIP, "self");
            write(CMIS.REPOSITORY_DESCRIPTION, info.getDescription());
            write(CMIS.VENDOR_NAME, info.getVendorName());
            write(CMIS.PRODUCT_NAME, info.getProductName());
            write(CMIS.PRODUCT_VERSION, info.getProductVersion());
            write(CMIS.ROOT_FOLDER_ID, info.getRootFolderId());

            sw.startElement(CMIS.CAPABILITIES);
            write(CMIS.CAPABILITY_MULTIFILING, cap.hasMultifiling());
            write(CMIS.CAPABILITY_UNFILING, cap.hasUnfiling());
            write(CMIS.CAPABILITY_VERSION_SPECIFIC_FILING,
                    cap.hasVersionSpecificFiling());
            write(CMIS.CAPABILITY_PWC_UPDATEABLE, cap.isPWCUpdatable());
            write(CMIS.CAPABILITY_PWC_SEARCHABLE, cap.isPWCSearchable());
            write(CMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE,
                    cap.isAllVersionsSearchable());
            QueryCapability qc = cap.getQueryCapability();
            // CMIS 0.50 query/fulltext
            String fts;
            String qcs;
            switch (qc) {
            case NONE:
            case METADATA_ONLY:
                qcs = qc.toString();
                fts = "none";
                break;
            case FULL_TEXT_ONLY:
                qcs = qc.toString();
                fts = "fulltextonly";
                break;
            case BOTH_COMBINED:
                qcs = "both";
                fts = "fulltext";
                break;
            case BOTH_SEPARATE:
                qcs = "both";
                fts = "fulltextonly";
                break;
            default:
                throw new UnsupportedOperationException();
            }
            write(CMIS.CAPABILITY_QUERY, qcs); // CMIS 0.50
            write(CMIS.CAPABILITY_JOIN, cap.getJoinCapability().toString());
            write(CMIS.CAPABILITY_FULL_TEXT, fts); // CMIS 0.50
            sw.endElement();

            // String version = info.getVersionSupported();
            String version = "0.5";
            write(CMIS.VERSIONS_SUPPORTED, version);
            write(CMIS.REPOSITORY_SPECIFIC_INFORMATION,
                    info.getRepositorySpecificInformation());
            sw.endElement();
        }

        protected void write(QName qname, String string) {
            sw.startElement(qname);
            sw.writeElementText(string);
            sw.endElement();
        }

        protected void write(QName qname, boolean bool) {
            sw.startElement(qname);
            sw.writeElementText(bool ? "true" : "false");
            sw.endElement();
        }

        private void write(QName qname, Document document) {
            sw.startElement(qname);
            // XXX TODO
            sw.endElement();
        }
    }

}
