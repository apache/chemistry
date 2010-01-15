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

package org.apache.chemistry.shell.jline;

import java.util.List;

import jline.Completor;

import org.apache.chemistry.shell.app.Console;
import org.apache.chemistry.shell.app.Context;
import org.apache.chemistry.shell.util.Path;

public class ContextItemCompletor implements Completor {

    protected void collectNames(String[] keys, String prefix, List candidates) {
        for (String key : keys) {
            if (key.startsWith(prefix)) {
                key = key.replace(" ", "\\ ");
                candidates.add(key);
            }
        }
    }

    public int complete(String buffer, int cursor, List candidates) {
        if (buffer == null) {
            buffer = "";
        }

        Context ctx;
        Path path = new Path(buffer);
        String prefix = path.getLastSegment();
        if (prefix == null) {
            ctx = Console.getDefault().getApplication().getContext();
            prefix = "";
        } else if (path.segmentCount() == 1) {
            ctx = Console.getDefault().getApplication().getContext();
        } else {
            path = path.removeLastSegments(1);
            ctx = Console.getDefault().getApplication().resolveContext(path);
        }
        if (ctx != null) {
            collectNames(ctx.entries(), prefix, candidates);
            return buffer.length()-prefix.length();
        } else {
            return -1;
        }
    }

}
