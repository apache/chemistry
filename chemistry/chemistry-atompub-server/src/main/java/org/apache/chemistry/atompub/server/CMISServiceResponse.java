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
package org.apache.chemistry.atompub.server;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.abdera.parser.stax.StaxStreamWriter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.writer.StreamWriter;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.w3c.dom.Document;

/**
 * AtomPub response for the CMIS service document. This adds CMIS-specific
 * elements to the response.
 */
public class CMISServiceResponse extends StreamWriterResponseContext {

    public static final String ATOMPUB_VERSION_SUPPORTED = "1.0";

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
        ((StaxStreamWriter) sw).writeNamespace(AtomPubCMIS.CMISRA_PREFIX,
                AtomPubCMIS.CMISRA_NS);
        for (WorkspaceInfo wi : provider.getWorkspaceManager(request).getWorkspaces(
                request)) {
            sw.startWorkspace();
            sw.writeTitle(wi.getTitle(request));
            // repository info
            RepositoryInfoWriter.write(sw, provider);
            // collections
            for (CollectionInfo ci : wi.getCollections(request)) {
                sw.startCollection(ci.getHref(request));
                sw.writeTitle(ci.getTitle(request));
                sw.writeAccepts(ci.getAccepts(request));
                sw.startElement(AtomPubCMIS.COLLECTION_TYPE);
                sw.writeElementText(((CMISCollection<?>) ci).getType());
                sw.endElement();
                // no AtomPub categories
                sw.endCollection();
            }
            // CMIS links
            // sw.startElement(AtomPub.ATOM_LINK);
            // sw.writeAttribute("type", "application/cmistree+xml");
            // sw.writeAttribute("rel", AtomPubCMIS.LINK_TYPES_DESCENDANTS);
            // String tdurl = request.absoluteUrlFor(TargetType.TYPE_SERVICE,
            // null);
            // tdurl = tdurl.replaceFirst("/repository$", "/typesdescendants");
            // sw.writeAttribute("href", tdurl);
            // sw.endElement();
            // URI templates
            for (URITemplate info : provider.getURITemplates(request)) {
                sw.startElement(AtomPubCMIS.URI_TEMPLATE);
                sw.startElement(AtomPubCMIS.TYPE);
                sw.writeElementText(info.type);
                sw.endElement();
                sw.startElement(AtomPubCMIS.MEDIA_TYPE);
                sw.writeElementText(info.mediaType);
                sw.endElement();
                sw.startElement(AtomPubCMIS.TEMPLATE);
                sw.writeElementText(info.template);
                sw.endElement();
                sw.endElement();
            }
            sw.endWorkspace();
        }
        sw.endService();
        sw.endDocument();
        sw.flush();
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

            sw.startElement(AtomPubCMIS.REPOSITORY_INFO);
            write(CMIS.REPOSITORY_ID, repository.getId());
            write(CMIS.REPOSITORY_NAME, repository.getName());
            write(CMIS.REPOSITORY_DESCRIPTION, info.getDescription());
            write(CMIS.VENDOR_NAME, info.getVendorName());
            write(CMIS.PRODUCT_NAME, info.getProductName());
            write(CMIS.PRODUCT_VERSION, info.getProductVersion());
            write(CMIS.ROOT_FOLDER_ID, info.getRootFolderId().getId());
            write(CMIS.LATEST_CHANGE_LOG_TOKEN, info.getLatestChangeLogToken());

            sw.startElement(CMIS.CAPABILITIES);
            write(CMIS.CAPABILITY_ACL, cap.getACLCapability().toString());
            write(CMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE,
                    cap.isAllVersionsSearchable());
            write(CMIS.CAPABILITY_CHANGES, cap.getChangeCapability().toString());
            write(CMIS.CAPABILITY_CONTENT_STREAM_UPDATABILITY,
                    cap.isContentStreamUpdatableAnytime() ? "anytime"
                            : "pwconly");
            write(CMIS.CAPABILITY_CAN_GET_DESCENDANTS, cap.hasGetDescendants());
            write(CMIS.CAPABILITY_CAN_GET_FOLDER_TREE, cap.hasGetFolderTree());
            write(CMIS.CAPABILITY_MULTIFILING, cap.hasMultifiling());
            write(CMIS.CAPABILITY_PWC_SEARCHABLE, cap.isPWCSearchable());
            write(CMIS.CAPABILITY_PWC_UPDATABLE, cap.isPWCUpdatable());
            write(CMIS.CAPABILITY_QUERY, cap.getQueryCapability().toString());
            write(CMIS.CAPABILITY_RENDITIONS,
                    cap.getRenditionCapability().toString());
            write(CMIS.CAPABILITY_UNFILING, cap.hasUnfiling());
            write(CMIS.CAPABILITY_VERSION_SPECIFIC_FILING,
                    cap.hasVersionSpecificFiling());
            write(CMIS.CAPABILITY_JOIN, cap.getJoinCapability().toString());
            sw.endElement();

            write(CMIS.VERSION_SUPPORTED, ATOMPUB_VERSION_SUPPORTED);
            // write(CMISXML.THIN_CLIENT_URI, "TODO");
            write(CMIS.CHANGES_INCOMPLETE, info.isChangeLogIncomplete());
            for (BaseType t : info.getChangeLogBaseTypes()) {
                write(CMIS.CHANGES_ON_TYPE, t.toString());
            }
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
            if (document == null) {
                return;
            }
            sw.startElement(qname);
            // XXX TODO
            sw.endElement();
        }
    }

}
