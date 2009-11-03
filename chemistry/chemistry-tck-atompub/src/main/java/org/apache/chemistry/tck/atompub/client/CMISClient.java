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
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.tck.atompub.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.chemistry.abdera.ext.CMISCapabilities;
import org.apache.chemistry.abdera.ext.CMISConstants;
import org.apache.chemistry.abdera.ext.CMISObject;
import org.apache.chemistry.abdera.ext.CMISRepositoryInfo;
import org.apache.chemistry.abdera.ext.CMISUriTemplate;
import org.apache.chemistry.tck.atompub.TCKMessageWriter;
import org.apache.chemistry.tck.atompub.http.Connection;
import org.apache.chemistry.tck.atompub.http.GetRequest;
import org.apache.chemistry.tck.atompub.http.PostRequest;
import org.apache.chemistry.tck.atompub.http.Request;
import org.apache.chemistry.tck.atompub.http.Response;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Interact with CMIS Repository
 */
public class CMISClient {

	private TCKMessageWriter messageWriter;
	
    private Connection connection;
    private boolean traceConnection;

    private String serviceUrl;

    private CMISAppModel appModel = new CMISAppModel();

    private CMISValidator cmisValidator = new CMISValidator();
    private boolean validate = true;
    private boolean failOnValidationError = false;

    private ResourceLoader templates = new ResourceLoader("/org/apache/chemistry/tck/atompub/templates/");

    private Service cmisService = null;
    private CMISRepositoryInfo cmisRepositoryInfo = null;


    public CMISClient(Connection connection, String serviceUrl, TCKMessageWriter messageWriter) {
        this.connection = connection;
        this.serviceUrl = serviceUrl;
    	this.messageWriter = messageWriter;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public void setFailOnValidationError(boolean failOnValidationError) {
        this.failOnValidationError = failOnValidationError;
    }

    public void setTrace(boolean trace) {
        this.traceConnection = trace;
    }

    public Service getRepository() throws Exception {
        if (cmisService == null) {
            Request req = new GetRequest(serviceUrl);
            Response res = executeRequest(req, 200);
            String xml = res.getContentAsString();
            Assert.assertNotNull(xml);
            Assert.assertTrue(xml.length() > 0);
            cmisService = appModel.parseService(new StringReader(xml), null);
            Assert.assertNotNull(cmisService);
        }
        return cmisService;
    }

    public CMISRepositoryInfo getRepositoryInfo() throws Exception {
        if (cmisRepositoryInfo == null) {
            // TODO: latestChangeLogToken can't be cached
            Service repo = getRepository();
            Workspace workspace = getWorkspace(repo);
            cmisRepositoryInfo = workspace.getExtension(CMISConstants.REPOSITORY_INFO);
            Assert.assertNotNull(cmisRepositoryInfo);
        }
        return cmisRepositoryInfo;
    }
    
    public CMISCapabilities getCapabilities() throws Exception {
        return getRepositoryInfo().getCapabilities();
    }

    public Workspace getWorkspace() throws Exception {
        return getRepository().getWorkspaces().get(0);
    }

    public Workspace getWorkspace(Service service) {
        return service.getWorkspaces().get(0);
    }

    public Collection getCMISCollection(Workspace workspace, String collectionId) {
        List<Collection> collections = workspace.getCollections();
        for (Collection collection : collections) {
            Element collectionType = collection.getFirstChild(CMISConstants.COLLECTION_TYPE);
            if (collectionType != null && collectionId.equals(collectionType.getText())) {
                return collection;
            }
        }
        return null;
    }

    public IRI getRootCollection(Workspace workspace) {
        Collection root = getCMISCollection(workspace, CMISConstants.COLLECTION_ROOT);
        Assert.assertNotNull(root);
        IRI rootHREF = root.getHref();
        Assert.assertNotNull(rootHREF);
        return rootHREF;
    }

    public IRI getCheckedOutCollection(Workspace workspace) {
        Collection root = getCMISCollection(workspace, CMISConstants.COLLECTION_CHECKEDOUT);
        Assert.assertNotNull(root);
        IRI rootHREF = root.getHref();
        Assert.assertNotNull(rootHREF);
        return rootHREF;
    }

    public IRI getTypesChildrenCollection(Workspace workspace) {
        Collection root = getCMISCollection(workspace, CMISConstants.COLLECTION_TYPES);
        Assert.assertNotNull(root);
        IRI rootHREF = root.getHref();
        Assert.assertNotNull(rootHREF);
        return rootHREF;
    }

    public IRI getQueryCollection(Workspace workspace) {
        Collection root = getCMISCollection(workspace, CMISConstants.COLLECTION_QUERY);
        Assert.assertNotNull(root);
        IRI rootHREF = root.getHref();
        Assert.assertNotNull(rootHREF);
        return rootHREF;
    }

    public CMISUriTemplate getUriTemplate(Workspace workspace, String templateType)
    {
        List<CMISUriTemplate> templates = workspace.getExtensions(CMISConstants.URI_TEMPLATE);
        for (CMISUriTemplate template : templates) {
            if (templateType.equals(template.getType())) {
                return template;
            }
        }
        return null;
    }
    
    public CMISUriTemplate getObjectByIdUriTemplate(Workspace workspace) {
        return getUriTemplate(workspace, CMISConstants.URI_OBJECT_BY_ID);
    }

    public CMISUriTemplate getObjectByPathUriTemplate(Workspace workspace) {
        return getUriTemplate(workspace, CMISConstants.URI_OBJECT_BY_PATH);
    }
    
    public CMISUriTemplate getQueryUriTemplate(Workspace workspace) {
        return getUriTemplate(workspace, CMISConstants.URI_QUERY);
    }

    public CMISUriTemplate getTypeByIdUriTemplate(Workspace workspace) {
        return getUriTemplate(workspace, CMISConstants.URI_TYPE_BY_ID);
    }

    public Link getLink(Entry entry, String rel, String... matchesMimetypes) {
        List<Link> links = entry.getLinks(rel);
        if (links != null) {
            for (Link link : links) {
                MimeType mimetype = link.getMimeType();
                if (matchesMimetypes.length == 0) {
                    if (links.size() == 1) {
                        // take the single link regardless of type
                        return link;
                    } else if (mimetype == null) {
                        // take the link if it doesn't have a type
                        return link;
                    }
                }
                for (String matchesMimetype : matchesMimetypes) {
                    try {
                        MimeType mtMatchesMimetype = new MimeType(matchesMimetype);
                        String type = mimetype.getParameter("type");
                        if (type == null) {
                            if (mimetype != null && mimetype.getBaseType().equals(mtMatchesMimetype.getBaseType())
                                    && mimetype.getSubType().equals(mtMatchesMimetype.getSubType())) {
                                return link;
                            }
                        } else {
                            String matchesType = mtMatchesMimetype.getParameter("type");
                            if (mimetype != null && mimetype.getBaseType().equals(mtMatchesMimetype.getBaseType())
                                    && mimetype.getSubType().equals(mtMatchesMimetype.getSubType())
                                    && type.equals(matchesType)) {
                                return link;
                            }
                        }
                    } catch (MimeTypeParseException e) {
                        // note: not a match
                    }
                }
            }
        }
        return null;
    }

    public Link getChildrenLink(Entry entry) {
        return getLink(entry, CMISConstants.REL_DOWN, CMISConstants.MIMETYPE_FEED);
    }

    public Link getDescendantsLink(Entry entry) {
        return getLink(entry, CMISConstants.REL_DOWN, CMISConstants.MIMETYPE_CMISTREE);
    }

    public Link getFolderTreeLink(Entry entry) {
        return getLink(entry, CMISConstants.REL_FOLDER_TREE, CMISConstants.MIMETYPE_CMISTREE);
    }

    public Link getObjectParentsLink(Entry entry) {
        return getLink(entry, CMISConstants.REL_UP, CMISConstants.MIMETYPE_FEED);
    }

    public Link getFolderParentLink(Entry entry) {
        return getLink(entry, CMISConstants.REL_UP, CMISConstants.MIMETYPE_ENTRY);
    }

    public Entry getEntry(IRI href) throws Exception {
        return getEntry(href, null);
    }

    public Entry getEntry(IRI href, Map<String, String> args) throws Exception {
        Request get = new GetRequest(href.toString()).setArgs(args);
        Response res = executeRequest(get, 200);
        String xml = res.getContentAsString();
        Entry entry = appModel.parseEntry(new StringReader(xml), null);
        Assert.assertNotNull(entry);
        // TODO: fix up self links with arguments
//        if (args == null) {
//            Assert.assertEquals(get.getFullUri(), entry.getSelfLink().getHref().toString());
//        }
        return entry;
    }

    public Feed getFeed(IRI href) throws Exception {
        return getFeed(href, null);
    }

    public Feed getFeed(IRI href, Map<String, String> args) throws Exception {
        Request get = new GetRequest(href.toString()).setArgs(args);
        Response res = executeRequest(get, 200);
        Assert.assertNotNull(res);
        String xml = res.getContentAsString();
        Feed feed = appModel.parseFeed(new StringReader(xml), null);
        Assert.assertNotNull(feed);
//        Assert.assertEquals(get.getFullUri(), feed.getSelfLink().getHref().toString());
        return feed;
    }

    public Entry createFolder(IRI parent, String name) throws Exception {
        return createFolder(parent, name, null);
    }

    public Entry createFolder(IRI parent, String name, String atomEntryFile) throws Exception {
        String createFolder = templates.load(atomEntryFile == null ? "createfolder.atomentry.xml" : atomEntryFile);
        createFolder = createFolder.replace("${NAME}", name);
        Request req = new PostRequest(parent.toString(), createFolder, CMISConstants.MIMETYPE_ENTRY);
        Response res = executeRequest(req, 201);
        Assert.assertNotNull(res);
        String xml = res.getContentAsString();
        Entry entry = appModel.parseEntry(new StringReader(xml), null);
        Assert.assertNotNull(entry);
        Assert.assertEquals(name, entry.getTitle());
        // Assert.assertEquals(name + " (summary)", entry.getSummary());
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        Assert.assertEquals(CMISConstants.TYPE_FOLDER, object.getBaseTypeId().getStringValue());
        String testFolderHREF = (String) res.getHeader("Location");
        Assert.assertNotNull(testFolderHREF);
        return entry;
    }

    public Entry createDocument(IRI parent, String name) throws Exception {
        return createDocument(parent, name, null);
    }

    public Entry createDocument(IRI parent, String name, String atomEntryFile) throws Exception {
        String createFile = templates.load(atomEntryFile == null ? "createdocument.atomentry.xml" : atomEntryFile);
        createFile = createFile.replace("${NAME}", name);
        // determine if creating content via mediatype
        Entry createEntry = appModel.parseEntry(new StringReader(createFile), null);
        MimeType mimeType = createEntry.getContentMimeType();
        boolean mediaType = (mimeType != null);
        createFile = createFile.replace("${CMISCONTENT}", new String(Base64.encodeBase64(name.getBytes())));
        createFile = createFile.replace("${CONTENT}", mediaType ? new String(Base64.encodeBase64(name.getBytes())) : name);
        Request req = new PostRequest(parent.toString(), createFile, CMISConstants.MIMETYPE_ENTRY);
        Response res = executeRequest(req, 201);
        Assert.assertNotNull(res);
        String xml = res.getContentAsString();
        Entry entry = appModel.parseEntry(new StringReader(xml), null);
        Assert.assertNotNull(entry);
        Assert.assertEquals(name, entry.getTitle());
        // Assert.assertEquals(name + " (summary)", entry.getSummary());
        Assert.assertNotNull(entry.getContentSrc());
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        Assert.assertEquals(CMISConstants.TYPE_DOCUMENT, object.getBaseTypeId().getStringValue());
        String testFileHREF = (String) res.getHeader("Location");
        Assert.assertNotNull(testFileHREF);
        return entry;
    }

    public Entry createRelationship(IRI parent, String type, String targetId) throws Exception {
        return createRelationship(parent, type, targetId, "createrelationship.atomentry.xml");
    }

    public Entry createRelationship(IRI parent, String type, String targetId, String atomEntryFile) throws Exception {
        String createFile = templates.load(atomEntryFile);
        createFile = createFile.replace("${RELTYPE}", type);
        createFile = createFile.replace("${TARGETID}", targetId);
        Request req = new PostRequest(parent.toString(), createFile, CMISConstants.MIMETYPE_ENTRY);
        Response res = executeRequest(req, 201);
        Assert.assertNotNull(res);
        String xml = res.getContentAsString();
        Entry entry = appModel.parseEntry(new StringReader(xml), null);
        Assert.assertNotNull(entry);
        CMISObject object = entry.getExtension(CMISConstants.OBJECT);
        Assert.assertEquals(CMISConstants.TYPE_RELATIONSHIP, object.getBaseTypeId().getStringValue());
        Assert.assertEquals(targetId, object.getTargetId().getStringValue());
        String testFileHREF = (String) res.getHeader("Location");
        Assert.assertNotNull(testFileHREF);
        return entry;
    }

    public Entry moveObject(IRI destFolder, Entry atomEntry, String sourceFolderId) throws Exception {
        Response res = moveObjectRequest(destFolder, atomEntry, sourceFolderId, 201);
        Assert.assertNotNull(res);
        String xml = res.getContentAsString();
        Entry entry = appModel.parseEntry(new StringReader(xml), null);
        return entry;
    }
    
    public Response moveObjectRequest(IRI destFolder, Entry atomEntry, String sourceFolderId, int expectedStatus) throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put("sourceFolderId", sourceFolderId);
        Request req = new PostRequest(destFolder.toString(), atomEntry.toString(), CMISConstants.MIMETYPE_ENTRY).setArgs(args);
        Response res = executeRequest(req, expectedStatus);
        return res;
    }
    
    /**
     * Execute Request
     *
     * @param req
     * @param expectedStatus
     * @param asUser
     * @return response
     * @throws IOException
     */
    public Response executeRequest(Request req, int expectedStatus) throws IOException {
        if (traceConnection) {
            messageWriter.trace("Request: " + req.getMethod() + " " + req.getFullUri()
                    + (req.getBody() == null ? "" : "\n" + new String(req.getBody(), req.getEncoding())));
        }

        Response res = connection.executeRequest(req);

        if (traceConnection) {
            messageWriter.trace("Response: " + res.getStatus() + " " + req.getMethod() + " "
                    + req.getFullUri() + (res.getContentAsString() == null ? "" : "\n" + res.getContentAsString()));
        }

        if (expectedStatus > -1)
            Assert.assertEquals("Request status for " + req.getFullUri(), expectedStatus, res.getStatus());

        if (validate) {
            Validator mimetypeValidator = null;
            String contentType = res.getContentType();
            if (contentType != null) {
                try {
                    // @TODO: register of mappings
                    if (contentType.startsWith(CMISConstants.MIMETYPE_ATOM)) {
                        mimetypeValidator = getAtomValidator();
                    }
                    else if (contentType.startsWith(CMISConstants.MIMETYPE_APP)) {
                        mimetypeValidator = getAppValidator();
                    }
                } catch(SAXException e) {}
                
                if (mimetypeValidator != null) {
                    try {
                        if (traceConnection) {
                            messageWriter.trace("Validating response of content type " + contentType);
                        }
                        
                        String resXML = res.getContentAsString();
                        assertValid(resXML, mimetypeValidator);
                    } catch (ParserConfigurationException e) {
                        // @TODO: Maybe make a Chemistry specific exception
                        throw new RuntimeException("Failed to validate", e);
                    }
                }
            }
        }
        
        return res;
    }

    public Validator getAppValidator() throws IOException, SAXException {
        return cmisValidator.getAppValidator();
    }

    public Validator getAtomValidator() throws IOException, SAXException {
        return cmisValidator.getCMISAtomValidator();
    }

    /**
     * Asserts XML complies with specified Validator
     *
     * @param xml
     *            xml to assert
     * @param validator
     *            validator to assert with
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private void assertValid(String xml, Validator validator) throws IOException, ParserConfigurationException {
        if (validate) {
            try {
                Document document = cmisValidator.getDocumentBuilder().parse(new InputSource(new StringReader(xml)));
                validator.validate(new DOMSource(document));
            } catch (SAXException e) {
                messageWriter.info("Failed Validation: " + cmisValidator.toString(e, null));
                if (failOnValidationError) {
                    Assert.fail(cmisValidator.toString(e, xml));
                }
            }
        }
    }

}
