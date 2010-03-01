/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.opencmis.client.provider.spi.atompub;

import static org.apache.opencmis.commons.impl.Converter.convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.opencmis.client.provider.spi.Session;
import org.apache.opencmis.client.provider.spi.atompub.objects.Acl;
import org.apache.opencmis.client.provider.spi.atompub.objects.AtomBase;
import org.apache.opencmis.client.provider.spi.atompub.objects.AtomElement;
import org.apache.opencmis.client.provider.spi.atompub.objects.AtomEntry;
import org.apache.opencmis.client.provider.spi.atompub.objects.AtomLink;
import org.apache.opencmis.client.provider.spi.atompub.objects.RepositoryWorkspace;
import org.apache.opencmis.client.provider.spi.atompub.objects.ServiceDoc;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.SessionParameter;
import org.apache.opencmis.commons.api.ExtensionsData;
import org.apache.opencmis.commons.api.TypeDefinition;
import org.apache.opencmis.commons.enums.AclPropagation;
import org.apache.opencmis.commons.enums.IncludeRelationships;
import org.apache.opencmis.commons.exceptions.CmisBaseException;
import org.apache.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.opencmis.commons.impl.Constants;
import org.apache.opencmis.commons.impl.JaxBHelper;
import org.apache.opencmis.commons.impl.ReturnVersion;
import org.apache.opencmis.commons.impl.UrlBuilder;
import org.apache.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.apache.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.opencmis.commons.impl.jaxb.CmisAccessControlListType;
import org.apache.opencmis.commons.impl.jaxb.CmisObjectType;
import org.apache.opencmis.commons.impl.jaxb.CmisPropertiesType;
import org.apache.opencmis.commons.impl.jaxb.CmisPropertyId;
import org.apache.opencmis.commons.impl.jaxb.CmisRepositoryInfoType;
import org.apache.opencmis.commons.impl.jaxb.CmisTypeDefinitionType;
import org.apache.opencmis.commons.provider.AccessControlEntry;
import org.apache.opencmis.commons.provider.AccessControlList;
import org.apache.opencmis.commons.provider.ObjectData;
import org.apache.opencmis.commons.provider.RepositoryInfoData;

/**
 * Base class for all AtomPub clients.
 * 
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 * 
 */
public class AbstractAtomPubService {

  protected enum IdentifierType {
    ID, PATH
  };

  protected static final String NAME_COLLECTION = "collection";
  protected static final String NAME_URI_TEMPLATE = "uritemplate";
  protected static final String NAME_PATH_SEGMENT = "pathSegment";
  protected static final String NAME_RELATIVE_PATH_SEGMENT = "relativePathSegment";
  protected static final String NAME_NUM_ITEMS = "numItems";

  private Session fSession;

  /**
   * Sets the current session.
   */
  protected void setSession(Session session) {
    fSession = session;
  }

  /**
   * Gets the current session.
   */
  protected Session getSession() {
    return fSession;
  }

  /**
   * Returns the service document URL of this session.
   */
  protected String getServiceDocURL() {
    Object url = fSession.get(SessionParameter.ATOMPUB_URL);
    if (url instanceof String) {
      return (String) url;
    }

    return null;
  }

  // ---- link cache ----

  /**
   * Returns the link cache or creates a new cache if it doesn't exist.
   */
  protected LinkCache getLinkCache() {
    LinkCache linkCache = (LinkCache) getSession().get(SpiSessionParameter.LINK_CACHE);
    if (linkCache == null) {
      linkCache = new LinkCache(getSession());
      getSession().put(SpiSessionParameter.LINK_CACHE, linkCache);
    }

    return linkCache;
  }

  /**
   * Gets a link from the cache.
   */
  protected String getLink(String repositoryId, String id, String rel, String type) {
    if (repositoryId == null) {
      throw new CmisInvalidArgumentException("Repository id must be set!");
    }

    if (id == null) {
      throw new CmisInvalidArgumentException("Object id must be set!");
    }

    return getLinkCache().getLink(repositoryId, id, rel, type);
  }

  /**
   * Gets a link from the cache.
   */
  protected String getLink(String repositoryId, String id, String rel) {
    return getLink(repositoryId, id, rel, null);
  }

  /**
   * Gets a link from the cache if it is there or loads it into the cache if it is not there.
   */
  protected String loadLink(String repositoryId, String id, String rel, String type) {
    String link = getLink(repositoryId, id, rel, type);
    if (link == null) {
      getObjectInternal(repositoryId, IdentifierType.ID, id, ReturnVersion.THIS, null, null, null,
          null, null, null, null);
      link = getLink(repositoryId, id, rel, type);
    }

    return link;
  }

  /**
   * Adds a link to the cache.
   */
  protected void addLink(String repositoryId, String id, String rel, String type, String link) {
    getLinkCache().addLink(repositoryId, id, rel, type, link);
  }

  /**
   * Adds a link to the cache.
   */
  protected void addLink(String repositoryId, String id, AtomLink link) {
    getLinkCache().addLink(repositoryId, id, link.getRel(), link.getType(), link.getHref());
  }

  /**
   * Removes all links of an object.
   */
  protected void removeLinks(String repositoryId, String id) {
    getLinkCache().removeLinks(repositoryId, id);
  }

  /**
   * Gets a type link from the cache.
   */
  protected String getTypeLink(String repositoryId, String typeId, String rel, String type) {
    if (repositoryId == null) {
      throw new CmisInvalidArgumentException("Repository id must be set!");
    }

    if (typeId == null) {
      throw new CmisInvalidArgumentException("Type id must be set!");
    }

    return getLinkCache().getTypeLink(repositoryId, typeId, rel, type);
  }

  /**
   * Gets a type link from the cache.
   */
  protected String getTypeLink(String repositoryId, String typeId, String rel) {
    return getTypeLink(repositoryId, typeId, rel, null);
  }

  /**
   * Gets a link from the cache if it is there or loads it into the cache if it is not there.
   */
  protected String loadTypeLink(String repositoryId, String typeId, String rel, String type) {
    String link = getTypeLink(repositoryId, typeId, rel, type);
    if (link == null) {
      getTypeDefinitionInternal(repositoryId, typeId);
      link = getTypeLink(repositoryId, typeId, rel, type);
    }

    return link;
  }

  /**
   * Adds a type link to the cache.
   */
  protected void addTypeLink(String repositoryId, String typeId, String rel, String type,
      String link) {
    getLinkCache().addTypeLink(repositoryId, typeId, rel, type, link);
  }

  /**
   * Adds a type link to the cache.
   */
  protected void addTypeLink(String repositoryId, String typeId, AtomLink link) {
    getLinkCache().addTypeLink(repositoryId, typeId, link.getRel(), link.getType(), link.getHref());
  }

  /**
   * Removes all links of a type.
   */
  protected void removeTypeLinks(String repositoryId, String id) {
    getLinkCache().removeTypeLinks(repositoryId, id);
  }

  /**
   * Gets a collection from the cache.
   */
  protected String getCollection(String repositoryId, String collection) {
    return getLinkCache().getCollection(repositoryId, collection);
  }

  /**
   * Gets a collection from the cache if it is there or loads it into the cache if it is not there.
   */
  protected String loadCollection(String repositoryId, String collection) {
    String link = getCollection(repositoryId, collection);
    if (link == null) {
      // cache repository info
      getRepositoriesInternal(repositoryId);
      link = getCollection(repositoryId, collection);
    }

    return link;
  }

  /**
   * Adds a collection to the cache.
   */
  protected void addCollection(String repositoryId, String collection, String link) {
    getLinkCache().addCollection(repositoryId, collection, link);
  }

  /**
   * Gets a repository link from the cache.
   */
  protected String getRepositoryLink(String repositoryId, String rel) {
    return getLinkCache().getRepositoryLink(repositoryId, rel);
  }

  /**
   * Gets a repository link from the cache if it is there or loads it into the cache if it is not
   * there.
   */
  protected String loadRepositoryLink(String repositoryId, String rel) {
    String link = getRepositoryLink(repositoryId, rel);
    if (link == null) {
      // cache repository info
      getRepositoriesInternal(repositoryId);
      link = getRepositoryLink(repositoryId, rel);
    }

    return link;
  }

  /**
   * Adds a repository link to the cache.
   */
  protected void addRepositoryLink(String repositoryId, String rel, String link) {
    getLinkCache().addRepositoryLink(repositoryId, rel, link);
  }

  /**
   * Adds a repository link to the cache.
   */
  protected void addRepositoryLink(String repositoryId, AtomLink link) {
    addRepositoryLink(repositoryId, link.getRel(), link.getHref());
  }

  /**
   * Gets an URI template from the cache.
   */
  protected String getTemplateLink(String repositoryId, String type, Map<String, Object> parameters) {
    return getLinkCache().getTemplateLink(repositoryId, type, parameters);
  }

  /**
   * Gets a template link from the cache if it is there or loads it into the cache if it is not
   * there.
   */
  protected String loadTemplateLink(String repositoryId, String type, Map<String, Object> parameters) {
    String link = getTemplateLink(repositoryId, type, parameters);
    if (link == null) {
      // cache repository info
      getRepositoriesInternal(repositoryId);
      link = getTemplateLink(repositoryId, type, parameters);
    }

    return link;
  }

  /**
   * Adds an URI template to the cache.
   */
  protected void addTemplate(String repositoryId, String type, String link) {
    getLinkCache().addTemplate(repositoryId, type, link);
  }

  // ---- exceptions ----

  /**
   * Converts a HTTP status code into an Exception.
   */
  protected CmisBaseException convertStatusCode(int code, String message, String errorContent,
      Throwable t) {
    switch (code) {
    case 400:
      return new CmisInvalidArgumentException(message, errorContent, t);
    case 404:
      return new CmisObjectNotFoundException(message, errorContent, t);
    case 403:
      return new CmisPermissionDeniedException(message, errorContent, t);
    case 405:
      return new CmisNotSupportedException(message, errorContent, t);
    case 409:
      return new CmisConstraintException(message, errorContent, t);
    default:
      return new CmisRuntimeException(message, errorContent, t);
    }
  }

  // ---- helpers ----

  protected boolean is(String name, AtomElement element) {
    return name.equals(element.getName().getLocalPart());
  }

  protected boolean isStr(String name, AtomElement element) {
    return is(name, element) && (element.getObject() instanceof String);
  }

  protected boolean isInt(String name, AtomElement element) {
    return is(name, element) && (element.getObject() instanceof BigInteger);
  }

  protected boolean isNextLink(AtomElement element) {
    return Constants.REL_NEXT.equals(((AtomLink) element.getObject()).getRel());
  }

  /**
   * Creates a CMIS object that only contains an id in the property list.
   */
  protected CmisObjectType createIdObject(String objectId) {
    CmisObjectType object = new CmisObjectType();

    CmisPropertiesType properties = new CmisPropertiesType();
    object.setProperties(properties);

    CmisPropertyId idProperty = new CmisPropertyId();
    properties.getProperty().add(idProperty);
    idProperty.setPropertyDefinitionId(PropertyIds.CMIS_OBJECT_ID);
    idProperty.getValue().add(objectId);

    return object;
  }

  /**
   * Parses an input stream.
   */
  @SuppressWarnings("unchecked")
  protected <T extends AtomBase> T parse(InputStream stream, Class<T> clazz) {
    AtomPubParser parser = new AtomPubParser(stream);

    try {
      parser.parse();
    }
    catch (Exception e) {
      throw new CmisConnectionException("Parsing exception!", e);
    }

    AtomBase parseResult = parser.getResults();

    if (!clazz.isInstance(parseResult)) {
      throw new CmisConnectionException("Unexpected document! Received "
          + (parseResult == null ? "something unknown" : parseResult.getType()) + "!");
    }

    return (T) parseResult;
  }

  /**
   * Performs a GET on an URL, checks the response code and returns the result.
   */
  protected HttpUtils.Response read(UrlBuilder url) {
    // make the call
    HttpUtils.Response resp = HttpUtils.invokeGET(url, fSession);

    // check response code
    if (resp.getResponseCode() != 200) {
      throw convertStatusCode(resp.getResponseCode(), resp.getResponseMessage(), resp
          .getErrorContent(), null);
    }

    return resp;
  }

  /**
   * Performs a POST on an URL, checks the response code and returns the result.
   */
  protected HttpUtils.Response post(UrlBuilder url, String contentType, HttpUtils.Output writer) {
    // make the call
    HttpUtils.Response resp = HttpUtils.invokePOST(url, contentType, writer, fSession);

    // check response code
    if (resp.getResponseCode() != 201) {
      throw convertStatusCode(resp.getResponseCode(), resp.getResponseMessage(), resp
          .getErrorContent(), null);
    }

    return resp;
  }

  /**
   * Performs a PUT on an URL, checks the response code and returns the result.
   */
  protected HttpUtils.Response put(UrlBuilder url, String contentType, HttpUtils.Output writer) {
    // make the call
    HttpUtils.Response resp = HttpUtils.invokePUT(url, contentType, writer, fSession);

    // check response code
    if ((resp.getResponseCode() < 200) || (resp.getResponseCode() > 299)) {
      throw convertStatusCode(resp.getResponseCode(), resp.getResponseMessage(), resp
          .getErrorContent(), null);
    }

    return resp;
  }

  /**
   * Performs a DELETE on an URL, checks the response code and returns the result.
   */
  protected void delete(UrlBuilder url) {
    // make the call
    HttpUtils.Response resp = HttpUtils.invokeDELETE(url, fSession);

    // check response code
    if (resp.getResponseCode() != 204) {
      throw convertStatusCode(resp.getResponseCode(), resp.getResponseMessage(), resp
          .getErrorContent(), null);
    }
  }

  // ---- common operations ----

  /**
   * Checks if at least one ACE list is not empty.
   */
  protected boolean isAclMergeRequired(AccessControlList addAces, AccessControlList removeAces) {
    return (addAces != null && addAces.getAces() != null && !addAces.getAces().isEmpty())
        || (removeAces != null && removeAces.getAces() != null && !removeAces.getAces().isEmpty());
  }

  /**
   * Merges the new ACL from original, add and remove ACEs lists.
   */
  protected AccessControlList mergeAcls(AccessControlList originalAces, AccessControlList addAces,
      AccessControlList removeAces) {

    if ((originalAces == null) || (originalAces.getAces() == null)) {
      originalAces = new AccessControlListImpl(new ArrayList<AccessControlEntry>());
    }

    if ((addAces == null) || (addAces.getAces() == null)) {
      throw new IllegalArgumentException("add ACEs must not be null!");
    }

    if ((removeAces == null) || (removeAces.getAces() == null)) {
      throw new IllegalArgumentException("remove ACEs must not be null!");
    }

    Map<String, Set<String>> originals = convertACEsToMap(originalAces.getAces());
    Map<String, Set<String>> adds = convertACEsToMap(addAces.getAces());
    Map<String, Set<String>> removes = convertACEsToMap(removeAces.getAces());
    List<AccessControlEntry> newACEs = new ArrayList<AccessControlEntry>();

    // iterate through the original ACEs
    for (Map.Entry<String, Set<String>> ace : originals.entrySet()) {

      // add permissions
      Set<String> addPermissions = adds.get(ace.getKey());
      if (addPermissions != null) {
        ace.getValue().addAll(addPermissions);
      }

      // remove permissions
      Set<String> removePermissions = removes.get(ace.getKey());
      if (removePermissions != null) {
        ace.getValue().removeAll(removePermissions);
      }

      // create new ACE
      if (!ace.getValue().isEmpty()) {
        newACEs.add(new AccessControlEntryImpl(new AccessControlPrincipalDataImpl(ace.getKey()),
            new ArrayList<String>(ace.getValue())));
      }
    }

    // find all ACEs that should be added but are not in the original ACE list
    for (Map.Entry<String, Set<String>> ace : adds.entrySet()) {
      if (!originals.containsKey(ace.getKey()) && !ace.getValue().isEmpty()) {
        newACEs.add(new AccessControlEntryImpl(new AccessControlPrincipalDataImpl(ace.getKey()),
            new ArrayList<String>(ace.getValue())));
      }
    }

    return new AccessControlListImpl(newACEs);
  }

  /**
   * Converts a list of ACEs into Map for better handling.
   */
  private Map<String, Set<String>> convertACEsToMap(List<AccessControlEntry> aces) {
    Map<String, Set<String>> result = new HashMap<String, Set<String>>();

    for (AccessControlEntry ace : aces) {
      // don't consider indirect ACEs - we can't change them
      if (!ace.isDirect()) {
        // ignore
        continue;
      }

      // although a principal must not be null, check it
      if ((ace.getPrincipal() == null) || (ace.getPrincipal().getPrincipalId() == null)) {
        // ignore
        continue;
      }

      Set<String> permissions = new HashSet<String>();
      if (ace.getPermissions() != null) {
        permissions.addAll(ace.getPermissions());
      }

      result.put(ace.getPrincipal().getPrincipalId(), permissions);
    }

    return result;
  }

  /**
   * Retrieves the Service Document from the server and caches the repository info objects,
   * collections, links, URI templates, etc.
   */
  @SuppressWarnings("unchecked")
  protected List<RepositoryInfoData> getRepositoriesInternal(String repositoryId) {
    List<RepositoryInfoData> repInfos = new ArrayList<RepositoryInfoData>();

    // retrieve service doc
    UrlBuilder url = new UrlBuilder(getServiceDocURL());
    url.addParameter(Constants.PARAM_REPOSITORY_ID, repositoryId);

    // read and parse
    HttpUtils.Response resp = read(url);
    ServiceDoc serviceDoc = parse(resp.getStream(), ServiceDoc.class);

    // walk through the workspaces
    for (RepositoryWorkspace ws : serviceDoc.getWorkspaces()) {
      if (ws.getId() == null) {
        // found a non-CMIS workspace
        continue;
      }

      for (AtomElement element : ws.getElements()) {
        if (is(NAME_COLLECTION, element)) {
          Map<String, String> colMap = (Map<String, String>) element.getObject();
          addCollection(ws.getId(), colMap.get("collectionType"), colMap.get("href"));
        }
        else if (element.getObject() instanceof AtomLink) {
          addRepositoryLink(ws.getId(), (AtomLink) element.getObject());
        }
        else if (is(NAME_URI_TEMPLATE, element)) {
          Map<String, String> tempMap = (Map<String, String>) element.getObject();
          addTemplate(ws.getId(), tempMap.get("type"), tempMap.get("template"));
        }
        else if (element.getObject() instanceof CmisRepositoryInfoType) {
          repInfos.add(convert((CmisRepositoryInfoType) element.getObject()));
        }
      }
    }

    return repInfos;
  }

  /**
   * Retrieves an object from the server and caches the links.
   */
  protected ObjectData getObjectInternal(String repositoryId, IdentifierType idOrPath,
      String objectIdOrPath, ReturnVersion returnVersion, String filter,
      Boolean includeAllowableActions, IncludeRelationships includeRelationships,
      String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
    ObjectData result = null;

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put(Constants.PARAM_ID, objectIdOrPath);
    parameters.put(Constants.PARAM_PATH, objectIdOrPath);
    parameters.put(Constants.PARAM_RETURN_VERSION, returnVersion);
    parameters.put(Constants.PARAM_FILTER, filter);
    parameters.put(Constants.PARAM_ALLOWABLE_ACTIONS, includeAllowableActions);
    parameters.put(Constants.PARAM_ACL, includeAcl);
    parameters.put(Constants.PARAM_POLICY_IDS, includePolicyIds);
    parameters.put(Constants.PARAM_RELATIONSHIPS, includeRelationships);
    parameters.put(Constants.PARAM_RENDITION_FILTER, renditionFilter);

    String link = loadTemplateLink(repositoryId,
        (idOrPath == IdentifierType.ID ? Constants.TEMPLATE_OBJECT_BY_ID
            : Constants.TEMPLATE_OBJECT_BY_PATH), parameters);
    if (link == null) {
      throw new CmisObjectNotFoundException("Unknown repository!");
    }

    UrlBuilder url = new UrlBuilder(link);
    // workaround for missing template parameter in the CMIS spec
    if ((returnVersion != null) && (returnVersion != ReturnVersion.THIS)) {
      url.addParameter(Constants.PARAM_RETURN_VERSION, returnVersion);
    }

    // read and parse
    HttpUtils.Response resp = read(url);
    AtomEntry entry = parse(resp.getStream(), AtomEntry.class);

    // we expect a CMIS entry
    if (entry.getId() == null) {
      throw new CmisConnectionException("Received Atom entry is not a CMIS entry!");
    }

    // clean up cache
    removeLinks(repositoryId, entry.getId());

    // walk through the entry
    for (AtomElement element : entry.getElements()) {
      if (element.getObject() instanceof AtomLink) {
        addLink(repositoryId, entry.getId(), (AtomLink) element.getObject());
      }
      else if (element.getObject() instanceof CmisObjectType) {
        result = convert((CmisObjectType) element.getObject());
      }
    }

    return result;
  }

  /**
   * Retrieves a type definition.
   */
  protected TypeDefinition getTypeDefinitionInternal(String repositoryId, String typeId) {
    TypeDefinition result = null;

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put(Constants.PARAM_ID, typeId);

    String link = loadTemplateLink(repositoryId, Constants.TEMPLATE_TYPE_BY_ID, parameters);
    if (link == null) {
      throw new CmisObjectNotFoundException("Unknown repository!");
    }

    // read and parse
    HttpUtils.Response resp = read(new UrlBuilder(link));
    AtomEntry entry = parse(resp.getStream(), AtomEntry.class);

    // we expect a CMIS entry
    if (entry.getId() == null) {
      throw new CmisConnectionException("Received Atom entry is not a CMIS entry!");
    }

    // clean up cache
    removeTypeLinks(repositoryId, entry.getId());

    // walk through the entry
    for (AtomElement element : entry.getElements()) {
      if (element.getObject() instanceof AtomLink) {
        addTypeLink(repositoryId, entry.getId(), (AtomLink) element.getObject());
      }
      else if (element.getObject() instanceof CmisTypeDefinitionType) {
        result = convert((CmisTypeDefinitionType) element.getObject());
        break;
      }
    }

    return result;
  }

  /**
   * Updates the ACL of an object.
   */
  protected Acl updateAcl(String repositoryId, String objectId, AccessControlList acl,
      AclPropagation aclPropagation) {

    // find the link
    String link = loadLink(repositoryId, objectId, Constants.REL_ACL, Constants.MEDIATYPE_ACL);

    if (link == null) {
      throw new CmisObjectNotFoundException(
          "Unknown repository or object or ACLs are not supported!");
    }

    UrlBuilder aclUrl = new UrlBuilder(link);
    aclUrl.addParameter(Constants.PARAM_ACL_PROPAGATION, aclPropagation);

    // set up object and writer
    final CmisAccessControlListType aclJaxb = convert(acl);

    // update
    HttpUtils.Response resp = put(aclUrl, Constants.MEDIATYPE_ACL, new HttpUtils.Output() {
      public void write(OutputStream out) throws Exception {
        JaxBHelper.marshal(JaxBHelper.CMIS_OBJECT_FACTORY.createAcl(aclJaxb), out, false);
      }
    });

    // parse new entry
    return parse(resp.getStream(), Acl.class);
  }

}
