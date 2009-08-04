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

import org.apache.chemistry.CapabilityACL;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.CapabilityChange;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.CapabilityRendition;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.atompub.AtomPub;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.URITemplate;
import org.apache.chemistry.atompub.client.APPRepository;
import org.apache.chemistry.atompub.client.APPRepositoryCapabilities;
import org.apache.chemistry.atompub.client.APPRepositoryInfo;
import org.apache.chemistry.xml.stax.ChildrenNavigator;
import org.apache.chemistry.xml.stax.StaxReader;

/**
 * Base class for service document reader implementations.
 */
public abstract class ServiceDocumentReader<T extends Repository> {

    protected abstract T createRepository(ReadContext ctx);

    protected abstract void addCollection(T repo, String href, String type);

    protected abstract void addURITemplate(T repo, URITemplate uriTemplate);

    protected abstract void setInfo(T repo, RepositoryInfo info);

    @SuppressWarnings("unchecked")
    public T[] read(ReadContext context, InputStream in) throws IOException {
        try {
            StaxReader reader = StaxReader.newReader(in);
            if (!reader.fwdTag("service")) {
                throw new IOException("Invalid APP service document");
            }
            List<Repository> repos = new ArrayList<Repository>();
            ChildrenNavigator workspaces = reader.getChildren("workspace");
            while (workspaces.next()) {
                T repo = createRepository(context);
                ChildrenNavigator children = reader.getChildren();
                while (children.next()) {
                    QName name = reader.getName();
                    if (name.equals(AtomPub.APP_COLLECTION)) {
                        String href = reader.getAttributeValue("href");
                        String type = reader.getAttributeValue(AtomPubCMIS.COLLECTION_TYPE.getLocalPart());
                        addCollection(repo, href, type);
                    } else if (name.equals(AtomPubCMIS.REPOSITORY_INFO)) {
                        RepositoryInfo info = readRepositoryInfo(context,
                                reader);
                        setInfo(repo, info);
                    } else if (name.equals(AtomPubCMIS.URI_TEMPLATE)) {
                        URITemplate uriTemplate = readURITemplate(context,
                                reader);
                        addURITemplate(repo, uriTemplate);
                    }
                }
                repos.add(repo);
            }
            return (T[]) repos.toArray(new APPRepository[repos.size()]);
        } catch (XMLStreamException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected RepositoryInfo readRepositoryInfo(ReadContext context,
            StaxReader reader) throws XMLStreamException {
        APPRepositoryCapabilities caps = null;
        Map<String, Object> map = new HashMap<String, Object>();
        Set<BaseType> changeLogBaseTypes = new HashSet<BaseType>();
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next()) {
            String localName = reader.getLocalName();
            if (localName.equals(CMIS.CAPABILITIES.getLocalPart())) {
                caps = new APPRepositoryCapabilities();
                ChildrenNavigator capElems = reader.getChildren();
                while (capElems.next()) {
                    localName = reader.getLocalName();
                    if (localName.equals(CMIS.CAPABILITY_ALL_VERSIONS_SEARCHABLE.getLocalPart())) {
                        caps.setAllVersionsSearchable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_CAN_GET_DESCENDANTS.getLocalPart())) {
                        caps.setHasGetDescendants(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_CONTENT_STREAM_UPDATABILITY.getLocalPart())) {
                        caps.setContentStreamUpdatableAnytime("anytime".equals(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_MULTIFILING.getLocalPart())) {
                        caps.setHasMultifiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_PWC_SEARCHABLE.getLocalPart())) {
                        caps.setPWCSearchable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_PWC_UPDATEABLE.getLocalPart())) {
                        caps.setPWCUpdatable(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_UNFILING.getLocalPart())) {
                        caps.setHasUnfiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_VERSION_SPECIFIC_FILING.getLocalPart())) {
                        caps.setHasVersionSpecificFiling(Boolean.parseBoolean(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_QUERY.getLocalPart())) {
                        caps.setQueryCapability(CapabilityQuery.get(
                                reader.getElementText(), CapabilityQuery.NONE));
                    } else if (localName.equals(CMIS.CAPABILITY_JOIN.getLocalPart())) {
                        caps.setJoinCapability(CapabilityJoin.get(
                                reader.getElementText(), CapabilityJoin.NONE));
                    } else if (localName.equals(CMIS.CAPABILITY_RENDITIONS.getLocalPart())) {
                        caps.setRenditionCapability(CapabilityRendition.get(
                                reader.getElementText(),
                                CapabilityRendition.NONE));
                    } else if (localName.equals(CMIS.CAPABILITY_CHANGES.getLocalPart())) {
                        caps.setChangeCapability(CapabilityChange.get(
                                reader.getElementText(), CapabilityChange.NONE));
                    } else if (localName.equals(CMIS.CAPABILITY_CHANGES_ON_TYPE.getLocalPart())) {
                        changeLogBaseTypes.add(BaseType.get(reader.getElementText()));
                    } else if (localName.equals(CMIS.CAPABILITY_ACL.getLocalPart())) {
                        caps.setACLCapability(CapabilityACL.get(
                                reader.getElementText(), CapabilityACL.NONE));
                    }
                }
            } else if (localName.equals(CMIS.REPOSITORY_SPECIFIC_INFORMATION.getLocalPart())) {
                readRepositorySpecificInformation(context, reader);
            } else {
                map.put(localName, reader.getElementText());
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

    protected URITemplate readURITemplate(ReadContext context, StaxReader reader)
            throws XMLStreamException {
        String type = null;
        String mediaType = null;
        String template = null;
        ChildrenNavigator nav = reader.getChildren();
        while (nav.next()) {
            String localName = reader.getLocalName();
            if (localName.equals(AtomPubCMIS.TYPE.getLocalPart())) {
                type = reader.getElementText();
            } else if (localName.equals(AtomPubCMIS.MEDIA_TYPE.getLocalPart())) {
                mediaType = reader.getElementText();
            } else if (localName.equals(AtomPubCMIS.TEMPLATE.getLocalPart())) {
                template = reader.getElementText();
            }
        }
        return new URITemplate(type, mediaType, template);
    }

    protected void readRepositorySpecificInformation(ReadContext context,
            StaxReader reader) {
        // do nothing
    }

}
