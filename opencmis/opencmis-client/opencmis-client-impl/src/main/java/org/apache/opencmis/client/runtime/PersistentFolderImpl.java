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
package org.apache.opencmis.client.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.opencmis.client.api.Ace;
import org.apache.opencmis.client.api.CmisObject;
import org.apache.opencmis.client.api.ContentStream;
import org.apache.opencmis.client.api.Document;
import org.apache.opencmis.client.api.FileableCmisObject;
import org.apache.opencmis.client.api.Folder;
import org.apache.opencmis.client.api.OperationContext;
import org.apache.opencmis.client.api.Policy;
import org.apache.opencmis.client.api.Property;
import org.apache.opencmis.client.api.objecttype.ObjectType;
import org.apache.opencmis.client.api.repository.ObjectFactory;
import org.apache.opencmis.client.api.util.Container;
import org.apache.opencmis.client.api.util.PagingList;
import org.apache.opencmis.client.runtime.util.AbstractPagingList;
import org.apache.opencmis.client.runtime.util.ContainerImpl;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.enums.IncludeRelationships;
import org.apache.opencmis.commons.enums.UnfileObjects;
import org.apache.opencmis.commons.enums.VersioningState;
import org.apache.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.opencmis.commons.provider.AccessControlList;
import org.apache.opencmis.commons.provider.FailedToDeleteData;
import org.apache.opencmis.commons.provider.NavigationService;
import org.apache.opencmis.commons.provider.ObjectData;
import org.apache.opencmis.commons.provider.ObjectInFolderContainer;
import org.apache.opencmis.commons.provider.ObjectInFolderData;
import org.apache.opencmis.commons.provider.ObjectInFolderList;
import org.apache.opencmis.commons.provider.ObjectList;
import org.apache.opencmis.commons.provider.PropertiesData;
import org.apache.opencmis.commons.provider.PropertyData;
import org.apache.opencmis.commons.provider.PropertyStringData;
import org.apache.opencmis.commons.provider.ProviderObjectFactory;

public class PersistentFolderImpl extends AbstractPersistentFilableCmisObject implements Folder {

  /**
   * Constructor.
   */
  public PersistentFolderImpl(PersistentSessionImpl session, ObjectType objectType,
      ObjectData objectData, OperationContext context) {
    initialize(session, objectType, objectData, context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#createDocument(java.util.List,
   * org.apache.opencmis.client.api.ContentStream,
   * org.apache.opencmis.commons.enums.VersioningState, java.util.List, java.util.List,
   * java.util.List, org.apache.opencmis.client.api.OperationContext)
   */
  public Document createDocument(List<Property<?>> properties, ContentStream contentStream,
      VersioningState versioningState, List<Policy> policies, List<Ace> addAces,
      List<Ace> removeAces, OperationContext context) {
    String objectId = getObjectId();

    String newId = getProvider().getObjectService().createDocument(getRepositoryId(),
        SessionUtil.convertProperties(getSession(), properties), objectId,
        SessionUtil.convertContentStream(getSession(), contentStream), versioningState,
        SessionUtil.convertPolicies(policies), SessionUtil.convertAces(getSession(), addAces),
        SessionUtil.convertAces(getSession(), removeAces), null);

    // if no context is provided the object will not be fetched
    if (context == null) {
      return null;
    }

    // get the new object
    CmisObject object = getSession().getObject(newId, context);
    if (!(object instanceof Document)) {
      throw new CmisRuntimeException("Newly created object is not a document! New id: " + newId);
    }

    return (Document) object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.opencmis.client.api.Folder#createDocumentFromSource(org.apache.opencmis.client.api
   * .Document, java.util.List, org.apache.opencmis.commons.enums.VersioningState, java.util.List,
   * java.util.List, java.util.List, org.apache.opencmis.client.api.OperationContext)
   */
  public Document createDocumentFromSource(Document source, List<Property<?>> properties,
      VersioningState versioningState, List<Policy> policies, List<Ace> addAces,
      List<Ace> removeAces, OperationContext context) {
    String objectId = getObjectId();

    if ((source == null) || (source.getId() == null)) {
      throw new IllegalArgumentException("Source document has no id!");
    }

    String newId = getProvider().getObjectService().createDocumentFromSource(getRepositoryId(),
        source.getId(), SessionUtil.convertProperties(getSession(), properties), objectId,
        versioningState, SessionUtil.convertPolicies(policies),
        SessionUtil.convertAces(getSession(), addAces),
        SessionUtil.convertAces(getSession(), removeAces), null);

    // if no context is provided the object will not be fetched
    if (context == null) {
      return null;
    }

    // get the new object
    CmisObject object = getSession().getObject(newId, context);
    if (!(object instanceof Document)) {
      throw new CmisRuntimeException("Newly created object is not a document! New id: " + newId);
    }

    return (Document) object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#createFolder(java.util.List, java.util.List,
   * java.util.List, java.util.List, org.apache.opencmis.client.api.OperationContext)
   */
  public Folder createFolder(List<Property<?>> properties, List<Policy> policies,
      List<Ace> addAces, List<Ace> removeAces, OperationContext context) {
    String objectId = getObjectId();

    String newId = getProvider().getObjectService().createFolder(getRepositoryId(),
        SessionUtil.convertProperties(getSession(), properties), objectId,
        SessionUtil.convertPolicies(policies), SessionUtil.convertAces(getSession(), addAces),
        SessionUtil.convertAces(getSession(), removeAces), null);

    // if no context is provided the object will not be fetched
    if (context == null) {
      return null;
    }

    // get the new object
    CmisObject object = getSession().getObject(newId, context);
    if (!(object instanceof Folder)) {
      throw new CmisRuntimeException("Newly created object is not a folder! New id: " + newId);
    }

    return (Folder) object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#createPolicy(java.util.List, java.util.List,
   * java.util.List, java.util.List, org.apache.opencmis.client.api.OperationContext)
   */
  public Policy createPolicy(List<Property<?>> properties, List<Policy> policies,
      List<Ace> addAces, List<Ace> removeAces, OperationContext context) {
    String objectId = getObjectId();

    String newId = getProvider().getObjectService().createPolicy(getRepositoryId(),
        SessionUtil.convertProperties(getSession(), properties), objectId,
        SessionUtil.convertPolicies(policies), SessionUtil.convertAces(getSession(), addAces),
        SessionUtil.convertAces(getSession(), removeAces), null);

    // if no context is provided the object will not be fetched
    if (context == null) {
      return null;
    }

    // get the new object
    CmisObject object = getSession().getObject(newId, context);
    if (!(object instanceof Policy)) {
      throw new CmisRuntimeException("Newly created object is not a policy! New id: " + newId);
    }

    return (Policy) object;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#deleteTree(boolean,
   * org.apache.opencmis.commons.enums.UnfileObjects, boolean)
   */
  public List<String> deleteTree(boolean allVersions, UnfileObjects unfile,
      boolean continueOnFailure) {
    String repositoryId = getRepositoryId();
    String objectId = getObjectId();

    FailedToDeleteData failed = getProvider().getObjectService().deleteTree(repositoryId, objectId,
        allVersions, unfile, continueOnFailure, null);

    return failed.getIds();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getAllowedChildObjectTypes()
   */
  public List<ObjectType> getAllowedChildObjectTypes() {
    List<ObjectType> result = new ArrayList<ObjectType>();

    List<String> otids = getPropertyMultivalue(PropertyIds.CMIS_ALLOWED_CHILD_OBJECT_TYPE_IDS);
    if (otids == null) {
      return result;
    }

    for (String otid : otids) {
      result.add(getSession().getTypeDefinition(otid));
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getCheckedOutDocs(int)
   */
  public PagingList<Document> getCheckedOutDocs(int itemsPerPage) {
    return getCheckedOutDocs(getSession().getDefaultContext(), itemsPerPage);
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.apache.opencmis.client.api.Folder#getCheckedOutDocs(org.apache.opencmis.client.api.
   * OperationContext, int)
   */
  public PagingList<Document> getCheckedOutDocs(OperationContext context, final int itemsPerPage) {
    if (itemsPerPage < 1) {
      throw new IllegalArgumentException("itemsPerPage must be > 0!");
    }

    final String objectId = getObjectId();
    final NavigationService nagivationService = getProvider().getNavigationService();
    final ObjectFactory objectFactory = getSession().getObjectFactory();
    final OperationContext ctxt = new OperationContextImpl(context);

    return new AbstractPagingList<Document>() {

      @Override
      protected FetchResult fetchPage(int pageNumber) {
        int skipCount = pageNumber * getMaxItemsPerPage();

        // get checked out documents for this folder
        ObjectList checkedOutDocs = nagivationService.getCheckedOutDocs(getRepositoryId(),
            objectId, ctxt.getFilterString(), ctxt.getOrderBy(), ctxt.isIncludeAllowableActions(),
            ctxt.getIncludeRelationships(), ctxt.getRenditionFilterString(), BigInteger
                .valueOf(getMaxItemsPerPage()), BigInteger.valueOf(skipCount), null);

        // convert objects
        List<Document> page = new ArrayList<Document>();
        if (checkedOutDocs.getObjects() != null) {
          for (ObjectData objectData : checkedOutDocs.getObjects()) {
            CmisObject doc = objectFactory.convertObject(objectData, ctxt);
            if (!(doc instanceof Document)) {
              // should not happen...
              continue;
            }

            page.add((Document) doc);
          }
        }

        return new FetchResult(page, checkedOutDocs.getNumItems(), checkedOutDocs.hasMoreItems());
      }

      @Override
      public int getMaxItemsPerPage() {
        return itemsPerPage;
      }
    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getChildren(int)
   */
  public PagingList<CmisObject> getChildren(int itemsPerPage) {
    return getChildren(getSession().getDefaultContext(), itemsPerPage);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.opencmis.client.api.Folder#getChildren(org.apache.opencmis.client.api.OperationContext
   * , int)
   */
  public PagingList<CmisObject> getChildren(OperationContext context, final int itemsPerPage) {
    if (itemsPerPage < 1) {
      throw new IllegalArgumentException("itemsPerPage must be > 0!");
    }

    final String objectId = getObjectId();
    final NavigationService navigationService = getProvider().getNavigationService();
    final ObjectFactory objectFactory = getSession().getObjectFactory();
    final OperationContext ctxt = new OperationContextImpl(context);

    return new AbstractPagingList<CmisObject>() {

      @Override
      protected FetchResult fetchPage(int pageNumber) {
        int skipCount = pageNumber * getMaxItemsPerPage();

        // get the children
        ObjectInFolderList children = navigationService.getChildren(getRepositoryId(), objectId,
            ctxt.getFilterString(), ctxt.getOrderBy(), ctxt.isIncludeAllowableActions(), ctxt
                .getIncludeRelationships(), ctxt.getRenditionFilterString(), ctxt
                .isIncludePathSegments(), BigInteger.valueOf(getMaxItemsPerPage()), BigInteger
                .valueOf(skipCount), null);

        // convert objects
        List<CmisObject> page = new ArrayList<CmisObject>();
        List<ObjectInFolderData> childObjects = children.getObjects();
        if (childObjects != null) {
          for (ObjectInFolderData objectData : childObjects) {
            if (objectData.getObject() != null) {
              page.add(objectFactory.convertObject(objectData.getObject(), ctxt));
            }
          }
        }

        return new FetchResult(page, children.getNumItems(), children.hasMoreItems());
      }

      @Override
      public int getMaxItemsPerPage() {
        return itemsPerPage;
      }
    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getDescendants(int)
   */
  public List<Container<FileableCmisObject>> getDescendants(int depth) {
    return getDescendants(depth, getSession().getDefaultContext());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getDescendants(int,
   * org.apache.opencmis.client.api.OperationContext)
   */
  public List<Container<FileableCmisObject>> getDescendants(int depth, OperationContext context) {
    String objectId = getObjectId();

    // get the descendants
    List<ObjectInFolderContainer> providerContainerList = getProvider().getNavigationService()
        .getDescendants(getRepositoryId(), objectId, BigInteger.valueOf(depth),
            context.getFilterString(), context.isIncludeAllowableActions(),
            context.getIncludeRelationships(), context.getRenditionFilterString(),
            context.isIncludePathSegments(), null);

    return convertProviderContainer(providerContainerList, context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getFolderTree(int)
   */
  public List<Container<FileableCmisObject>> getFolderTree(int depth) {
    return getFolderTree(depth, getSession().getDefaultContext());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getFolderTree(int,
   * org.apache.opencmis.client.api.OperationContext)
   */
  public List<Container<FileableCmisObject>> getFolderTree(int depth, OperationContext context) {
    String objectId = getObjectId();

    // get the folder tree
    List<ObjectInFolderContainer> providerContainerList = getProvider().getNavigationService()
        .getFolderTree(getRepositoryId(), objectId, BigInteger.valueOf(depth),
            context.getFilterString(), context.isIncludeAllowableActions(),
            context.getIncludeRelationships(), context.getRenditionFilterString(),
            context.isIncludePathSegments(), null);

    return convertProviderContainer(providerContainerList, context);
  }

  /**
   * Converts a provider container into an API container.
   */
  private List<Container<FileableCmisObject>> convertProviderContainer(
      List<ObjectInFolderContainer> providerContainerList, OperationContext context) {
    if (providerContainerList == null) {
      return null;
    }

    ObjectFactory of = getSession().getObjectFactory();

    List<Container<FileableCmisObject>> result = new ArrayList<Container<FileableCmisObject>>();
    for (ObjectInFolderContainer oifc : providerContainerList) {
      if ((oifc.getObject() == null) || (oifc.getObject().getObject() == null)) {
        // shouldn't happen ...
        continue;
      }

      // convert the object
      CmisObject object = of.convertObject(oifc.getObject().getObject(), context);
      if (!(object instanceof FileableCmisObject)) {
        // the repository must not return objects that are not fileable, but you never know...
        continue;
      }

      // convert the children
      List<Container<FileableCmisObject>> children = convertProviderContainer(oifc.getChildren(),
          context);

      // add both to current container
      result.add(new ContainerImpl<FileableCmisObject>((FileableCmisObject) object, children));
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#isRootFolder()
   */
  public boolean isRootFolder() {
    String objectId = getObjectId();
    String rootFolderId = getSession().getRepositoryInfo().getRootFolderId();

    return objectId.equals(rootFolderId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getFolderParent()
   */
  public Folder getFolderParent() {
    if (isRootFolder()) {
      return null;
    }

    List<Folder> parents = super.getParents();
    if ((parents == null) || (parents.isEmpty())) {
      return null;
    }

    return parents.get(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.api.Folder#getPath()
   */
  public String getPath() {
    // get the path property
    String path = getPropertyValue(PropertyIds.CMIS_PATH);

    // if the path property isn't set, get it
    if (path == null) {
      String objectId = getObjectId();
      ObjectData objectData = getProvider().getObjectService().getObject(getRepositoryId(),
          objectId, PropertyIds.CMIS_PATH, false, IncludeRelationships.NONE, "cmis:none", false,
          false, null);

      if ((objectData.getProperties() != null)
          && (objectData.getProperties().getProperties() != null)) {
        PropertyData<?> pathProperty = objectData.getProperties().getProperties().get(
            PropertyIds.CMIS_PATH);

        if (pathProperty instanceof PropertyStringData) {
          path = ((PropertyStringData) pathProperty).getFirstValue();
        }
      }
    }

    // we still don't know the path ... it's not a CMIS compliant repository
    if (path == null) {
      throw new CmisRuntimeException("Repository didn't return " + PropertyIds.CMIS_PATH + "!");
    }

    return path;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.opencmis.client.runtime.AbstractPersistentFilableCmisObject#getPaths()
   */
  @Override
  public List<String> getPaths() {
    return Collections.singletonList(getPath());
  }

  /**
   * Create folder in backend
   * 
   * @param parent
   * @param properties
   * @param policies
   * @param addACEs
   * @param removeACEs
   */
  public void create(Folder parent, List<Property<?>> properties, List<Policy> policies,
      List<Ace> addAce, List<Ace> removeAce) {

    String repositoryId = getRepositoryId();
    String parentFolderId = parent.getId();
    PropertiesData pd = this.convertToPropertiesData(properties);
    List<String> pol = this.convertToPoliciesData(policies);
    AccessControlList addAcl = SessionUtil.convertAces(getSession(), addAce);
    AccessControlList removeAcl = SessionUtil.convertAces(getSession(), removeAce);

    String objectId = getProvider().getObjectService().createFolder(repositoryId, pd,
        parentFolderId, pol, addAcl, removeAcl, null);
    ObjectData newObjectData = getProvider().getObjectService().getObject(repositoryId, objectId,
        null, false, IncludeRelationships.NONE, null, true, true, null);

    // getSession().getCache().put(this);
  }

  private List<String> convertToPoliciesData(List<Policy> policies) {
    List<String> pList = null;

    if (policies != null) {
      pList = new ArrayList<String>();
      for (Policy pol : policies) {
        pList.add(pol.getId());
      }
    }
    return pList;
  }

  @SuppressWarnings("unchecked")
  private PropertiesData convertToPropertiesData(List<Property<?>> origProperties) {
    ProviderObjectFactory of = getProvider().getObjectFactory();

    List<PropertyData<?>> convProperties = new ArrayList<PropertyData<?>>();
    PropertyData<?> convProperty = null;

    convProperties.add(of.createPropertyStringData(PropertyIds.CMIS_NAME, "testfolder"));
    convProperties.add(of.createPropertyIdData(PropertyIds.CMIS_OBJECT_TYPE_ID, "cmis_Folder"));

    for (Property<?> origProperty : origProperties) {

      switch (origProperty.getType()) {
      case BOOLEAN:
        Property<Boolean> pb = (Property<Boolean>) origProperty;
        convProperty = of.createPropertyBooleanData(pb.getId(), pb.getValue());
        break;
      case DATETIME:
        Property<GregorianCalendar> pg = (Property<GregorianCalendar>) origProperty;
        convProperty = of.createPropertyDateTimeData(pg.getId(), pg.getValue());
        break;
      case DECIMAL:
        Property<BigDecimal> pd = (Property<BigDecimal>) origProperty;
        convProperty = of.createPropertyDecimalData(pd.getId(), pd.getValue());
        break;
      case HTML:
        Property<String> ph = (Property<String>) origProperty;
        convProperty = of.createPropertyHtmlData(ph.getId(), ph.getValue());
        break;
      case ID:
        Property<String> pi = (Property<String>) origProperty;
        convProperty = of.createPropertyIdData(pi.getId(), pi.getValue());
        break;
      case INTEGER:
        Property<BigInteger> pn = (Property<BigInteger>) origProperty;
        convProperty = of.createPropertyIntegerData(pn.getId(), pn.getValue());
        break;
      case STRING:
        Property<String> ps = (Property<String>) origProperty;
        convProperty = of.createPropertyStringData(ps.getId(), ps.getValue());
        break;
      case URI:
        Property<String> pu = (Property<String>) origProperty;
        convProperty = of.createPropertyUriData(pu.getId(), pu.getValue());
        break;
      default:
        throw new CmisRuntimeException("unsupported property type" + origProperty.getType());
      }
      convProperties.add(convProperty);
    }

    PropertiesData pd = of.createPropertiesData(convProperties);

    return pd;
  }
}
