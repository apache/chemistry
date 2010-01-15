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

import org.junit.Assert;
import org.junit.Test;
import org.apache.chemistry.shell.util.StringUtils;

public class TestStringUtils extends Assert {

    @Test
    public void testSplit() {
        String[] res = StringUtils.split("abc", '|', true);
        assertArrayEquals(new String[] {"abc"}, res);

        res = StringUtils.split(" abc ", '|', true);
        assertArrayEquals(new String[] {"abc"}, res);

        res = StringUtils.split(" abc ", '|', false);
        assertArrayEquals(new String[] {" abc "}, res);

        res = StringUtils.split("a|b|c", '|', true);
        assertArrayEquals(new String[] {"a", "b", "c"}, res);

        res = StringUtils.split(" a | b |c ", '|', true);
        assertArrayEquals(new String[] {"a", "b", "c"}, res);

        res = StringUtils.split(" a | b |c ", '|', false);
        assertArrayEquals(new String[] {" a ", " b ", "c "}, res);
    }

    @Test
    public void testTokenizeSimple() {
        String[] res = StringUtils.tokenize("a bc def");
        assertArrayEquals(new String[] {"a", "bc", "def"}, res);
    }

    @Test
    public void testTokenizeEscape() {
        String[] res = StringUtils.tokenize("a\\ bc def");
        assertArrayEquals(new String[] {"a bc", "def"}, res);

        res = StringUtils.tokenize("a\\\\\\n\\t def");
        assertArrayEquals(new String[] {"a\\\n\t", "def"}, res);

        res = StringUtils.tokenize("a\\bc def");
        assertArrayEquals(new String[] {"abc", "def"}, res);
    }

    @Test
    public void testTokenizeString() {
        String[] res = StringUtils.tokenize("a \"bc def\"");
        assertArrayEquals(new String[] {"a", "bc def"}, res);
    }

    @Test
    // "" is stronger than \
    public void testTokenizeBoth() {
        String[] res = StringUtils.tokenize("a \"bc\\ \\ndef\"");
        assertArrayEquals(new String[] {"a", "bc\\ \\ndef"}, res);
    }

}
