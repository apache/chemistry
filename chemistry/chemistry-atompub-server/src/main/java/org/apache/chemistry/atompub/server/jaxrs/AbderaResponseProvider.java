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
package org.apache.chemistry.atompub.server.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.chemistry.atompub.server.SizedMediaResponseContext;

/**
 * A JAX-RS MessageBodyWriter that knows how to write an Abdera ResponseContext.
 */
// @Produces of text/plain is needed otherwise RESTEasy will use its
// DefaultTextPlain writer to write text/plain objects and the ResponseContext
// is not written correctly.
@Provider
@Produces( { "*/*", "text/plain" })
public class AbderaResponseProvider implements
        MessageBodyWriter<ResponseContext> {

    public long getSize(ResponseContext responseContext, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (responseContext instanceof SizedMediaResponseContext) {
            return ((SizedMediaResponseContext) responseContext).getSize();
        }
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return ResponseContext.class.isAssignableFrom(type);
    }

    public void writeTo(ResponseContext responseContext, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        responseContext.writeTo(entityStream);
    }

}
