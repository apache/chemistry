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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.apache.opencmis.client.provider.factory.CmisProviderFactory;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.SessionParameter;
import org.apache.opencmis.commons.api.DocumentTypeDefinition;
import org.apache.opencmis.commons.api.PropertyDefinition;
import org.apache.opencmis.commons.api.TypeDefinition;
import org.apache.opencmis.commons.enums.BaseObjectTypeIds;
import org.apache.opencmis.commons.enums.IncludeRelationships;
import org.apache.opencmis.commons.enums.VersioningState;
import org.apache.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.opencmis.commons.exceptions.CmisUpdateConflictException;
import org.apache.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.opencmis.commons.provider.CmisProvider;
import org.apache.opencmis.commons.provider.ContentStreamData;
import org.apache.opencmis.commons.provider.Holder;
import org.apache.opencmis.commons.provider.NavigationService;
import org.apache.opencmis.commons.provider.ObjectData;
import org.apache.opencmis.commons.provider.ObjectList;
import org.apache.opencmis.commons.provider.ObjectService;
import org.apache.opencmis.commons.provider.PropertiesData;
import org.apache.opencmis.commons.provider.PropertyBooleanData;
import org.apache.opencmis.commons.provider.PropertyData;
import org.apache.opencmis.commons.provider.PropertyIdData;
import org.apache.opencmis.commons.provider.PropertyStringData;
import org.apache.opencmis.commons.provider.ProviderObjectFactory;
import org.apache.opencmis.commons.provider.RepositoryInfoData;
import org.apache.opencmis.commons.provider.RepositoryService;
import org.apache.opencmis.commons.provider.VersioningService;
import org.apache.opencmis.inmemory.RepositoryServiceTest.UnitTestRepositoryInfo;
import org.apache.opencmis.inmemory.server.RuntimeContext;
import org.apache.opencmis.inmemory.types.InMemoryDocumentTypeDefinition;
import org.apache.opencmis.inmemory.types.InMemoryFolderTypeDefinition;
import org.apache.opencmis.inmemory.types.PropertyCreationHelper;

public class VersioningTest extends TestCase {
  private static Log log = LogFactory.getLog(ObjectServiceTest.class);
  private static final String REPOSITORY_ID = "UnitTestRepository";
  private static final String PROP_VALUE = "Mickey Mouse";
  private static final String PROP_VALUE_NEW = "Donald Duck";
  private static final String PROP_NAME = "My Versioned Document";
  private static final String TEST_USER = "TestUser";
  private static final String TEST_USER_2 = "OtherUser";
  
  private ProviderObjectFactory fFactory;
  private CmisProvider fProvider;
  ObjectService fObjSvc;
  VersioningService fVerSvc;
  RepositoryService fRepSvc;
  NavigationService fNavSvc;
  String fRepositoryId;
  String fRootFolderId;
  ObjectCreator fCreator;
  
  @Before
  public void setUp() throws Exception {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(SessionParameter.BINDING_SPI_CLASS, CmisProviderFactory.BINDING_SPI_INMEMORY);
    // attach repository info to the session:
    parameters
        .put(ConfigConstants.REPOSITORY_INFO_CREATOR_CLASS, UnitTestRepositoryInfo.class.getName());

    parameters.put(ConfigConstants.TYPE_CREATOR_CLASS, VersionTestTypeSystemCreator.class.getName());
    parameters.put(ConfigConstants.REPOSITORY_ID, REPOSITORY_ID);
  
    // get factory and create provider
    CmisProviderFactory factory = CmisProviderFactory.newInstance();
    fProvider = factory.createCmisProvider(parameters);
    assertNotNull(fProvider);
    fFactory = fProvider.getObjectFactory();
    fRepSvc = fProvider.getRepositoryService();
    RepositoryInfoData rep = fRepSvc.getRepositoryInfo(REPOSITORY_ID, null);
    fObjSvc = fProvider.getObjectService();
    fVerSvc = fProvider.getVersioningService();
    fNavSvc = fProvider.getNavigationService();
    fRepositoryId = rep.getRepositoryId();
    fRootFolderId = rep.getRootFolderId();
    fCreator = new ObjectCreator(fFactory, fObjSvc, fRepositoryId);
    setRuntimeContext(TEST_USER);
  }

  @After
  public void tearDown() throws Exception {
  }

  private void setRuntimeContext(String user) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(ConfigConstants.USERNAME, user);

    // Attach the CallContext to a thread local context that can be accessed from everywhere
    ConfigMap cfgReader = new MapConfigReader(parameters);  
    RuntimeContext.getRuntimeConfig().attachCfg(cfgReader); 
  }
    
  public void testCreateVersionedDocumentMinor() {
    testCreateVersionedDocument(VersioningState.MINOR);
  }
  
  public void testCreateVersionedDocumentCheckedOut() {
    testCreateVersionedDocument(VersioningState.CHECKEDOUT);
  }
  
  public void testCreateVersionedDocumentNone() {
    try {
      testCreateVersionedDocument(VersioningState.NONE);
      fail("creating a document of a versionable type with state VersioningState.NONE should fail.");
    } catch (Exception e) {     
      assertEquals(CmisConstraintException.class, e.getClass());
    }
  }

  public void testCheckOutBasic() {
    String verId = createDocument(PROP_NAME, fRootFolderId, VersioningState.MAJOR);

    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );

    assertFalse(isCheckedOut(docId));

    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    // test that object is checked out and that all properties are set correctly
    PropertiesData props = fObjSvc.getProperties(fRepositoryId, pwcId, "*", null);
    String changeToken = (String) props.getProperties().get(PropertyIds.CMIS_CHANGE_TOKEN).getFirstValue();
    checkVersionProperties(pwcId, VersioningState.CHECKEDOUT, props.getProperties());
    
    // Test that a second checkout is not possible
    try {
      fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
      fail("Checking out a document that is already checked-out should fail.");
    } catch (Exception e) {
      assertTrue(e instanceof CmisUpdateConflictException);
    }
    // version and version series should be checked out now
    assertTrue(isCheckedOut(docId));
    assertTrue(isCheckedOut(pwcId));

    // Set a new content and modify property
    ContentStreamData altContent = fCreator.createAlternateContent();
    idHolder = new Holder<String>(pwcId);
    Holder<String> tokenHolder = new Holder<String>(changeToken);
    fObjSvc.setContentStream(fRepositoryId, idHolder, true, tokenHolder, altContent, null);
    fCreator.updateProperty(idHolder.getValue(), VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);

    // Test that a check-in as same user is possible
    String checkinComment = "Checkin without content and properties.";
    fVerSvc.checkIn(fRepositoryId, idHolder, true, null, null, checkinComment, null, null, null,
        null);
    // Neither the version nor the version series should be checked out any longer:
    assertFalse(isCheckedOut(idHolder.getValue()));
    assertFalse(isCheckedOut(docId));
    ContentStreamData retrievedContent = fObjSvc.getContentStream(fRepositoryId, idHolder
        .getValue(), null, BigInteger.valueOf(-1) /* offset */,
        BigInteger.valueOf(-1) /* length */, null);
    assertTrue(fCreator.verifyContent(fCreator.createAlternateContent(), retrievedContent));
    assertTrue(fCreator.verifyProperty(idHolder.getValue(), VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW));

    List<ObjectData> allVersions = fVerSvc.getAllVersions(fRepositoryId, docId, "*", false, null);
    assertEquals(2, allVersions.size());
  }
  
  public void testCheckInWithContent() {
    String verId = createDocument(PROP_NAME, fRootFolderId, VersioningState.MAJOR);

    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );

    assertFalse(isCheckedOut(docId));

    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    
    ContentStreamData altContent = fCreator.createAlternateContent();
    PropertiesData newProps = fCreator.getUpdatePropertyList(VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);
    idHolder = new Holder<String>(pwcId);
    assertTrue(isCheckedOut(docId));
    assertTrue(isCheckedOut(pwcId));

    // Test check-in and pass content and properties
    String checkinComment = "Checkin with content and properties.";
    fVerSvc.checkIn(fRepositoryId, idHolder, true, newProps, altContent, checkinComment, null, null, null,
        null);
    // Neither the version nor the version series should be checked out any longer:
    assertFalse(isCheckedOut(idHolder.getValue()));
    assertFalse(isCheckedOut(docId));
    ContentStreamData retrievedContent = fObjSvc.getContentStream(fRepositoryId, idHolder
        .getValue(), null, BigInteger.valueOf(-1) /* offset */,
        BigInteger.valueOf(-1) /* length */, null);

    // New content and property should be set
    assertTrue(fCreator.verifyContent(fCreator.createAlternateContent(), retrievedContent));    
    assertTrue(fCreator.verifyProperty(idHolder.getValue(), VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW));
  }
  
  public void testCheckOutAndOtherUser() {
    String verId = createDocument(PROP_NAME, fRootFolderId, VersioningState.MAJOR);
    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );
    assertFalse(isCheckedOut(docId));
    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    
    // Test that a checkin as another user is not possible
    setRuntimeContext(TEST_USER_2);
    try {      
      fVerSvc.checkIn(fRepositoryId, idHolder, true, null, null, "My Comment", null, null, null, null);
      fail("Checking in a document as another user should fail.");
    } catch (Exception e) {
      assertTrue(e instanceof CmisUpdateConflictException);
    }

    // Test that a cancel checkout as another user is not possible
    try {      
      fVerSvc.cancelCheckOut(fRepositoryId, pwcId, null);
      fail("Checking in a document as another user should fail.");
    } catch (Exception e) {
      assertTrue(e instanceof CmisUpdateConflictException);
    }
    
    // Test that an updateProperties as another user is not possible
    try {      
      fCreator.updateProperty(pwcId, VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);
      fail("updateProperty in a document as another user should fail.");
    } catch (Exception e) {
      assertTrue(e instanceof CmisUpdateConflictException);
    }

    ContentStreamData altContent = fCreator.createAlternateContent();
    Holder<String> pwcHolder = new Holder<String>(pwcId);
    try {      
      fObjSvc.setContentStream(fRepositoryId, pwcHolder, true, null, altContent, null);
      fail("setContentStream in a document as another user should fail.");
    } catch (Exception e) {
      assertTrue(e instanceof CmisUpdateConflictException);
    }

    setRuntimeContext(TEST_USER);
    // Test that a check-in as same user is possible
    fVerSvc.checkIn(fRepositoryId, pwcHolder, true, null, null, "testCheckOutAndOtherUser", null, null, null,null);
    
    // Because nothing was changed we should have a new version with identical content
    ContentStreamData retrievedContent = fObjSvc.getContentStream(fRepositoryId, pwcHolder.getValue(),
        null, BigInteger.valueOf(-1) /* offset */, BigInteger.valueOf(-1) /* length */, null);
    assertTrue(fCreator.verifyContent(retrievedContent, fCreator.createContent()));    
    assertTrue(fCreator.verifyProperty(idHolder.getValue(), VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE));  
  }
  
  public void testCancelCheckout() {
    String verId = createDocument(PROP_NAME, fRootFolderId, VersioningState.MAJOR);
    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String idOfLastVersion = version.getId();
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );
    assertFalse(isCheckedOut(docId));
    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    
    // Set a new content and modify property
    PropertiesData props = fObjSvc.getProperties(fRepositoryId, pwcId, "*", null);
    String changeToken = (String) props.getProperties().get(PropertyIds.CMIS_CHANGE_TOKEN).getFirstValue();
    ContentStreamData altContent = fCreator.createAlternateContent();
    idHolder = new Holder<String>(pwcId);
    Holder<String> tokenHolder = new Holder<String>(changeToken);
    fObjSvc.setContentStream(fRepositoryId, idHolder, true, tokenHolder, altContent, null);
    fCreator.updateProperty(idHolder.getValue(), VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);
    
    // cancel checkout 
    fVerSvc.cancelCheckOut(fRepositoryId, pwcId, null);
    try {
      // Verify that pwc no longer exists
      fObjSvc.getObject(fRepositoryId, pwcId, "*", false, IncludeRelationships.NONE,
          null, false, false, null);
      fail("Getting pwc after cancel checkout should fail.");
    } catch (Exception e1) {
    }
    
    // verify that the old content and properties are still valid
    assertTrue(fCreator.verifyProperty(docId, VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE));      
    ContentStreamData retrievedContent = fObjSvc.getContentStream(fRepositoryId, idOfLastVersion,
        null, BigInteger.valueOf(-1) /* offset */, BigInteger.valueOf(-1) /* length */, null);
    assertTrue(fCreator.verifyContent(retrievedContent, fCreator.createContent()));    
  }
  
  public void testGetPropertiesOfLatestVersion() {
    VersioningState versioningState = VersioningState.MAJOR;
    String verId = createDocument(PROP_NAME, fRootFolderId, versioningState);
    getDocument(verId);
    
    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );
    
    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    
    ContentStreamData altContent = fCreator.createAlternateContent();
    PropertiesData newProps = fCreator.getUpdatePropertyList(VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);
    idHolder = new Holder<String>(pwcId);
    assertTrue(isCheckedOut(docId));
    assertTrue(isCheckedOut(pwcId));

    // Test check-in and pass content and properties
    String checkinComment = "Checkin with content and properties.";
    fVerSvc.checkIn(fRepositoryId, idHolder, true, newProps, altContent, checkinComment, null, null, null,
        null);
    
    PropertiesData latest = fVerSvc.getPropertiesOfLatestVersion(fRepositoryId, docId, true, "*", null);
    assertNotNull(latest);
    
    checkVersionProperties(verId, versioningState, latest.getProperties());        
  }
  
  public void testGetLatestVersion() {
    VersioningState versioningState = VersioningState.MINOR;
    String verId = createDocument(PROP_NAME, fRootFolderId, versioningState);
    getDocument(verId);
    
    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );
    
    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verId); // or should this be version series?
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    String pwcId = idHolder.getValue();
    
    ContentStreamData altContent = fCreator.createAlternateContent();
    PropertiesData newProps = fCreator.getUpdatePropertyList(VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE_NEW);
    idHolder = new Holder<String>(pwcId);
    assertTrue(isCheckedOut(docId));
    assertTrue(isCheckedOut(pwcId));

    // Test check-in and pass content and properties
    String checkinComment = "Checkin with content and properties.";
    fVerSvc.checkIn(fRepositoryId, idHolder, true, newProps, altContent, checkinComment, null, null, null,
        null);
    
    // get latest major version
    versioningState = VersioningState.MAJOR;
    boolean isMajor = true;
    ObjectData objData = fVerSvc.getObjectOfLatestVersion(fRepositoryId, docId, isMajor, "*", false, IncludeRelationships.NONE, null, false, false, null);
    checkVersionProperties(verId, versioningState, objData.getProperties().getProperties());        
    ContentStreamData retrievedContent = fObjSvc.getContentStream(fRepositoryId, objData.getId(),
        null, BigInteger.valueOf(-1) /* offset */, BigInteger.valueOf(-1) /* length */, null);
    assertTrue(fCreator.verifyContent(retrievedContent, fCreator.createAlternateContent()));    

    // get latest non-major version, must be the same as before
    versioningState = VersioningState.MAJOR;
    isMajor = false;
    objData = fVerSvc.getObjectOfLatestVersion(fRepositoryId, docId, isMajor, "*", false, IncludeRelationships.NONE, null, false, false, null);
    checkVersionProperties(verId, versioningState, objData.getProperties().getProperties());        
    retrievedContent = fObjSvc.getContentStream(fRepositoryId, objData.getId(),
        null, BigInteger.valueOf(-1) /* offset */, BigInteger.valueOf(-1) /* length */, null);
    assertTrue(fCreator.verifyContent(retrievedContent, fCreator.createAlternateContent()));        
   }
  
  public void testGetCheckedOutDocuments() {
    // create two folders with each having two documents, one of them being checked out
    final int count = 2;
    String[] folderIds = createLevel1Folders();
    String[] verSeriesIds = new String[folderIds.length * count];
    for (int i=0; i<folderIds.length; i++) {
      for (int j=0; j<count; j++) {
        String verId = createDocument("MyDoc"+j, folderIds[i], VersioningState.MAJOR);
        ObjectData od = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
        verSeriesIds[i*folderIds.length + j] = getVersionSeriesId(verId, od.getProperties().getProperties());
      }
    }
    // checkout first in each folder
    Holder<Boolean> contentCopied = new Holder<Boolean>();
    Holder<String> idHolder = new Holder<String>(verSeriesIds[0]);
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    idHolder = new Holder<String>(verSeriesIds[2]);
    fVerSvc.checkOut(fRepositoryId, idHolder, null, contentCopied);
    
    // must be one in first folder
    ObjectList checkedOutDocuments = fNavSvc.getCheckedOutDocs(fRepositoryId, folderIds[0], "*",
        null, false, IncludeRelationships.NONE, null, BigInteger.valueOf(-1), BigInteger
            .valueOf(-1), null);
    assertEquals(1, checkedOutDocuments.getNumItems().longValue());
    assertEquals(1, checkedOutDocuments.getObjects().size());
    
    // must be one in second folder
    checkedOutDocuments = fNavSvc.getCheckedOutDocs(fRepositoryId, folderIds[1], "*",
        null, false, IncludeRelationships.NONE, null, BigInteger.valueOf(-1), BigInteger
            .valueOf(-1), null);
    assertEquals(1, checkedOutDocuments.getNumItems().longValue());
    assertEquals(1, checkedOutDocuments.getObjects().size());
    
    // must be two in repository
    checkedOutDocuments = fNavSvc.getCheckedOutDocs(fRepositoryId, null, "*",
        null, false, IncludeRelationships.NONE, null, BigInteger.valueOf(-1), BigInteger
            .valueOf(-1), null);
    assertEquals(2, checkedOutDocuments.getNumItems().longValue());    
    assertEquals(2, checkedOutDocuments.getObjects().size());
  }
  
  private String[] createLevel1Folders() {
    ObjectService objSvc = fProvider.getObjectService();
    final int num = 2;
    String[] res = new String[num];
    
    for (int i = 0; i < num; i++) {
      List<PropertyData<?>> properties = new ArrayList<PropertyData<?>>();
      properties.add(fFactory.createPropertyIdData(PropertyIds.CMIS_NAME, "Folder " + i));
      properties.add(fFactory.createPropertyIdData(PropertyIds.CMIS_OBJECT_TYPE_ID,
          BaseObjectTypeIds.CMIS_FOLDER.value()));
      PropertiesData props = fFactory.createPropertiesData(properties);      
      String id = objSvc.createFolder(fRepositoryId, props, fRootFolderId, null, null, null, null);
      res[i] = id;
    }
    return res;
  }

  private void testCreateVersionedDocument(VersioningState versioningState) {
    // type id is: VersionTestTypeSystemCreator.VERSION_TEST_DOCUMENT_TYPE_ID    
    String verId = createDocument(PROP_NAME, fRootFolderId, versioningState);
    getDocument(verId);
    
    ObjectData version = fObjSvc.getObject(fRepositoryId, verId, "*", false, IncludeRelationships.NONE, null, false, false, null);
    String docId = getVersionSeriesId(verId, version.getProperties().getProperties());
    assertTrue(null != docId && docId.length() > 0 );
    
    List<ObjectData> allVersions = fVerSvc.getAllVersions(fRepositoryId, docId, "*", false, null);
    assertEquals(1, allVersions.size());
    
    checkVersionProperties(verId, versioningState, allVersions.get(0).getProperties().getProperties());    
  }
  
  private String getVersionSeriesId(String docId, Map<String, PropertyData<?>> props) {
    PropertyIdData pdid = (PropertyIdData) props.get(PropertyIds.CMIS_VERSION_SERIES_ID);
    assertNotNull(pdid);
    String sVal = pdid.getFirstValue();
    assertNotNull(sVal);
    return sVal;
  }
  
  private boolean isCheckedOut(String objectId) {
    PropertiesData props = fObjSvc.getProperties(fRepositoryId, objectId, "*", null);
    return isCheckedOut(props.getProperties());
  }
  
  private boolean isCheckedOut( Map<String, PropertyData<?>> props) {
    PropertyBooleanData pdb = (PropertyBooleanData) props.get(PropertyIds.CMIS_IS_VERSION_SERIES_CHECKED_OUT);
    assertNotNull(pdb);
    boolean bVal = pdb.getFirstValue();
    return bVal;
 
  }
  
  private void checkVersionProperties(String docId, VersioningState versioningState, Map<String, PropertyData<?>> props) {
    for (PropertyData<?> pd : props.values()) {
      log.info("return property id: " + pd.getId() + ", value: " + pd.getValues());
    }
    
    DocumentTypeDefinition typeDef = (DocumentTypeDefinition) fRepSvc.getTypeDefinition(fRepositoryId, VersionTestTypeSystemCreator.VERSION_TEST_DOCUMENT_TYPE_ID, null);
    PropertyBooleanData pdb = (PropertyBooleanData) props.get(PropertyIds.CMIS_IS_LATEST_VERSION);
    assertNotNull(pdb);
    boolean bVal = pdb.getFirstValue();
    assertEquals(versioningState != VersioningState.CHECKEDOUT, bVal); // if checked out it isn't the latest version

    pdb = (PropertyBooleanData) props.get(PropertyIds.CMIS_IS_MAJOR_VERSION);
    assertNotNull(pdb);
    bVal = pdb.getFirstValue();
    assertEquals(versioningState == VersioningState.MAJOR, bVal);
    
    pdb = (PropertyBooleanData) props.get(PropertyIds.CMIS_IS_LATEST_MAJOR_VERSION);
    assertNotNull(pdb);
    bVal = pdb.getFirstValue();
    assertEquals(versioningState == VersioningState.MAJOR, bVal);
    
    PropertyIdData pdid = (PropertyIdData) props.get(PropertyIds.CMIS_VERSION_SERIES_ID);
    assertNotNull(pdb);
    String sVal = pdid.getFirstValue();
    if (typeDef.isVersionable())
      assertFalse(docId.equals(sVal));
    else
      assertEquals(docId, sVal);

    pdb = (PropertyBooleanData) props.get(PropertyIds.CMIS_IS_VERSION_SERIES_CHECKED_OUT);
    assertNotNull(pdb);
    bVal = pdb.getFirstValue();
    assertEquals(versioningState == VersioningState.CHECKEDOUT, bVal);
    
    PropertyStringData pds = (PropertyStringData) props.get(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_BY);
    assertNotNull(pdb);
    sVal = pds.getFirstValue();
    if (versioningState == VersioningState.CHECKEDOUT)
      assertTrue(sVal != null && sVal.length() > 0);
    else
      assertTrue(null == sVal || sVal.equals(""));

    pdid = (PropertyIdData) props.get(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_ID);
    assertNotNull(pdid);
    sVal = pdid.getFirstValue();
    if (versioningState == VersioningState.CHECKEDOUT)
      assertTrue(sVal != null && sVal.length() > 0);
    else
      assertTrue(null == sVal || sVal.equals(""));
    
    pds = (PropertyStringData) props.get(PropertyIds.CMIS_CHECKIN_COMMENT);
    assertNotNull(pdb);
    sVal = pds.getFirstValue();
    assertTrue(null == sVal || sVal.equals(""));
    
  }

  private String getDocument(String id) {
    String returnedId=null;
    try {
      ObjectData res = fObjSvc.getObject(fRepositoryId, id, "*", false, IncludeRelationships.NONE,
          null, false, false, null);
      assertNotNull(res);
      testReturnedProperties(res.getProperties().getProperties());
      returnedId = res.getId();
      assertEquals(id, returnedId);    
    } catch (Exception e) {
      fail("getObject() failed with exception: " + e);
    }    
    return returnedId;
  }
  
  private void testReturnedProperties(Map<String, PropertyData<?>> props) {
    for (PropertyData<?> pd : props.values()) {
      log.info("return property id: " + pd.getId() + ", value: " + pd.getValues());
    }
    
    PropertyData<?> pd = props.get(PropertyIds.CMIS_NAME);
    assertNotNull(pd);
    assertEquals(PROP_NAME, pd.getFirstValue());
    pd = props.get(PropertyIds.CMIS_OBJECT_TYPE_ID);
    assertEquals(VersionTestTypeSystemCreator.VERSION_TEST_DOCUMENT_TYPE_ID, pd.getFirstValue());
    pd = props.get(VersionTestTypeSystemCreator.PROPERTY_ID);
    assertEquals(PROP_VALUE, pd.getFirstValue());
  }
 
  private String createDocument(String name, String folderId, VersioningState versioningState) {

    String id = null;
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(VersionTestTypeSystemCreator.PROPERTY_ID, PROP_VALUE);
    id = fCreator.createDocument(name, VersionTestTypeSystemCreator.VERSION_TEST_DOCUMENT_TYPE_ID, folderId, 
        versioningState, properties);
    
    return id;
  }
    
  public static class VersionTestTypeSystemCreator implements TypeCreator {
    static public String VERSION_TEST_DOCUMENT_TYPE_ID = "MyVersionedType";
    static public String PROPERTY_ID = "StringProp";

    public List<TypeDefinition> createTypesList() {
      // always add CMIS default types
      List<TypeDefinition> typesList = new LinkedList<TypeDefinition>();

      // create a complex type with properties
      InMemoryDocumentTypeDefinition cmisComplexType = new InMemoryDocumentTypeDefinition(VERSION_TEST_DOCUMENT_TYPE_ID,
          "VersionedType", InMemoryDocumentTypeDefinition.getRootDocumentType());
      
      // create a single String property definition
      
      Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      
      PropertyStringDefinitionImpl prop1 = PropertyCreationHelper.createStringDefinition(PROPERTY_ID, "Sample String Property");
      propertyDefinitions.put(prop1.getId(), prop1);
      
      cmisComplexType.addCustomPropertyDefinitions(propertyDefinitions);    
      cmisComplexType.setIsVersionable(true); // make it a versionable type;
      
      // add type to types collection
      typesList.add(cmisComplexType);

      
      return typesList;
   }
    
  } // ObjectTestTypeSystemCreator
  
}