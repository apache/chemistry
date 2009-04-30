/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.server.atompub;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;
import org.apache.jackrabbit.cmis.Capabilities;
import org.apache.jackrabbit.cmis.Repository;

public abstract class AbstractRepositoryProvider extends AbstractWorkspaceProvider {

    private Repository repository;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    protected ResponseContext getServiceDocument(final RequestContext request) {
        return new StreamWriterResponseContext(request.getAbdera()) {
            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startService();
                for (WorkspaceInfo wi : getWorkspaceManager(request)
                        .getWorkspaces(request)) {
                    sw.startWorkspace()
                        .writeAttribute(CMIS.REPOSITORY_RELATIONSHIP, "self")
                        .writeTitle(wi.getTitle(request));

                    writeRepositoryInfo(sw);

                    for (CollectionInfo ci : wi.getCollections(request)) {
                        sw.startCollection(ci.getHref(request))
                            .writeTitle(ci.getTitle(request))
                            .writeAccepts(ci.getAccepts(request));
                        CategoriesInfo[] catinfos = ci.getCategoriesInfo(request);
                        if (catinfos != null) {
                            for (CategoriesInfo catinfo : catinfos) {
                                String cathref = catinfo.getHref(request);
                                if (cathref != null) {
                                    sw.startCategories().
                                        writeAttribute("href",
                                                request.getTargetBasePath()
                                                    + cathref).endCategories();
                                } else {
                                    sw.startCategories(
                                            catinfo.isFixed(request),
                                            catinfo.getScheme(request));
                                    for (CategoryInfo cat : catinfo) {
                                        sw.writeCategory(
                                                cat.getTerm(request),
                                                cat.getScheme(request),
                                                cat.getLabel(request));
                                    }
                                    sw.endCategories();
                                }
                            }
                        }
                        sw.endCollection();
                    }
                    sw.endWorkspace();
                }
                sw.endService()
                  .endDocument();
            }
        }
        .setStatus(200)
        .setContentType(Constants.APP_MEDIA_TYPE);
    }

    protected void writeRepositoryInfo(StreamWriter sw) throws IOException {
        sw.startElement(CMIS.REPOSITORY_INFO);
        writeElement(sw, CMIS.REPOSITORY_ID, repository.getId());
        writeElement(sw, CMIS.REPOSITORY_NAME, repository.getName());
        writeElement(sw, CMIS.REPOSITORY_RELATIONSHIP, "self");
        writeElement(sw, CMIS.REPOSITORY_DESCRIPTION, repository.getDescription());
        writeElement(sw, CMIS.VENDOR_NAME, repository.getVendorName());
        writeElement(sw, CMIS.PRODUCT_NAME, repository.getProductName());
        writeElement(sw, CMIS.PRODUCT_VERSION, repository.getProductVersion());
        writeElement(sw, CMIS.ROOT_FOLDER_ID, repository.getRootFolderId());

        Capabilities capabilities = repository.getCapabilities();

        sw.startElement(CMIS.CAPABILITIES);
        writeElement(sw, CMIS.CAPABILITY_MULTIFILING, capabilities.hasMultifiling());
        writeElement(sw, CMIS.CAPABILITY_UNFILING, capabilities.hasUnfiling());
        writeElement(sw, CMIS.CAPABILITY_VERSION_SPECIFIC_FILING, capabilities.hasVersionSpecificFiling());
        writeElement(sw, CMIS.CAPABILITY_PWC_UPDATEABLE, capabilities.isPWCUpdatable());
        writeElement(sw, CMIS.CAPABILITY_PWC_SEARCHABLE, capabilities.isPWCSearchable());
        writeElement(sw, CMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE, capabilities.areAllVersionsSearchable());
        writeElement(sw, CMIS.CAPABILITY_QUERY, capabilities.getQuerySupport().toString());
        writeElement(sw, CMIS.CAPABILITY_JOIN, capabilities.getJoinSupport().toString());
        writeElement(sw, CMIS.CAPABILITY_FULL_TEXT, capabilities.getFullTextSupport().toString());
        sw.endElement();

        writeElement(sw, CMIS.VERSIONS_SUPPORTED, repository.getVersionsSupported());
        sw.endElement();
    }

    private void writeElement(StreamWriter sw, QName qname, String s) {
        sw.startElement(qname).
            writeElementText(s).
            endElement();
    }

    private void writeElement(StreamWriter sw, QName qname, boolean b) {
        sw.startElement(qname).
            writeElementText(b ? "true" : "false").
            endElement();
    }
}
