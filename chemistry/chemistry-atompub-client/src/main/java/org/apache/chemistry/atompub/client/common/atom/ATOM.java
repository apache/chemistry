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
package org.apache.chemistry.atompub.client.common.atom;

import javax.xml.namespace.QName;

/**
 *
 */
public interface ATOM {

    public final static String APP_NS = "http://www.w3.org/2007/app";

    public final static String ATOM_NS = "http://www.w3.org/2005/Atom";

    public final static QName FEED = new QName(ATOM_NS, "feed");

    public final static QName ENTRY = new QName(ATOM_NS, "entry");

    public final static QName LINK = new QName(ATOM_NS, "link");

    public final static QName COLLECTION = new QName(APP_NS, "collection");
}
