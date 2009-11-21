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
 * The operation attempts to set the content stream for a {@link Document} that
 * already has a content stream without explicitly specifying the {@code
 * overwrite} parameter.
 */
public class ContentAlreadyExistsException extends CMISException {

    private static final long serialVersionUID = 1L;

    public ContentAlreadyExistsException() {
        super();
    }

    public ContentAlreadyExistsException(String message) {
        super(message);
    }

    public ContentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentAlreadyExistsException(Throwable cause) {
        super(cause);
    }

}
