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
 *     Florent Guillaume, Nuxeo
 *     Michael Durig, Day
 */
package org.apache.chemistry.atompub.client.stax;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.APPContext;
import org.apache.chemistry.atompub.client.APPRepositoryCapabilities;
import org.apache.chemistry.atompub.client.APPRepositoryInfo;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Base class for service document reader implementations.
 */
public abstract class ServiceDocumentReader<T extends Repository> {

    protected abstract T createRepository(APPContext ctx);

    protected abstract void addCollection(T repo, String href, String type);

    protected abstract void addURITemplate(T repo, URITemplate uriTemplate);

    protected abstract void setInfo(T repo, RepositoryInfo info);

    public List<T> read(APPContext ctx, InputStream in) throws IOException {
        try {
            StaxReader reader = StaxReader.newReader(in);
            if (!reader.fwdTag("service")) {
                throw new IOException("Invalid APP service document");
            }
            List<T> repos = new ArrayList<T>(1);
            ChildrenNavigator workspaces = reader.getChildren("workspace");
            while (workspaces.next()) {
                T repo = createRepository(ctx);
                ChildrenNavigator children = reader.getChildren();
                while (children.next()) {
                    QName name = reader.getName();
                    if (AtomPub.APP_COLLECTION.equals(name)) {
                        String href = reader.getAttributeValue("href");
                        String type = "";
                        ChildrenNavigator nav = reader.getChildren();
                        while (nav.next()) {
                            QName n = reader.getName();
                            if (AtomPubCMIS.COLLECTION_TYPE.equals(n)) {
                                type = reader.getElementText();
                            }
                        }
                        addCollection(repo, href, type);
                    } else if (AtomPubCMIS.REPOSITORY_INFO.equals(name)) {
                        RepositoryInfo info = readRepositoryInfo(ctx, reader);
                        setInfo(repo, info);
                    } else if (AtomPubCMIS.URI_TEMPLATE.equals(name)) {
                        URITemplate uriTemplate = readURITemplate(ctx, reader);
                        addURITemplate(repo, uriTemplate);
                    }
                }
                repos.add(repo);
            }
            return repos;
        } catch (XMLStreamException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected RepositoryInfo readRepositoryInfo(APPContext ctx,
            StaxReader reader) throws XMLStreamException {
        APPRepositoryCapabilities caps = null;
        Map<String, Object> map = new HashMap<String, Object>();
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next()) {
            QName name = reader.getName();
            if (CMIS.CAPABILITIES.equals(name)) {
                caps = new APPRepositoryCapabilities();
                ChildrenNavigator capElems = reader.getChildren();
                while (capElems.next()) {
                    name = reader.getName();
                    if (CMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE.equals(name)) {
                        caps.setAllVersionsSearchable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_CAN_GET_DESCENDANTS.equals(name)) {
                        caps.setHasGetDescendants(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_CAN_GET_FOLDER_TREE.equals(name)) {
                        caps.setHasGetFolderTree(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_CONTENT_STREAM_UPDATABILITY.equals(name)) {
                        caps.setContentStreamUpdatableAnytime("anytime".equals(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_MULTIFILING.equals(name)) {
                        caps.setHasMultifiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_PWC_SEARCHABLE.equals(name)) {
                        caps.setPWCSearchable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_PWC_UPDATABLE.equals(name)) {
                        caps.setPWCUpdatable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_UNFILING.equals(name)) {
                        caps.setHasUnfiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_VERSION_SPECIFIC_FILING.equals(name)) {
                        caps.setHasVersionSpecificFiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (CMIS.CAPABILITY_QUERY.equals(name)) {
                        caps.setQueryCapability(CapabilityQuery.get(
                                reader.getElementText(), CapabilityQuery.NONE));
                    } else if (CMIS.CAPABILITY_JOIN.equals(name)) {
                        caps.setJoinCapability(CapabilityJoin.get(
                                reader.getElementText(), CapabilityJoin.NONE));
                    } else if (CMIS.CAPABILITY_RENDITIONS.equals(name)) {
                        caps.setRenditionCapability(CapabilityRendition.get(
                                reader.getElementText(),
                                CapabilityRendition.NONE));
                    } else if (CMIS.CAPABILITY_CHANGES.equals(name)) {
                        caps.setChangeCapability(CapabilityChange.get(
                                reader.getElementText(), CapabilityChange.NONE));
                    } else if (CMIS.CAPABILITY_ACL.equals(name)) {
                        caps.setACLCapability(CapabilityACL.get(
                                reader.getElementText(), CapabilityACL.NONE));
                    }
                }
            } else if (CMIS.REPOSITORY_SPECIFIC_INFORMATION.equals(name)) {
                readRepositorySpecificInformation(ctx, reader);
            } else if (CMIS.CHANGES_ON_TYPE.equals(name)) {
                changeLogBaseTypes.add(BaseType.get(reader.getElementText()));
            } else if (CMIS.ACL_CAPABILITY.equals(name)) {
                // TODO implement ACL capabilities
            } else {
                try {
                    map.put(name.getLocalPart(), reader.getElementText());
                } catch (XMLStreamException e) {
                    // ignore unknown tag containing non-text
                }
            }
        }
        if (changeLogBaseTypes.isEmpty()) {
            // TODO-0.63 TCK checks 0.62 schema which has minOccurs=1
            changeLogBaseTypes.add(BaseType.DOCUMENT);
            changeLogBaseTypes.add(BaseType.FOLDER);
            changeLogBaseTypes.add(BaseType.RELATIONSHIP);
            changeLogBaseTypes.add(BaseType.POLICY);
        }
        return new APPRepositoryInfo(caps, map, changeLogBaseTypes);
    }

    protected URITemplate readURITemplate(APPContext ctx, StaxReader reader)
            throws XMLStreamException {
        String type = null;
        String mediaType = null;
        String template = null;
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next()) {
            QName name = reader.getName();
            if (AtomPubCMIS.TYPE.equals(name)) {
                type = reader.getElementText();
            } else if (AtomPubCMIS.MEDIA_TYPE.equals(name)) {
                mediaType = reader.getElementText();
            } else if (AtomPubCMIS.TEMPLATE.equals(name)) {
                template = reader.getElementText();
            }
        }
        return new URITemplate(type, mediaType, template);
    }

    protected void readRepositorySpecificInformation(APPContext ctx,
            StaxReader reader) {
        // do nothing
    }

}
