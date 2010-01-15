/*
 * (C) Copyright 2009-2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   Bogdan Stefanescu (bs@nuxeo.com), Nuxeo
 *   Stefane Fermigier (sf@nuxeo.com), Nuxeo
 *   Florent Guillaume (fg@nuxeo.com), Nuxeo
 */

package org.apache.chemistry.shell.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.Document;
import org.apache.chemistry.Property;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.shell.app.Console;
import org.apache.chemistry.shell.command.CommandException;

public class SimplePropertyManager {

    protected final CMISObject item;

    public SimplePropertyManager(CMISObject item) {
        this.item = item;
    }

    public String getPropertyAsString(String name) {
        Property p = item.getProperty(name);
        if (p == null) {
            return "[null]";
        }
        Serializable val = p.getValue();
        return val != null ? val.toString() : "[null]";
    }

    public void setProperty(String name, Serializable value) throws Exception{
        item.setValue(name, value);
        item.save();
    }

    public void dumpProperties() {
        Map<String, Property> props = item.getProperties();

        List<String> keys = new LinkedList<String>(props.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            Property prop = props.get(key);
            Object value = prop.getValue();
            Console.getDefault().println(key + " = " + (value != null ? value : "[null]"));
        }
    }

    public ContentStream getStream() throws IOException {
        return  item.getContentStream(null);
    }

    public void setStream(InputStream in, String name) throws Exception {
        if (item instanceof Document) {
            Document doc = (Document) item;
            String mimeType = MimeTypeHelper.getMimeType(name);
            ContentStream stream = new SimpleContentStream(in, mimeType, name);
            doc.setContentStream(stream);
            doc.save();
        } else {
            throw new CommandException("Target object is not a Document, can not set stream");
        }
    }

}
