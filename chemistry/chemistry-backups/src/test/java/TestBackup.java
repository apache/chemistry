/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Stephane Lacoin [aka matic] (Nuxeo EP Software Engineer)
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.Connection;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.backups.RepositoryBackup;
import org.apache.chemistry.backups.RepositoryBackup.Info;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.test.RepositoryTestFactory;



public class TestBackup extends TestCase {
    
    protected URL doResolvePath(String name) throws MalformedURLException {
        URL folderLocation = getClass().getResource("/");
        URL fileLocation = new URL(folderLocation.toExternalForm() + name + ".xml");
        return fileLocation;
    }
    
    public Folder getRootFolder(Repository repo) {
        Connection conn = repo.getConnection(null);
        return conn.getRootFolder();
    }
    
    public List<ObjectEntry> getEntries(Repository repo) {
        Connection conn = repo.getConnection(null);
        Folder folder = conn.getRootFolder();
        return folder.getChildren(null, null);
    }
    
    public void testSaveAndRestore() throws IOException, URISyntaxException {
        SimpleRepository source = RepositoryTestFactory.makeRepository();
        RepositoryBackup backup = new RepositoryBackup(doResolvePath("backup"));
        backup.save(source);
        Info info = backup.loadInfo();
        String repoName = source.getName();
        String infoName = info.getName();
        assertEquals(repoName,infoName);
        Repository copy = backup.load();
        assertNotNull(copy);
        List<ObjectEntry> sourceEntries = getEntries(source);
        List<ObjectEntry> copyEntries = getEntries(copy);
        assertEquals(sourceEntries.size(), copyEntries.size());
        assertEquals(sourceEntries.get(0), copyEntries.get(0));
    }

   
}
