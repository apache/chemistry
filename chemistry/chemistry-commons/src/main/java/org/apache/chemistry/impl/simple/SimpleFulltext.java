/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.impl.simple;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Simple utility class that does basic and naive fulltext match.
 */
public class SimpleFulltext {

    private SimpleFulltext() {
    }

    /**
     * Extracts the words from a SimpleData for fulltext indexing.
     */
    protected static Set<String> parseFulltext(Map<String, Serializable> data) {
        Set<String> set = new HashSet<String>();
        for (Entry<String, Serializable> es : data.entrySet()) {
            Object value = es.getValue();
            if (value instanceof String) {
                parseFullText((String) value, set);
            } else if (value instanceof String[]) {
                for (String v : (String[]) value) {
                    parseFullText(v, set);
                }
            }
        }
        return set;
    }

    protected static void parseFullText(String string, Set<String> set) {
        if (string == null) {
            return;
        }
        for (String word : wordPattern.split(string)) {
            String w = parseWord(word);
            if (w != null) {
                set.add(w);
            }
        }
    }

    /**
     * Checks if the passed query expression matches the fulltext.
     */
    // TODO XXX implement actual CMIS 1.0 language
    protected static boolean matchesFullText(Set<String> fulltextWords,
            String query) {
        if (fulltextWords == null || query == null) {
            return false;
        }
        Set<String> queryWords = split(query, ' ');
        if (queryWords.isEmpty()) {
            return false;
        }
        for (String word : queryWords) {
            if (!fulltextWords.contains(word)) {
                return false;
            }
        }
        return true;
    }

    // ----- simple parsing, don't try to be exhaustive -----

    private static final Pattern wordPattern = Pattern.compile("[\\s\\p{Punct}]+");

    private static final String UNACCENTED = "aaaaaaaceeeeiiii\u00f0nooooo\u00f7ouuuuy\u00fey";

    private static final String STOPWORDS = "a an are and as at be by for from how "
            + "i in is it of on or that the this to was what when where who will with "
            + "car donc est il ils je la le les mais ni nous or ou pour tu un une vous "
            + "www com net org";

    private static final Set<String> stopWords = new HashSet<String>(split(
            STOPWORDS, ' '));

    protected static final String parseWord(String string) {
        int len = string.length();
        if (len < 3) {
            return null;
        }
        StringBuilder buf = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(string.charAt(i));
            if (c == '\u00e6') {
                buf.append("ae");
            } else if (c >= '\u00e0' && c <= '\u00ff') {
                buf.append(UNACCENTED.charAt((c) - 0xe0));
            } else if (c == '\u0153') {
                buf.append("oe");
            } else {
                buf.append(c);
            }
        }
        // simple heuristic to remove plurals
        int l = buf.length();
        if (l > 3 && buf.charAt(l - 1) == 's') {
            buf.setLength(l - 1);
        }
        String word = buf.toString();
        if (stopWords.contains(word)) {
            return null;
        }
        return word;
    }

    protected static Set<String> split(String string, char sep) {
        int len = string.length();
        if (len == 0) {
            return Collections.emptySet();
        }
        int end = string.indexOf(sep);
        if (end == -1) {
            return Collections.singleton(string);
        }
        Set<String> set = new HashSet<String>();
        int start = 0;
        do {
            String segment = string.substring(start, end);
            set.add(segment);
            start = end + 1;
            end = string.indexOf(sep, start);
        } while (end != -1);
        if (start < len) {
            set.add(string.substring(start));
        } else {
            set.add("");
        }
        return set;
    }

}
