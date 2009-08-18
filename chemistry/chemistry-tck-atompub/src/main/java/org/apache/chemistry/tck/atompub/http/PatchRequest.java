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
package org.apache.chemistry.tck.atompub.http;

import java.io.UnsupportedEncodingException;

/**
 * PATCH Request
 */
public class PatchRequest extends Request {

    public PatchRequest(String uri, String patch, String contentType) throws UnsupportedEncodingException {
        super("patch", uri);
        setBody(getEncoding() == null ? patch.getBytes() : patch.getBytes(getEncoding()));
        setType(contentType);
    }

    public PatchRequest(String uri, byte[] patch, String contentType) {
        super("patch", uri);
        setBody(patch);
        setType(contentType);
    }
}
