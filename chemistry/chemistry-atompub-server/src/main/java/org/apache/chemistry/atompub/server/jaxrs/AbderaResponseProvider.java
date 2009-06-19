package org.apache.chemistry.atompub.server.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.protocol.server.ResponseContext;

/**
 * A JAX-RS MessageBodyWriter that knows how to write an Abdera ResponseContext.
 */
@Provider
public class AbderaResponseProvider implements
        MessageBodyWriter<ResponseContext> {

    public long getSize(ResponseContext responseContext, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType) {
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
