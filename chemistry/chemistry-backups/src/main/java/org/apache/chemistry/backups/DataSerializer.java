/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Stephane Lacoin [aka matic] (Nuxeo EP Software Engineer)
 */
package org.apache.chemistry.backups;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author Stephane Lacoin [aka matic] (Nuxeo EP Software Engineer)
 * 
 * @depend - - - InputStream
 */
public class DataSerializer<T> {

    public DataSerializer(Class<T> clazz) {
        xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {

                    @SuppressWarnings("unchecked")
                    public boolean shouldSerializeMember(Class definedIn,
                            String fieldName) {
                        return definedIn != Object.class ? super.shouldSerializeMember(
                                definedIn, fieldName)
                                : false;
                    }

                };
            }

        };
        xstream.processAnnotations(clazz);
    }

    protected final XStream xstream;

    
    @SuppressWarnings("unchecked")
    public T fromXML(InputStream input)  {
        return (T) xstream.fromXML(input);
    }
    
    public void toXML(T data, OutputStream output)  {
        xstream.toXML(data,output);
    }

}
