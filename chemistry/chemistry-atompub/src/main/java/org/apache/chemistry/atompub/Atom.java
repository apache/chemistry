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

    public static final String MEDIA_TYPE_ATOM = "application/atom+xml";

    public static final String MEDIA_TYPE_ATOM_ENTRY = "application/atom+xml;type=entry";

    public static final String MEDIA_TYPE_ATOM_FEED = "application/atom+xml;type=feed";

    public static final String MEDIA_TYPE_ATOM_SERVICE = "application/atomsvc+xml";

    /*
     * http://www.iana.org/assignments/link-relations/link-relations.xhtml
     */

    public static final String LINK_SELF = "self";

    public static final String LINK_SERVICE = "service";

    public static final String LINK_DESCRIBED_BY = "describedby";

    public static final String LINK_VIA = "via";

    public static final String LINK_EDIT_MEDIA = "edit-media";

    public static final String LINK_EDIT = "edit";

    public static final String LINK_ALTERNATE = "alternate";

    public static final String LINK_FIRST = "first";

    public static final String LINK_LAST = "last";

    public static final String LINK_PREVIOUS = "previous";

    public static final String LINK_NEXT = "next";

    /*
     * http://www.ietf.org/id/draft-divilly-atom-hierarchy-03.txt
     */

    public static final String LINK_UP = "up";

    public static final String LINK_DOWN = "down";

    /*
     * TODO Will be updated by 0.63 to use properly
     * http://www.ietf.org/id/draft-brown-versioning-link-relations-01.txt
     */

    public static final String LINK_VERSION_HISTORY = "allversions"; // TODO

    public static final String LINK_LATEST_VERSION = "latestversion"; // TODO

    public static final String LINK_WORKING_COPY = "pwc"; // TODO

}
