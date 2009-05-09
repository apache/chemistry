/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.apache.chemistry.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.impl.simple.SimpleType;

/**
 * Helpers to create a basic repository.
 */
public class RepositoryCreationHelper {

    // Utility class
    private RepositoryCreationHelper() {
    }

    public static final String TEST_FILE_CONTENT = "This is a test file.\nTesting, testing...\n";

    public static Repository makeRepository(String rootId) throws IOException {
        PropertyDefinition p1 = new SimplePropertyDefinition("title",
                "def:title", "Title", "", false, PropertyType.STRING, false,
                null, false, false, "", Updatability.READ_WRITE, true, true, 0,
                null, null, -1, null, null);
        PropertyDefinition p2 = new SimplePropertyDefinition("description",
                "def:description", "Description", "", false,
                PropertyType.STRING, false, null, false, false, "",
                Updatability.READ_WRITE, true, true, 0, null, null, -1, null,
                null);
        PropertyDefinition p3 = new SimplePropertyDefinition("date",
                "def:date", "Date", "", false, PropertyType.DATETIME, false,
                null, false, false, null, Updatability.READ_WRITE, true, true,
                0, null, null, -1, null, null);
        SimpleType dt = new SimpleType("doc", "document", "Doc", "My Doc Type",
                BaseType.DOCUMENT, "", true, true, true, true, true, true,
                ContentStreamPresence.ALLOWED, null, null, Arrays.asList(p1,
                        p2, p3));
        SimpleType ft = new SimpleType("fold", "folder", "Fold",
                "My Folder Type", BaseType.FOLDER, "", true, true, true, true,
                false, false, ContentStreamPresence.NOT_ALLOWED, null, null,
                Arrays.asList(p1, p2));
        SimpleRepository repo = new SimpleRepository("test", Arrays.asList(dt,
                ft), rootId);
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();

        Folder folder1 = root.newFolder("fold");
        folder1.setName("folder 1");
        folder1.setValue("title", "The folder 1 description");
        folder1.setValue("description", "folder 1 title");
        folder1.save();

        Folder folder2 = folder1.newFolder("fold");
        folder2.setName("folder 2");
        folder2.setValue("title", "The folder 2 description");
        folder2.setValue("description", "folder 2 title");
        folder2.save();

        Document doc1 = folder1.newDocument("doc");
        doc1.setName("doc 1");
        doc1.setValue("title", "doc 1 title");
        doc1.setValue("description", "The doc 1 descr");
        doc1.save();

        Document doc2 = folder2.newDocument("doc");
        doc2.setName("doc 2");
        doc2.setValue("title", "doc 2 title");
        doc2.setValue("description", "The doc 2 descr");
        doc2.save();

        Document doc3 = folder2.newDocument("doc");
        doc3.setName("doc 3");
        doc3.setValue("title", "doc 3 title");
        doc3.setValue("description", "The doc 3 descr");
        ContentStream cs = new SimpleContentStream(
                TEST_FILE_CONTENT.getBytes("UTF-8"), "text/plain", "doc3.txt",
                null);
        doc3.setContentStream(cs);
        doc3.save();

        Document doc4 = folder2.newDocument("doc");
        doc4.setName("dog.jpg");
        doc4.setValue("title", "A Dog");
        doc4.setValue("description", "This is a small dog");
        InputStream stream = MainServlet.class.getResourceAsStream("/dog.jpg");
        cs = new SimpleContentStream(stream, "image/jpeg", "dog.jpg", null);
        doc4.setContentStream(cs);
        doc4.save();

        conn.close();
        return repo;
    }

}
