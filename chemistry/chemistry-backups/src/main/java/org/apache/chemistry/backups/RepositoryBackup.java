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
 *     matic
 */
package org.apache.chemistry.backups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import org.apache.chemistry.repository.Repository;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author matic
 * 
 */
public class RepositoryBackup {

    public final URL location;

    public RepositoryBackup(URL url) {
        this.location = url;
    }

    @XStreamAlias("data")
    public static class Info {
        String name = "";
        long timestamp = 0L;
        public String getName() {
            return name;
        }
        public long getTimestamp() {
            return timestamp;
        }
    }

    @XStreamAlias("data")
    protected static class Data {
        String name;
        long timestamp;
        Repository repository;
    }

    public void save(Repository repo) throws IOException, URISyntaxException {
        Data data = new Data();
        data.timestamp = Calendar.getInstance().getTimeInMillis();
        data.name = repo.getName();
        data.repository = repo;
        File file = new File(location.toURI());
        file.delete();
        file.createNewFile();
        OutputStream stream = new FileOutputStream(file);
        try {
            new DataSerializer<Data>(Data.class).toXML(data, stream);
        } finally {
            stream.close();
        }
    }

    public Repository load() throws IOException {
        InputStream stream = location.openStream();
        try {
            Data data = new DataSerializer<Data>(Data.class).fromXML(stream);
            return data.repository;
        } finally {
            stream.close();
        }
    }
    
    public Info loadInfo() throws IOException {
        InputStream stream = location.openStream();
        try {
            return new DataSerializer<Info>(Info.class).fromXML(stream);
        } finally {
            stream.close();
        }
    }
}
