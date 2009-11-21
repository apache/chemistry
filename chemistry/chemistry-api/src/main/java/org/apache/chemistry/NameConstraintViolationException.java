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
 * The repository is not able to store the object that the user is
 * creating/updating due to a name constraint violation.
 */
public class NameConstraintViolationException extends CMISException {

    private static final long serialVersionUID = 1L;

    public NameConstraintViolationException() {
        super();
    }

    public NameConstraintViolationException(String message) {
        super(message);
    }

    public NameConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameConstraintViolationException(Throwable cause) {
        super(cause);
    }

}
