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
 *     David Caruana, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.Feed;


/**
 * CMIS Children for the Abdera ATOM library.
 * 
 * Encapsulates access to nested feed of children
 */
public class CMISChildren extends ElementWrapper {
    
    public CMISChildren(Element internal) {
        super(internal);
    }

    public CMISChildren(Factory factory) {
        super(factory, CMISConstants.CHILDREN);
    }

    /**
     * Gets feed of children
     * 
     * @return
     */
    public Feed getFeed() {
        Element child = getFirstChild();
        if (child != null && child instanceof Feed)
            return (Feed)child;
        return null;
    }

}
