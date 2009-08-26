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
 *     Gabriele Columbro, Alfresco
 */
package org.apache.chemistry.tck.atompub.test.custom;

import org.apache.chemistry.tck.atompub.TCKTest;
import org.apache.chemistry.tck.atompub.utils.ResourceLoader;

public class TCKCustomTest extends TCKTest {
    
    protected ResourceLoader customTemplates;

    @Override
    public void setUp() {
        super.setUp();

        // construct test custom templates
        customTemplates = new ResourceLoader("/org/apache/chemistry/tck/atompub/templates/custom/");
    }
}
