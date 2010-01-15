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

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    private StringUtils() {
    }

    public static String[] split(String str, char delimiter, boolean trim) {
        int s = 0;
        int e = str.indexOf(delimiter, s);
        if (e == -1) {
            if (trim) {
                str = str.trim();
            }
            return new String[] {str};
        }
        List<String> ar = new ArrayList<String>();
        do {
            String segment = str.substring(s, e);
            if (trim) {
                segment = segment.trim();
            }
            ar.add(segment);
            s = e + 1;
            e = str.indexOf(delimiter, s);
        } while (e != -1);

        int len = str.length();
        if (s < len) {
            String segment = str.substring(s);
            if (trim) {
                segment = segment.trim();
            }
            ar.add(segment);
        } else {
            ar.add("");
        }

        return ar.toArray(new String[ar.size()]);
    }

    public static String[] tokenize(String text) {
        boolean esc = false;
        boolean inString = false;
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder buf = new StringBuilder();

        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (esc) {
                switch (c) {
                    case 'n':
                        buf.append('\n');
                        break;
                    case 't':
                        buf.append('\t');
                        break;
                    default:
                        buf.append(c);
                }
                esc = false;
                continue;
            }
            if (inString && c != '"') {
                buf.append(c);
                continue;
            }
            switch (c) {
                case ' ':
                case '\t':
                    if (buf.length() > 0) {
                        tokens.add(buf.toString());
                        buf.setLength(0);
                    }
                    break;
                case '\\':
                    esc = true;
                    break;
                case '"':
                    inString = !inString;
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }

        if (buf.length() > 0) {
            tokens.add(buf.toString());
            buf.setLength(0);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

}
