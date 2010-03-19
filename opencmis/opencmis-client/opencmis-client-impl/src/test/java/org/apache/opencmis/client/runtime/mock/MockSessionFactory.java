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
package org.apache.opencmis.client.runtime.mock;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.opencmis.client.api.Ace;
import org.apache.opencmis.client.api.ChangeEvent;
import org.apache.opencmis.client.api.CmisObject;
import org.apache.opencmis.client.api.ContentStream;
import org.apache.opencmis.client.api.Document;
import org.apache.opencmis.client.api.FileableCmisObject;
import org.apache.opencmis.client.api.Folder;
import org.apache.opencmis.client.api.ObjectId;
import org.apache.opencmis.client.api.OperationContext;
import org.apache.opencmis.client.api.Policy;
import org.apache.opencmis.client.api.Property;
import org.apache.opencmis.client.api.QueryResult;
import org.apache.opencmis.client.api.Session;
import org.apache.opencmis.client.api.SessionFactory;
import org.apache.opencmis.client.api.objecttype.DocumentType;
import org.apache.opencmis.client.api.objecttype.FolderType;
import org.apache.opencmis.client.api.objecttype.ObjectType;
import org.apache.opencmis.client.api.objecttype.PolicyType;
import org.apache.opencmis.client.api.objecttype.RelationshipType;
import org.apache.opencmis.client.api.repository.ObjectFactory;
import org.apache.opencmis.client.api.repository.Repository;
import org.apache.opencmis.client.api.repository.RepositoryCapabilities;
import org.apache.opencmis.client.api.repository.RepositoryInfo;
import org.apache.opencmis.client.api.util.Container;
import org.apache.opencmis.client.api.util.PagingList;
import org.apache.opencmis.client.runtime.Fixture;
import org.apache.opencmis.client.runtime.FixtureData;
import org.apache.opencmis.commons.PropertyIds;
import org.apache.opencmis.commons.SessionParameter;
import org.apache.opencmis.commons.api.PropertyDefinition;
import org.apache.opencmis.commons.enums.BaseObjectTypeIds;
import org.apache.opencmis.commons.enums.CapabilityAcl;
import org.apache.opencmis.commons.enums.CapabilityChanges;
import org.apache.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.opencmis.commons.enums.CapabilityJoin;
import org.apache.opencmis.commons.enums.CapabilityQuery;
import org.apache.opencmis.commons.enums.CapabilityRendition;
import org.apache.opencmis.commons.enums.CmisProperties;
import org.apache.opencmis.commons.enums.PropertyType;
import org.apache.opencmis.commons.enums.SessionType;
import org.apache.opencmis.commons.enums.TypeOfChanges;
import org.apache.opencmis.commons.exceptions.CmisNotSupportedException;

/**
 * MockSessionFactory based on EasyMock Framework.
 */
public class MockSessionFactory implements SessionFactory {

	private Session mockSession;

	{
		this.idObjectIndex = new Hashtable<String, CmisObject>();
		this.pathObjectIndex = new Hashtable<String, CmisObject>();
		this.idTypeIndex = new Hashtable<String, ObjectType>();
		this.mockSession = this.createMockSession();
	}

	private Hashtable<String, CmisObject> idObjectIndex = null;
	private Hashtable<String, ObjectType> idTypeIndex = null;
	private Hashtable<String, CmisObject> pathObjectIndex = null;

	/*
	 * properties
	 */
	private static String PROPERTY_VALUE_STRING = "abc";
	private static Integer PROPERTY_VALUE_INTEGER = new Integer(4711);
	private static Boolean PROPERTY_VALUE_BOOLEAN = new Boolean(true);
	private static Double PROPERTY_VALUE_DOUBLE = new Double(1.0);
	private static Float PROPERTY_VALUE_FLOAT = new Float(1.0);
	private static String PROPERTY_VALUE_ID = "xyz";
	private static String PROPERTY_VALUE_HTML = "<html><body>html value</body></html>";
	private static Calendar PROPERTY_VALUE_DATETIME = GregorianCalendar
			.getInstance();
	private static URI PROPERTY_VALUE_URI = URI.create("http://foo.com");

	@SuppressWarnings("unchecked")
	public <T extends Session> T createSession(Map<String, String> parameters) {
		T session = null;
		SessionType st;
		String p = parameters.get(SessionParameter.SESSION_TYPE);

		if (p == null) {
			st = SessionType.PERSISTENT; // default, if type is not set
		} else {
			st = SessionType.fromValue(p);
		}

		switch (st) {
		case PERSISTENT:
			session = (T) this.mockSession;
			break;
		case TRANSIENT:
			throw new CmisNotSupportedException("SessionType = " + st);
		}

		return session;
	}

	@SuppressWarnings("unchecked")
	private Session createMockSession() {
		this.createMockGlobalTypes();

		Session session = createNiceMock(Session.class);
		Folder rootFolder = this.createMockRepositoryRootFolder();
		this.createMockTestRootFolder("/", rootFolder);

		for (String path : this.pathObjectIndex.keySet()) {
			CmisObject o = this.pathObjectIndex.get(path);
			expect(session.getObjectByPath(path)).andReturn(o).anyTimes();
		}

		expect(session.getRepositoryInfo()).andReturn(
				this.createMockRepositoryInfo()).anyTimes();
		expect(session.getRootFolder()).andReturn(rootFolder).anyTimes();

		String id = rootFolder.getId();
		ObjectId objectId = createMockObjectId(id);
		expect(session.createObjectId(id)).andReturn(objectId).anyTimes();

		expect(session.getDefaultContext()).andReturn(
				this.createMockOperationContext()).anyTimes();
		expect(session.getLocale()).andReturn(new Locale("EN")).anyTimes();
		expect(session.getObjectFactory()).andReturn(
				this.createMockObjectFactory()).anyTimes();

		expect(session.getTypeDefinition(ObjectType.DOCUMENT_BASETYPE_ID))
				.andReturn(
						this.idTypeIndex.get(ObjectType.DOCUMENT_BASETYPE_ID))
				.anyTimes();
		expect(session.getTypeDefinition(ObjectType.FOLDER_BASETYPE_ID))
				.andReturn(this.idTypeIndex.get(ObjectType.FOLDER_BASETYPE_ID))
				.anyTimes();
		expect(session.getTypeDefinition(ObjectType.POLICY_BASETYPE_ID))
				.andReturn(this.idTypeIndex.get(ObjectType.POLICY_BASETYPE_ID))
				.anyTimes();
		expect(session.getTypeDefinition(ObjectType.RELATIONSHIP_BASETYPE_ID))
				.andReturn(
						this.idTypeIndex
								.get(ObjectType.RELATIONSHIP_BASETYPE_ID))
				.anyTimes();

		if (!FixtureData.DOCUMENT_TYPE_ID.toString().equalsIgnoreCase(
				ObjectType.DOCUMENT_BASETYPE_ID)) {
			expect(
					session.getTypeDefinition(FixtureData.DOCUMENT_TYPE_ID
							.toString())).andReturn(
					this.idTypeIndex.get(FixtureData.DOCUMENT_TYPE_ID
							.toString())).anyTimes();
		}
		if (!FixtureData.FOLDER_TYPE_ID.toString().equalsIgnoreCase(
				ObjectType.FOLDER_BASETYPE_ID)) {
			expect(
					session.getTypeDefinition(FixtureData.FOLDER_TYPE_ID
							.toString()))
					.andReturn(
							this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID
									.toString())).anyTimes();
		}
		/* document child/descendants types */
		List<ObjectType> dtl = new ArrayList<ObjectType>();
		dtl.add(this.idTypeIndex.get(FixtureData.DOCUMENT_TYPE_ID.toString()));
		PagingList<ObjectType> plot = this.createMockPaging(dtl);
		Container<ObjectType> ctdoc = this.createMockContainer(this.idTypeIndex
				.get(FixtureData.DOCUMENT_TYPE_ID.toString()), null);
		expect(
				session.getTypeChildren(
						BaseObjectTypeIds.CMIS_DOCUMENT.value(), true, 2))
				.andReturn(plot).anyTimes();
		expect(
				session.getTypeDescendants(BaseObjectTypeIds.CMIS_DOCUMENT
						.value(), 1, true)).andReturn(
				Collections.singletonList(ctdoc)).anyTimes();

		/* folder child/descendants types */
		List<ObjectType> ftl = new ArrayList<ObjectType>();
		ftl.add(this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID.toString()));
		PagingList<ObjectType> plfot = this.createMockPaging(ftl);
		Container<ObjectType> ctfolder = this.createMockContainer(
				this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID.toString()),
				null);
		expect(
				session.getTypeChildren(BaseObjectTypeIds.CMIS_FOLDER.value(),
						true, 2)).andReturn(plfot).anyTimes();
		expect(
				session.getTypeDescendants(BaseObjectTypeIds.CMIS_FOLDER
						.value(), 1, true)).andReturn(
				Collections.singletonList(ctfolder)).anyTimes();

		/* change support */
		List<ChangeEvent> cel = this.createMockChangeEvents();
		PagingList<ChangeEvent> plce = this.createMockPaging(cel);
		expect(session.getContentChanges(null, -1)).andReturn(plce).anyTimes();

		/* query support */
		List<QueryResult> queryList = new ArrayList<QueryResult>();
		for (CmisObject cm : this.idObjectIndex.values()) {
			queryList.add(createMockQueryResult(cm));
		}
		PagingList<QueryResult> plq = this.createMockPaging(queryList);
		expect(session.query(FixtureData.QUERY.toString(), false, 2))
				.andReturn(plq).anyTimes();

		this.makeObjectsAccessible(session);

		/*
		 * WRITE SUPPORT
		 */
		expect(
				session.createFolder((List<Property<?>>) anyObject(),
						(ObjectId) anyObject(), (List<Policy>) anyObject(),
						(List<Ace>) anyObject(), (List<Ace>) anyObject()))
				.andReturn(this.createMockObjectId()).anyTimes();

		replay(session);

		return session;
	}

	private ObjectId createMockObjectId() {
		ObjectId id = createNiceMock(ObjectId.class);

		replay(id);

		return id;
	}

	private List<ChangeEvent> createMockChangeEvents() {
		List<ChangeEvent> cel = new ArrayList<ChangeEvent>();
		ChangeEvent ce = createNiceMock(ChangeEvent.class);
		List<Property<?>> pl = new ArrayList<Property<?>>();

		expect(ce.getObjectId()).andReturn(UUID.randomUUID().toString())
				.anyTimes();
		expect(ce.getChangeType()).andReturn(TypeOfChanges.CREATED).anyTimes();
		expect(ce.getNewProperties()).andReturn(pl).anyTimes();

		replay(ce);

		cel.add(ce);

		return cel;
	}

	private void makeObjectsAccessible(Session s) {
		Enumeration<String> e = this.idObjectIndex.keys();
		String id;
		CmisObject obj;

		while (e.hasMoreElements()) {
			id = e.nextElement();
			obj = this.idObjectIndex.get(id);

			ObjectId objectId = createMockObjectId(id);
			expect(s.getObject(objectId)).andReturn(obj).anyTimes();
		}
	}

	private ObjectFactory createMockObjectFactory() {
		ObjectFactory of = createNiceMock(ObjectFactory.class);

		replay(of);

		return of;
	}

	private ObjectId createMockObjectId(String id) {
		ObjectId oid = createNiceMock(ObjectId.class);

		expect(oid.getId()).andReturn(id).anyTimes();

		replay(oid);

		return oid;
	}

	private OperationContext createMockOperationContext() {
		OperationContext oc = createNiceMock(OperationContext.class);

		replay(oc);

		return oc;
	}

	private Folder createMockTestRootFolder(String parentPath, Folder parent) {
		Folder f = createNiceMock(Folder.class);

		expect(f.getId()).andReturn(UUID.randomUUID().toString()).anyTimes();
		expect(f.getName()).andReturn(Fixture.TEST_ROOT_FOLDER_NAME).anyTimes();
		expect(f.getFolderParent()).andReturn(parent).anyTimes();
		expect(f.getType()).andReturn(
				this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID.toString()))
				.anyTimes();
		expect(f.getBaseType()).andReturn(
				this.idTypeIndex.get(BaseObjectTypeIds.CMIS_FOLDER.value()))
				.anyTimes();
		List<CmisObject> children = new ArrayList<CmisObject>();
		children.add(this.createMockTestFolder(f, "/"
				+ Fixture.TEST_ROOT_FOLDER_NAME + "/"
				+ FixtureData.FOLDER1_NAME.toString(), FixtureData.FOLDER1_NAME
				.toString()));
		children.add(this.createMockTestFolder(f, "/"
				+ Fixture.TEST_ROOT_FOLDER_NAME + "/"
				+ FixtureData.FOLDER2_NAME.toString(), FixtureData.FOLDER2_NAME
				.toString()));
		children.add(this.createMockTestDocument(f, "/"
				+ Fixture.TEST_ROOT_FOLDER_NAME + "/"
				+ FixtureData.DOCUMENT1_NAME.toString(),
				FixtureData.DOCUMENT1_NAME.toString()));

		PagingList<CmisObject> pl = this.createMockPaging(children);
		expect(f.getChildren(1)).andReturn(pl).anyTimes();
		expect(f.getChildren(2)).andReturn(pl).anyTimes();
		expect(f.getChildren(1000)).andReturn(pl).anyTimes();

		List<Container<FileableCmisObject>> ct = new ArrayList<Container<FileableCmisObject>>();
		Folder f1 = this.createMockTestFolder(f, "/"
				+ FixtureData.FOLDER1_NAME.toString(), FixtureData.FOLDER1_NAME
				.toString());
		Folder f2 = this.createMockTestFolder(f, "/"
				+ FixtureData.FOLDER2_NAME.toString(), FixtureData.FOLDER2_NAME
				.toString());
		ct.add(this.createMockContainer((FileableCmisObject) f1, null));
		ct.add(this.createMockContainer((FileableCmisObject) f2, null));

		expect(f.getDescendants(1)).andReturn(ct).anyTimes();
		expect(f.getDescendants(2)).andReturn(ct).anyTimes();
		expect(f.getDescendants(1000)).andReturn(ct).anyTimes();
		expect(f.getFolderTree(1)).andReturn(ct).anyTimes();
		expect(f.getFolderTree(2)).andReturn(ct).anyTimes();
		expect(f.getFolderTree(1000)).andReturn(ct).anyTimes();
		this.createMockProperties(f);

		replay(f);
		this.idObjectIndex.put(f.getId(), f);
		this.pathObjectIndex.put(parentPath + Fixture.TEST_ROOT_FOLDER_NAME, f);

		return f;
	}

	@SuppressWarnings("unchecked")
	private void createMockProperties(CmisObject o) {
		GregorianCalendar date = new GregorianCalendar();

		expect(o.getCreatedBy()).andReturn(
				Fixture.getParamter().get(SessionParameter.USER)).anyTimes();
		expect(o.getLastModifiedBy()).andReturn(
				Fixture.getParamter().get(SessionParameter.USER)).anyTimes();
		expect(o.getLastModificationDate()).andReturn(date).anyTimes();
		expect(o.getCreationDate()).andReturn(date).anyTimes();

		{
			ArrayList a = new ArrayList<Property<Property>>();

			// mandatory default properties
			a.add(this.createMockStringProperty(PropertyIds.CMIS_BASE_TYPE_ID));
			a.add(this.createMockStringProperty(PropertyIds.CMIS_NAME));
			a
					.add(this
							.createMockIntegerProperty(PropertyIds.CMIS_CONTENT_STREAM_LENGTH));

			// other properties
			a.add(this.createMockBooleanProperty(UUID.randomUUID().toString()));
			a
					.add(this.createMockDateTimeProperty(UUID.randomUUID()
							.toString()));
			a.add(this.createMockFloatProperty(UUID.randomUUID().toString()));
			a.add(this.createMockDoubleProperty(UUID.randomUUID().toString()));
			a.add(this.createMockHtmlProperty(UUID.randomUUID().toString()));
			a.add(this.createMockIdProperty(UUID.randomUUID().toString()));
			a.add(this.createMockUriProperty(UUID.randomUUID().toString()));

			expect(o.getProperties()).andReturn(a).anyTimes();
			// expect(o.getProperties(Fixture.PROPERTY_FILTER)).andReturn(a)
			// .anyTimes();

			/* single property */
			Property<Object> p1 = (Property<Object>) createMockStringProperty(CmisProperties.OBJECT_ID
					.value());
			expect(o.getProperty(CmisProperties.OBJECT_ID.value())).andReturn(
					p1).anyTimes();
			expect(o.getPropertyValue(CmisProperties.OBJECT_ID.value()))
					.andReturn(p1.getValue()).anyTimes();

			/* multi valued property */
			Property<Object> p2 = (Property<Object>) createMockMultiValuedStringProperty(FixtureData.PROPERTY_NAME_STRING_MULTI_VALUED
					.toString());
			expect(
					o.getProperty(FixtureData.PROPERTY_NAME_STRING_MULTI_VALUED
							.toString())).andReturn(p2).anyTimes();
			expect(
					o
							.getPropertyMultivalue(FixtureData.PROPERTY_NAME_STRING_MULTI_VALUED
									.toString())).andReturn(p2.getValues())
					.anyTimes();
		}

	}

	private Property<?> createMockMultiValuedStringProperty(String id) {
		Property<String> p = createNiceMock(StringProperty.class);

		expect(p.getType()).andReturn(PropertyType.STRING).anyTimes();
		expect(p.getValue())
				.andReturn(MockSessionFactory.PROPERTY_VALUE_STRING).anyTimes();
		expect(p.isMultiValued()).andReturn(true).anyTimes();
		List<String> v = new ArrayList<String>();
		v.add(MockSessionFactory.PROPERTY_VALUE_STRING);
		v.add(MockSessionFactory.PROPERTY_VALUE_STRING);
		expect(p.getValues()).andReturn(v).anyTimes();
		expect(p.getValueAsString()).andReturn(v.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockDateTimeProperty(String id) {
		Property<Calendar> p = createNiceMock(DateTimeProperty.class);

		expect(p.getType()).andReturn(PropertyType.DATETIME).anyTimes();
		expect(p.getValue()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_DATETIME).anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_DATETIME.toString())
				.anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockHtmlProperty(String id) {
		Property<String> p = createNiceMock(StringProperty.class);

		expect(p.getType()).andReturn(PropertyType.HTML).anyTimes();
		expect(p.getValue()).andReturn(MockSessionFactory.PROPERTY_VALUE_HTML)
				.anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_HTML.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockIdProperty(String id) {
		Property<String> p = createNiceMock(StringProperty.class);

		expect(p.getType()).andReturn(PropertyType.ID).anyTimes();
		expect(p.getValue()).andReturn(MockSessionFactory.PROPERTY_VALUE_ID)
				.anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_ID.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockUriProperty(String id) {
		Property<URI> p = createNiceMock(UriProperty.class);

		expect(p.getType()).andReturn(PropertyType.URI).anyTimes();
		expect(p.getValue()).andReturn(MockSessionFactory.PROPERTY_VALUE_URI)
				.anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_URI.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockDoubleProperty(String id) {
		Property<Double> p = createNiceMock(DoubleProperty.class);

		expect(p.getType()).andReturn(PropertyType.DECIMAL).anyTimes();
		expect(p.getValue())
				.andReturn(MockSessionFactory.PROPERTY_VALUE_DOUBLE).anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_DOUBLE.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockFloatProperty(String id) {
		Property<Float> p = createNiceMock(FloatProperty.class);

		expect(p.getType()).andReturn(PropertyType.DECIMAL).anyTimes();
		expect(p.getValue()).andReturn(MockSessionFactory.PROPERTY_VALUE_FLOAT)
				.anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_FLOAT.toString()).anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockBooleanProperty(String id) {
		Property<Boolean> p = createNiceMock(BooleanProperty.class);

		expect(p.getType()).andReturn(PropertyType.BOOLEAN).anyTimes();
		expect(p.getValue()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_BOOLEAN).anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_BOOLEAN.toString())
				.anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockIntegerProperty(String id) {
		Property<Integer> p = createNiceMock(IntegerProperty.class);

		expect(p.getType()).andReturn(PropertyType.INTEGER).anyTimes();
		expect(p.getValue()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_INTEGER).anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_INTEGER.toString())
				.anyTimes();

		replay(p);

		return p;
	}

	private Property<?> createMockStringProperty(String id) {
		Property<String> p = createNiceMock(StringProperty.class);

		expect(p.getType()).andReturn(PropertyType.STRING).anyTimes();
		expect(p.getValue())
				.andReturn(MockSessionFactory.PROPERTY_VALUE_STRING).anyTimes();
		expect(p.getValueAsString()).andReturn(
				MockSessionFactory.PROPERTY_VALUE_STRING.toString()).anyTimes();

		replay(p);

		return p;
	}

	private CmisObject createMockTestDocument(Folder parent, String path,
			String name) {
		Document d = createNiceMock(Document.class);

		expect(d.getId()).andReturn(UUID.randomUUID().toString()).anyTimes();
		expect(d.getName()).andReturn(name).anyTimes();
		expect(d.getType()).andReturn(
				this.idTypeIndex.get(FixtureData.DOCUMENT_TYPE_ID.toString()))
				.anyTimes();
		expect(d.getBaseType()).andReturn(
				this.idTypeIndex.get(BaseObjectTypeIds.CMIS_DOCUMENT.value()))
				.anyTimes();
		this.createMockProperties(d);
		expect(d.getContentStream()).andReturn(this.createMockContentStream())
				.anyTimes();

		replay(d);
		this.idObjectIndex.put(d.getId(), d);
		this.pathObjectIndex.put(path, d);

		return d;
	}

	private ContentStream createMockContentStream() {
		ContentStream cs = createNiceMock(ContentStream.class);

		byte[] b = "abc".getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(b);

		expect(cs.getFileName()).andReturn("file.txt").anyTimes();
		expect(cs.getMimeType()).andReturn("text/html").anyTimes();
		expect(cs.getLength()).andReturn((long) b.length).anyTimes();
		expect(cs.getStream()).andReturn(bais).anyTimes();

		replay(cs);

		return cs;
	}

	private Folder createMockTestFolder(Folder parent, String path, String name) {
		Folder f = createNiceMock(Folder.class);

		expect(f.getId()).andReturn(UUID.randomUUID().toString()).anyTimes();
		expect(f.getName()).andReturn(name).anyTimes();
		expect(f.getFolderParent()).andReturn(parent).anyTimes();
		expect(f.getType()).andReturn(
				this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID.toString()))
				.anyTimes();
		expect(f.getBaseType()).andReturn(
				this.idTypeIndex.get(BaseObjectTypeIds.CMIS_FOLDER.value()))
				.anyTimes();
		List<CmisObject> children = new ArrayList<CmisObject>();
		children.add(this.createMockTestDocument(f, path + "/"
				+ FixtureData.DOCUMENT1_NAME.toString(),
				FixtureData.DOCUMENT1_NAME.toString()));
		children.add(this.createMockTestDocument(f, path + "/"
				+ FixtureData.DOCUMENT2_NAME.toString(),
				FixtureData.DOCUMENT2_NAME.toString()));

		PagingList<CmisObject> pl = this.createMockPaging(children);
		expect(f.getChildren(null, -1)).andReturn(pl).anyTimes();

		List<Container<FileableCmisObject>> ctlist = new ArrayList<Container<FileableCmisObject>>();
		expect(f.getDescendants(-1)).andReturn(ctlist).anyTimes();
		expect(f.getFolderTree(-1)).andReturn(ctlist).anyTimes();
		this.createMockProperties(f);

		replay(f);
		this.idObjectIndex.put(f.getId(), f);

		return f;
	}

	private QueryResult createMockQueryResult(CmisObject cmisObject) {
		QueryResult qr = createNiceMock(QueryResult.class);

		return qr;
	}

	@SuppressWarnings( { "unchecked" })
	private <T> PagingList<T> createMockPaging(List<T> items) {
		PagingList<T> pl = createNiceMock(PagingList.class);

		ArrayList<List<T>> a = new ArrayList<List<T>>();
		a.add(items);
		Iterator<List<T>> i = a.iterator();

		expect(pl.get(0)).andReturn(items).anyTimes();
		// expect(pl.isEmpty()).andReturn(false).anyTimes();
		expect(pl.iterator()).andReturn(i).anyTimes();

		replay(pl);

		return pl;
	}

	@SuppressWarnings("unchecked")
	private <T> Container<T> createMockContainer(T item,
			List<Container<T>> children) {
		Container<T> c = createNiceMock(Container.class);

		expect(c.getItem()).andReturn(item).anyTimes();
		expect(c.getChildren()).andReturn(children).anyTimes();

		replay(c);

		return c;
	}

	private void createMockGlobalTypes() {
		FolderType bft = createNiceMock(FolderType.class);
		expect(bft.getId()).andReturn(ObjectType.FOLDER_BASETYPE_ID).anyTimes();
		expect(bft.isBaseType()).andReturn(true);
		expect(bft.getBaseType()).andReturn(null).anyTimes();
		expect(bft.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();
		
		FolderType ft = createNiceMock(FolderType.class);
		expect(ft.getId()).andReturn(FixtureData.FOLDER_TYPE_ID.toString())
				.anyTimes();
		expect(ft.isBaseType()).andReturn(false);
		expect(ft.getBaseType()).andReturn(bft).anyTimes();
		expect(ft.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();

		PolicyType bpt = createNiceMock(PolicyType.class);
		expect(bpt.getId()).andReturn(ObjectType.POLICY_BASETYPE_ID).anyTimes();
		expect(bpt.isBaseType()).andReturn(true);
		expect(bpt.getBaseType()).andReturn(null).anyTimes();
		expect(bpt.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();

		RelationshipType brt = createNiceMock(RelationshipType.class);
		expect(brt.getId()).andReturn(ObjectType.RELATIONSHIP_BASETYPE_ID)
				.anyTimes();
		expect(brt.isBaseType()).andReturn(true);
		expect(brt.getBaseType()).andReturn(null).anyTimes();
		expect(brt.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();

		DocumentType bdt = createNiceMock(DocumentType.class);
		expect(bdt.getId()).andReturn(ObjectType.DOCUMENT_BASETYPE_ID)
				.anyTimes();
		expect(bdt.isBaseType()).andReturn(true);
		expect(bdt.getBaseType()).andReturn(null).anyTimes();
		expect(bdt.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();

		DocumentType dt = createNiceMock(DocumentType.class);
		expect(dt.getId()).andReturn(FixtureData.DOCUMENT_TYPE_ID.toString())
				.anyTimes();
		expect(dt.isBaseType()).andReturn(false);
		expect(dt.getBaseType()).andReturn(bft).anyTimes();
		expect(dt.getPropertyDefintions()).andReturn((new HashMap<String, PropertyDefinition<?>>())).anyTimes();

		replay(bft);
		replay(ft);
		replay(bdt);
		replay(dt);
		replay(bpt);
		replay(brt);

		this.idTypeIndex.put(ft.getId(), ft);
		this.idTypeIndex.put(bft.getId(), bft); // can overwrite ft

		this.idTypeIndex.put(dt.getId(), dt);
		this.idTypeIndex.put(bdt.getId(), bdt); // can overwrite dt

		this.idTypeIndex.put(bpt.getId(), bpt);
		this.idTypeIndex.put(brt.getId(), brt);

	}

	private Folder createMockRepositoryRootFolder() {
		Folder f = createNiceMock(Folder.class);

		expect(f.getId()).andReturn(UUID.randomUUID().toString()).anyTimes();
		expect(f.getName()).andReturn("").anyTimes(); // or "root" ?
		expect(f.getFolderParent()).andReturn(null).anyTimes();
		expect(f.getType()).andReturn(
				this.idTypeIndex.get(FixtureData.FOLDER_TYPE_ID.toString()))
				.anyTimes();
		this.createMockProperties(f);

		replay(f);
		this.idObjectIndex.put(f.getId(), f);
		this.pathObjectIndex.put("/", f);
		return f;
	}

	private RepositoryInfo createMockRepositoryInfo() {
		RepositoryInfo ri = createNiceMock(RepositoryInfo.class);

		expect(ri.getName()).andReturn("MockRepository").anyTimes();
		expect(ri.getId()).andReturn(UUID.randomUUID().toString()).anyTimes();
		expect(ri.getDescription()).andReturn("description").anyTimes();
		expect(ri.getCmisVersionSupported()).andReturn("1.0").anyTimes();
		expect(ri.getVendorName()).andReturn("Apache").anyTimes();
		expect(ri.getProductName()).andReturn("OpenCMIS").anyTimes();
		expect(ri.getPrincipalIdAnonymous()).andReturn("anonymous").anyTimes();
		expect(ri.getPrincipalIdAnyone()).andReturn("anyone").anyTimes();
		expect(ri.getThinClientUri()).andReturn("http://foo.com").anyTimes();
		expect(ri.getChangesOnType()).andReturn(
				new ArrayList<BaseObjectTypeIds>()).anyTimes();

		expect(ri.getCapabilities()).andReturn(
				this.createMockRepositoryCapabilities()).anyTimes();

		replay(ri);

		return ri;
	}

	private RepositoryCapabilities createMockRepositoryCapabilities() {
		RepositoryCapabilities rc = createNiceMock(RepositoryCapabilities.class);

		expect(rc.getAclSupport()).andReturn(CapabilityAcl.NONE).anyTimes();
		expect(rc.getChangesSupport()).andReturn(CapabilityChanges.ALL)
				.anyTimes();
		expect(rc.getContentStreamUpdatabilitySupport()).andReturn(
				CapabilityContentStreamUpdates.NONE).anyTimes();
		expect(rc.getJoinSupport()).andReturn(CapabilityJoin.NONE).anyTimes();
		expect(rc.getQuerySupport()).andReturn(CapabilityQuery.BOTHCOMBINED)
				.anyTimes();
		expect(rc.getRenditionsSupport()).andReturn(CapabilityRendition.NONE)
				.anyTimes();

		replay(rc);

		return rc;
	}

	private interface StringProperty extends Property<String> {
	}

	private interface IntegerProperty extends Property<Integer> {
	}

	private interface DoubleProperty extends Property<Double> {
	}

	private interface FloatProperty extends Property<Float> {
	}

	private interface DateTimeProperty extends Property<Calendar> {
	}

	private interface UriProperty extends Property<URI> {
	}

	private interface BooleanProperty extends Property<Boolean> {
	}

	public List<Repository> getRepositories(Map<String, String> parameters) {
		return null;
	}

}
