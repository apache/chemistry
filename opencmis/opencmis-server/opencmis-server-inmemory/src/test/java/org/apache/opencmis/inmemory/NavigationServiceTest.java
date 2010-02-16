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
package org.apache.opencmis.inmemory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.opencmis.client.provider.factory.CmisProviderFactory;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.SessionParameter;
import org.apache.opencmis.commons.enums.IncludeRelationships;
import org.apache.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.opencmis.commons.provider.CmisProvider;
import org.apache.opencmis.commons.provider.NavigationService;
import org.apache.opencmis.commons.provider.ObjectData;
import org.apache.opencmis.commons.provider.ObjectInFolderContainer;
import org.apache.opencmis.commons.provider.ObjectInFolderData;
import org.apache.opencmis.commons.provider.ObjectInFolderList;
import org.apache.opencmis.commons.provider.ObjectService;
import org.apache.opencmis.commons.provider.PropertiesData;
import org.apache.opencmis.commons.provider.PropertyData;
import org.apache.opencmis.commons.provider.ProviderObjectFactory;
import org.apache.opencmis.commons.provider.RepositoryInfoData;
import org.apache.opencmis.commons.provider.RepositoryService;
import org.apache.opencmis.inmemory.ConfigConstants;
import org.apache.opencmis.inmemory.ConfigMap;
import org.apache.opencmis.inmemory.MapConfigReader;
import org.apache.opencmis.inmemory.RepositoryServiceTest.UnitTestRepositoryInfo;
import org.apache.opencmis.inmemory.clientprovider.CmisInMemorySpi;
import org.apache.opencmis.inmemory.server.RuntimeContext;
import org.apache.opencmis.inmemory.storedobj.impl.SessionConfigReader;
import org.apache.opencmis.inmemory.types.InMemoryFolderTypeDefinition;
import org.apache.opencmis.util.repository.ObjectGenerator;


/**
 * @author Jens
 */
public class NavigationServiceTest extends TestCase {
  private static Log log = LogFactory.getLog(NavigationServiceTest.class);
  private static final String REPOSITORY_ID = "UnitTestRepository";
  private ProviderObjectFactory fFactory;
  private CmisProvider fProvider;
  private static final int NUM_ROOT_FOLDERS = 10;
  private String fLevel1FolderId;
  String fRootFolderId;
  private String fRepositoryId;

  protected void setUp() throws Exception {

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(SessionParameter.BINDING_SPI_CLASS, CmisProviderFactory.BINDING_SPI_INMEMORY);
    // attach repository info to the session:
    parameters
        .put(ConfigConstants.REPOSITORY_INFO_CREATOR_CLASS, UnitTestRepositoryInfo.class.getName());
    parameters.put(ConfigConstants.REPOSITORY_ID, REPOSITORY_ID);
    
    // get factory and create provider
    CmisProviderFactory factory = CmisProviderFactory.newInstance();
    fProvider = factory.createCmisProvider(parameters);
    assertNotNull(fProvider);
    fFactory = fProvider.getObjectFactory();
    RepositoryService repSvc = fProvider.getRepositoryService();
    RepositoryInfoData rep = repSvc.getRepositoryInfo(REPOSITORY_ID, null);
    fRootFolderId = rep.getRootFolderId();
    fRepositoryId = rep.getRepositoryId();
    // Attach the CallContext to a thread local context that can be accessed from everywhere
    ConfigMap cfgReader = new MapConfigReader(parameters);  
    RuntimeContext.getRuntimeConfig().attachCfg(cfgReader);
  }

  protected void tearDown() throws Exception {
  }

  public void testGetChildren() {
    log.info("starting testGetChildren() ...");
    createLevel1Folders();
    NavigationService navSvc = fProvider.getNavigationService();

    log.info("test getting all objects with getChildren");
    BigInteger maxItems = BigInteger.valueOf(NUM_ROOT_FOLDERS *2);
    BigInteger skipCount = BigInteger.valueOf(0);
    ObjectInFolderList result = navSvc.getChildren(fRepositoryId, fRootFolderId, "*", null, false,
        IncludeRelationships.NONE, null, true, maxItems, skipCount, null);
    List<ObjectInFolderData> folders = result.getObjects();
    log.info(" found " + folders.size() + " folders in getChildren()");
    for (ObjectInFolderData folder : folders) {
      log.info("   found folder id " + folder.getObject().getId() + " path segment "
          + folder.getPathSegment());
    }
    assertEquals(NUM_ROOT_FOLDERS, folders.size());
    
    log.info("test paging with getChildren");
    maxItems = BigInteger.valueOf(3);
    skipCount = BigInteger.valueOf(3);    
    result = navSvc.getChildren(fRepositoryId, fRootFolderId, "*", null, false,
        IncludeRelationships.NONE, null, true, maxItems, skipCount, null);
    folders = result.getObjects();
    log.info(" found " + folders.size() + " folders in getChildren()");
    for (ObjectInFolderData folder : folders) {
      log.info("   found folder id " + folder.getObject().getId() + " path segment "
          + folder.getPathSegment());
    }
    assertEquals(3, folders.size());
    assertEquals("Folder 3", folders.get(0).getPathSegment());
    log.info("... testGetChildren() finished.");
  }

  public void testGetFolderTree() {
    log.info("starting testGetFolderTree() ...");    
    createFolderHierachy(3, 5);
    //createLevel1Folders();
    NavigationService navSvc = fProvider.getNavigationService();

    log.info("test getting all objects with getFolderTree");
    BigInteger depth = BigInteger.valueOf(-1);
    Boolean includePathSegments = true;
    String propertyFilter = "*";
    String renditionFilter = null;
    Boolean includeAllowableActions = false;
    String objectId = fRootFolderId;
    
    List<ObjectInFolderContainer> tree = navSvc.getFolderTree(fRepositoryId, objectId,
        depth, propertyFilter, includeAllowableActions, IncludeRelationships.NONE, renditionFilter,
        includePathSegments, null);    
    
    log.info("Descendants for object id " + objectId + " are: ");
    for (ObjectInFolderContainer folder : tree) {
      logFolderContainer(folder, 0);
    }
    
    log.info("... testGetFolderTree() finished.");
  }

  private void logFolderContainer(ObjectInFolderContainer folder, int depth) {
    StringBuilder prefix = new StringBuilder();
    for (int i=0; i<depth; i++)
      prefix.append("   ");
    
    log.info(prefix + "name: " + folder.getObject().getPathSegment());
    List<ObjectInFolderContainer> children = folder.getChildren();
    if (null != children) {
      for (ObjectInFolderContainer child: children) {
        logFolderContainer(child, depth+1);
      } 
    }
  }

  public void testGetDescendants() {
    log.info("starting testGetDescendants() ...");
    final int numLevels = 3;
    final int childrenPerLevel = 3;
    int objCount = createFolderHierachy(numLevels, childrenPerLevel);
    NavigationService navSvc = fProvider.getNavigationService();

    log.info("test getting all objects with getDescendants");
    List<ObjectInFolderContainer> result = navSvc.getDescendants(fRepositoryId, fRootFolderId, BigInteger.valueOf(-1),
        "*", Boolean.TRUE, IncludeRelationships.NONE, null, Boolean.TRUE, null);
    
    for (ObjectInFolderContainer obj: result) {
      log.info("   found folder id " + obj.getObject().getObject().getId() + " path segment "
          + obj.getObject().getPathSegment());
    }
    int sizeOfDescs = getSizeOfDescendants(result);
    assertEquals(objCount, sizeOfDescs);

    log.info("test getting one level with getDescendants");
    result = navSvc.getDescendants(fRepositoryId, fRootFolderId, BigInteger.valueOf(1),
        "*", Boolean.TRUE, IncludeRelationships.NONE, null, Boolean.TRUE, null);
    
    for (ObjectInFolderContainer obj: result) {
      log.info("   found folder id " + obj.getObject().getObject().getId() + " path segment "
          + obj.getObject().getPathSegment());
    }
    sizeOfDescs = getSizeOfDescendants(result);
    assertEquals(childrenPerLevel, sizeOfDescs);
    
    log.info("test getting two levels with getDescendants");
    result = navSvc.getDescendants(fRepositoryId, fRootFolderId, BigInteger.valueOf(2),
        "*", Boolean.TRUE, IncludeRelationships.NONE, null, Boolean.TRUE, null);
    
    for (ObjectInFolderContainer obj: result) {
      log.info("   found folder id " + obj.getObject().getObject().getId() + " path segment "
          + obj.getObject().getPathSegment());
    }
    sizeOfDescs = getSizeOfDescendants(result);
    assertEquals(childrenPerLevel*childrenPerLevel+childrenPerLevel, sizeOfDescs);

    log.info("... testGetDescendants() finished.");
  }
  
  public void testGetFolderParent() {
    log.info("starting testGetFolderParent() ...");
    createLevel1Folders();
    NavigationService navSvc = fProvider.getNavigationService();
    String folderId = fLevel1FolderId;
    
    ObjectData result = navSvc.getFolderParent(fRepositoryId, folderId, null, null);
    log.info(" found parent for id \'" + folderId + "\' is \'" + result.getId() + "\'");
    assertEquals(fRootFolderId, result.getId()); // should be root folder 
    
    folderId = fRootFolderId;
    try {
      result = navSvc.getFolderParent(fRepositoryId, folderId, null, null);
      log.info(" found parent for id " + folderId + " is " + result.getId());
      fail("Should not be possible to get parent for root folder");
    } catch (Exception e) {
      assertEquals(CmisInvalidArgumentException.class, e.getClass());
      log.info(" getParent() for root folder raised expected exception");      
    }
    log.info("... testGetFolderParent() finished.");
  }

  private int getSizeOfDescendants(List<ObjectInFolderContainer> objs) {
    int sum = objs.size();
    if (null != objs) {
      for (ObjectInFolderContainer obj: objs) {
        if (null != obj.getChildren())
          sum += getSizeOfDescendants(obj.getChildren());
      }
    }
    return sum;
  }
  
  private void createLevel1Folders() {
    ObjectService objSvc = fProvider.getObjectService();
    for (int i = 0; i < NUM_ROOT_FOLDERS; i++) {
      List<PropertyData<?>> properties = new ArrayList<PropertyData<?>>();
      properties.add(fFactory.createPropertyIdData(PropertyIds.CMIS_NAME, "Folder " + i));
      properties.add(fFactory.createPropertyIdData(PropertyIds.CMIS_OBJECT_TYPE_ID,
          InMemoryFolderTypeDefinition.getRootFolderType().getId()));
      PropertiesData props = fFactory.createPropertiesData(properties);      
      String id = objSvc.createFolder(fRepositoryId, props, fRootFolderId, null, null, null, null);
      if (i==3) // store one
        fLevel1FolderId = id;
    }
  }
  
  private int createFolderHierachy(int levels, int childrenPerLevel) {
    NavigationService navSvc = fProvider.getNavigationService();
    ObjectService objSvc = fProvider.getObjectService();

    ObjectGenerator gen = new ObjectGenerator(fFactory, navSvc, objSvc, fRepositoryId);
    gen.createFolderHierachy(levels, childrenPerLevel, fRootFolderId);
    int objCount = gen.getObjectsInTotal();
    return objCount;
  }
  
}
