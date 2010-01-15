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

import org.apache.chemistry.shell.util.Path;

/**
 * Auto-completes remote item names.
 */
public class ItemNameCompletor implements Completor {

    private final JLineConsole console;

    public ItemNameCompletor(JLineConsole console) {
        this.console = console;
    }

    public int complete(String buffer, int cursor, List candidates) {
        if (buffer == null) {
            buffer = "";
        }
        Path path = new Path(buffer);
        String prefix = path.getLastSegment();
        if (path.hasTrailingSeparator()) {
            prefix = "";
        } else {
            path = path.removeLastSegments(1);
        }

        if (prefix == null) {
            prefix = "";
        }

        try {
            String[] names = console.getApplication().getContext().entries();
            if (names == null || names.length == 0) {
                return -1;
            }

            if (buffer.length() == 0) {
                for (String name : names) {
                    candidates.add(name);
                }
            } else {
                for (String name : names) {
                    if (name.startsWith(prefix)) {
                        candidates.add(name);
                    }
                }
            }

            return buffer.length()-prefix.length();

        } catch (Exception e) {
            return -1;
        }
    }

}
