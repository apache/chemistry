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
package org.apache.chemistry.tck.atompub;

public class TCKSkipCapabilityException extends Exception {

	private static final long serialVersionUID = 3715821943383140356L;

	private String capability;
	private String required;
	private String actual;

    public TCKSkipCapabilityException(String capability, String required, String actual) {
        super("Expected capability " + capability + " value of " + required + " but was " + actual);
        this.capability = capability;
        this.required = required;
        this.actual = actual;
    }
    
    public String getCapability() {
    	return capability;
    }
    
    public String getRequired() {
    	return required;
    }

    public String getActual() {
    	return actual;
    }
}
