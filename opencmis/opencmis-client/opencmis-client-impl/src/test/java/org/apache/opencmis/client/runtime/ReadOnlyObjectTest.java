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

import java.net.URI;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.opencmis.client.api.Document;
import org.apache.opencmis.client.api.Folder;
import org.apache.opencmis.client.api.Property;
import org.apache.opencmis.client.api.objecttype.ObjectType;
import org.apache.opencmis.commons.SessionParameter;
import org.apache.opencmis.commons.enums.CmisProperties;
import org.apache.opencmis.commons.enums.PropertyType;
import org.junit.Test;

/**
 * Readonly tests on files and documents
 */
public class ReadOnlyObjectTest extends AbstractSessionTest {

  @Test
  public void verifyRoot() {
    Folder root = this.session.getRootFolder();
    Assert.assertNotNull(root);

    Assert.assertEquals("", root.getName());
    Assert.assertNotNull(root.getId());
    Assert.assertEquals("/", root.getPath());
    Assert.assertNull(root.getFolderParent());
    Assert.assertNotNull(root.getType());
    Assert.assertEquals(Fixture.FOLDER_TYPE_ID, root.getType().getId());

    Assert.assertEquals("", root.getName());
    Assert.assertNotNull(root.getId());
    Assert.assertEquals("/", root.getPath());
    Assert.assertNull(root.getFolderParent());
    Assert.assertNotNull(root.getType());
    Assert.assertEquals(Fixture.FOLDER_TYPE_ID, root.getType().getId());
  }

  @Test
  public void readTestFolder() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME;
    Folder folder = (Folder) this.session.getObjectByPath(path);
    Assert.assertNotNull("folder not found: " + path, folder);

    Assert.assertEquals(Fixture.TEST_ROOT_FOLDER_NAME, folder.getName());
    Assert.assertNotNull(folder.getId());
    Assert.assertEquals("/" + Fixture.TEST_ROOT_FOLDER_NAME, folder.getPath());
    Assert.assertNotNull(folder.getFolderParent());
    Assert.assertNotNull(folder.getType());
    Assert.assertEquals(Fixture.FOLDER_TYPE_ID, folder.getType().getId());
    Assert.assertNotNull(folder.getBaseType());
    Assert.assertEquals(ObjectType.FOLDER_BASETYPE_ID, folder.getBaseType().getId());

    Assert.assertNotNull(folder.getCreatedBy());
    Assert.assertEquals(Fixture.getParamter().get(SessionParameter.USER), folder.getCreatedBy());
    Assert.assertNotNull(folder.getLastModifiedBy());
    Assert.assertEquals(Fixture.getParamter().get(SessionParameter.USER), folder
        .getLastModifiedBy());
    Assert.assertNotNull(folder.getLastModificationDate());
    Assert.assertNotNull(folder.getCreationDate());

  }

  @Test
  public void readTestDocument() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    Assert.assertNotNull(document.getId());
    Assert.assertNotNull(document.getBaseType());
    Assert.assertEquals(ObjectType.DOCUMENT_BASETYPE_ID, document.getBaseType().getId());
    Assert.assertEquals(Fixture.DOCUMENT1_NAME, document.getName());
    Assert.assertEquals(path, document.getPath());
    Assert.assertNotNull(document.getType());
    Assert.assertEquals(Fixture.DOCUMENT_TYPE_ID, document.getType().getId());

  }

  public void readDocumentDefaultProperties() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    Assert.assertNotNull(document.getCreatedBy());
    Assert.assertEquals(Fixture.getParamter().get(SessionParameter.USER), document.getCreatedBy());
    Assert.assertNotNull(document.getLastModifiedBy());
    Assert.assertEquals(Fixture.getParamter().get(SessionParameter.USER), document
        .getLastModifiedBy());
    Assert.assertNotNull(document.getLastModificationDate());
    Assert.assertNotNull(document.getCreationDate());
  }

  @Test
  public void readDocumentPropertiesWithFilter() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    List<Property<?>> l = document.getProperties(Fixture.PROPERTY_FILTER);
    Assert.assertNotNull(l);
    Assert.assertEquals(false, l.isEmpty());
    Iterator<Property<?>> i = l.iterator();
    while (i.hasNext()) {
      Property<?> p = i.next();
      Object value = p.getValue();
      PropertyType t = p.getType();

      Assert.assertNotNull(p);
      Assert.assertNotNull(t);

      switch (t) {
      case INTEGER:
        Integer n = (Integer) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_INTEGER, n);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_INTEGER.toString(), p.getValueAsString());
        break;
      case STRING:
        String s = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_STRING, s);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_STRING.toString(), p.getValueAsString());
        break;
      case BOOLEAN:
        Boolean b = (Boolean) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_BOOLEAN, b);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_BOOLEAN.toString(), p.getValueAsString());
        break;
      case DATETIME:
        Calendar c = (Calendar) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_DATETIME, c);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_DATETIME.toString(), p.getValueAsString());
        break;
      case DECIMAL:
        Number num = (Number) value;
        if (num instanceof Double) {
          Assert.assertEquals(Fixture.PROPERTY_VALUE_DOUBLE, num);
          Assert.assertEquals(Fixture.PROPERTY_VALUE_DOUBLE.toString(), p.getValueAsString());
        }
        else if (num instanceof Float) {
          Assert.assertEquals(Fixture.PROPERTY_VALUE_FLOAT, num);
          Assert.assertEquals(Fixture.PROPERTY_VALUE_FLOAT.toString(), p.getValueAsString());
        }
        else {
          Assert.fail("Number not supported: " + num.toString());
        }
        break;
      case HTML:
        String html = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_HTML, html);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_HTML.toString(), p.getValueAsString());
        break;
      case ID:
        String id = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_ID, id);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_ID.toString(), p.getValueAsString());
        break;
      case URI:
        URI uri = (URI) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_URI, uri);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_URI.toString(), p.getValueAsString());
        break;
      default:
        Assert.fail("PropertyType not supported: " + t);
      }
    }

  }

  @Test
  public void readDocumentProperties() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    List<Property<?>> l = document.getProperties();
    Assert.assertNotNull(l);
    Assert.assertEquals(false, l.isEmpty());
    Iterator<Property<?>> i = l.iterator();
    while (i.hasNext()) {
      Property<?> p = i.next();
      Object value = p.getValue();
      PropertyType t = p.getType();

      Assert.assertNotNull(p);
      Assert.assertNotNull(t);

      switch (t) {
      case INTEGER:
        Integer n = (Integer) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_INTEGER, n);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_INTEGER.toString(), p.getValueAsString());
        break;
      case STRING:
        String s = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_STRING, s);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_STRING.toString(), p.getValueAsString());
        break;
      case BOOLEAN:
        Boolean b = (Boolean) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_BOOLEAN, b);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_BOOLEAN.toString(), p.getValueAsString());
        break;
      case DATETIME:
        Calendar c = (Calendar) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_DATETIME, c);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_DATETIME.toString(), p.getValueAsString());
        break;
      case DECIMAL:
        Number num = (Number) value;
        if (num instanceof Double) {
          Assert.assertEquals(Fixture.PROPERTY_VALUE_DOUBLE, num);
          Assert.assertEquals(Fixture.PROPERTY_VALUE_DOUBLE.toString(), p.getValueAsString());
        }
        else if (num instanceof Float) {
          Assert.assertEquals(Fixture.PROPERTY_VALUE_FLOAT, num);
          Assert.assertEquals(Fixture.PROPERTY_VALUE_FLOAT.toString(), p.getValueAsString());
        }
        else {
          Assert.fail("Number not supported: " + num.toString());
        }
        break;
      case HTML:
        String html = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_HTML, html);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_HTML.toString(), p.getValueAsString());
        break;
      case ID:
        String id = (String) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_ID, id);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_ID.toString(), p.getValueAsString());
        break;
      case URI:
        URI uri = (URI) value;
        Assert.assertEquals(Fixture.PROPERTY_VALUE_URI, uri);
        Assert.assertEquals(Fixture.PROPERTY_VALUE_URI.toString(), p.getValueAsString());
        break;
      default:
        Assert.fail("PropertyType not supported: " + t);
      }
    }
  }

  @Test
  public void readSingleProperty() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    Property<String> p = document.getProperty(CmisProperties.OBJECT_ID.value());
    Assert.assertNotNull(p);
    String v1 = p.getValue();
    Assert.assertNotNull(v1);

    String v2 = document.getPropertyValue(CmisProperties.OBJECT_ID.value());
    Assert.assertNotNull(v2);
    Assert.assertEquals(v1, v2);

  }

  @Test
  public void readMultiValueProperty() {
    String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + Fixture.DOCUMENT1_NAME;
    Document document = (Document) this.session.getObjectByPath(path);
    Assert.assertNotNull("document not found: " + path, document);

    Property<String> p = document.getProperty(Fixture.PROPERTY_NAME_STRING_MULTI_VALUED);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.isMultiValued());
    List<String> v1 = p.getValues();
    Assert.assertNotNull(v1);
    Assert.assertFalse(v1.isEmpty());

    List<String> v2 = document.getPropertyMultivalue(Fixture.PROPERTY_NAME_STRING_MULTI_VALUED);
    Assert.assertNotNull(v2);
    Assert.assertFalse(v2.isEmpty());
    Assert.assertEquals(v1, v2);
  }
}
