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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jline.ANSIBuffer;

/**
 * An utility class to add ANSI colors to objects in folder listings.
 * <p>
 * Disabled by default. Enabled only when used in interactive mode.
 */
public class ColorHelper {

    public static final int OFF = 0;
    public static final int BOLD = 1;
    public static final int UNDERSCORE = 4;
    public static final int BLINK = 5;
    public static final int REVERSE = 7;
    public static final int CONCEALED = 8;
    public static final int FG_BLACK = 30;
    public static final int FG_RED = 31;
    public static final int FG_GREEN = 32;
    public static final int FG_YELLOW = 33;
    public static final int FG_BLUE = 34;
    public static final int FG_MAGENTA = 35;
    public static final int FG_CYAN = 36;
    public static final int FG_WHITE = 37;
    public static final char ESC = 27;

    protected static final Map<String,Integer> ansiCodes = new HashMap<String, Integer>();
    protected static final Map<String,Integer> colorMap = new HashMap<String, Integer>();

    protected static boolean enabled;

    static {
        ansiCodes.put("white", FG_WHITE);
        ansiCodes.put("black", FG_BLACK);
        ansiCodes.put("blue", FG_BLUE);
        ansiCodes.put("cyan", FG_CYAN);
        ansiCodes.put("magenta", FG_MAGENTA);
        ansiCodes.put("green", FG_GREEN);
        ansiCodes.put("red", FG_RED);
        ansiCodes.put("yellow", FG_YELLOW);
        ansiCodes.put("blink", BLINK);
        ansiCodes.put("bold", BOLD);
        ansiCodes.put("underscore", UNDERSCORE);
        ansiCodes.put("reverse", REVERSE);
        ansiCodes.put("concealed", CONCEALED);

        Properties props = new Properties();
        try {
            String mapStr = System.getProperty("chemistry.shell.colorMap");
            if (mapStr != null) {
                props.load(new ByteArrayInputStream(mapStr.getBytes()));
            } else {
                URL url = ColorHelper.class.getClassLoader().getResource("color.properties");
                if (url != null) {
                    InputStream in = url.openStream();
                    props.load(in);
                    in.close();
                }
            }
            for (Map.Entry<Object,Object> entry : props.entrySet()) {
                String val = (String) entry.getValue();
                Integer code = ansiCodes.get(val);
                if (code == null) {
                    System.err.println("Skipping unknown color code: "+val);
                } else {
                    colorMap.put((String) entry.getKey(), code);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load color map");
        }
    }

    // Utility class.
    private ColorHelper() {
    }

    public static void enable() {
        enabled = true;
    }

    private static boolean supportsColor() {
        String osName = System.getProperty("os.name");
        return enabled && !osName.toLowerCase().contains("windows");
    }

    public static String decorateName(String name, String color) {
        Integer code = ansiCodes.get(color);
        return code != null ? decorateName(name, code.intValue()) : name;
    }

    public static String decorateNameByType(String name, String type) {
        Integer color = colorMap.get(type);
        if (color != null) {
            return decorateName(name, color);
        }
        return name;
    }

    public static String decorateName(String name, int color) {
        // don't add any color for crappy terminals
        if (!supportsColor()) {
            return name;
        }
        ANSIBuffer buf = new ANSIBuffer();
        return buf.attrib(name, color).toString();
    }

    public static String blue(String name) {
        return decorateName(name, FG_BLUE);
    }

    public static String green(String name) {
        return decorateName(name, FG_GREEN);
    }

    public static String yellow(String name) {
        return decorateName(name, FG_YELLOW);
    }

    public static String red(String name) {
        return decorateName(name, FG_RED);
    }

    public static String cyan(String name) {
        return decorateName(name, FG_CYAN);
    }

    public static String black(String name) {
        return decorateName(name, FG_BLACK);
    }

    public static String magenta(String name) {
        return decorateName(name, FG_MAGENTA);
    }

    public static String white(String name) {
        return decorateName(name, FG_WHITE);
    }

    public static String blink(String name) {
        return decorateName(name, BLINK);
    }

    public static String bold(String name) {
        return decorateName(name, BOLD);
    }

    public static String underscore(String name) {
        return decorateName(name, UNDERSCORE);
    }

    public static String reverse(String name) {
        return decorateName(name, REVERSE);
    }

}
