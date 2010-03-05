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
 *     David Ward, Alfresco
 */
package org.apache.chemistry.abdera.ext;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * CMIS Change Event Info for the Abdera ATOM library.
 */
public class CMISChangeEventInfo extends ExtensibleElementWrapper {

    public CMISChangeEventInfo(Element internal) {
        super(internal);
    }

    public CMISChangeEventInfo(Factory factory) {
        super(factory, CMISConstants.CHANGE_EVENT_INFO);
    }

    public String getChangeType() {
        return getFirstChild(CMISConstants.CHANGE_TYPE).getText();
    }
}
