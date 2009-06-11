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
 *     Bogdan Stefanescu, Nuxeo
 */
package org.apache.chemistry.atompub;

import javax.xml.namespace.QName;

/**
 * Utility class providing various Atom- and AtomPub-related constants.
 */
public class Atom {

    // utility class
    private Atom() {
    }

    public static final String ATOM_NS = "http://www.w3.org/2005/Atom";

    public static final String APP_NS = "http://www.w3.org/2007/app";

    public static final QName ATOM_FEED = new QName(ATOM_NS, "feed");

    public static final QName ATOM_ENTRY = new QName(ATOM_NS, "entry");

    public static final QName ATOM_LINK = new QName(ATOM_NS, "link");

    public static final QName APP_COLLECTION = new QName(APP_NS, "collection");

}
