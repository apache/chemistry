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
package org.apache.chemistry;

/**
 * The operation is attempting to perform an action on a non-latest version of a
 * {@link Document} but this is not possible.
 */
public class VersioningException extends CMISRuntimeException {

    private static final long serialVersionUID = 1L;

    public VersioningException() {
        super();
    }

    public VersioningException(String message) {
        super(message);
    }

    public VersioningException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersioningException(Throwable cause) {
        super(cause);
    }

}
