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
package org.apache.chemistry.tck.atompub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;


/**
 * Load resource from classpath
 */
public class ResourceLoader {
    
    private String basePath;

    public ResourceLoader() {
        this(null);
    }

    public ResourceLoader(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Load text from file specified by class path
     * 
     * @param classPath
     *            XML file
     * @return XML
     * @throws IOException
     */
    public String load(String path) throws IOException {
        String fullPath = (basePath == null) ? path : basePath + path;
        InputStream input = getClass().getResourceAsStream(fullPath);
        if (input == null) {
            throw new IOException(fullPath + " not found.");
        }

        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
        StringWriter writer = new StringWriter();

        try {
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            writer.flush();
        } finally {
            reader.close();
            writer.close();
        }

        return writer.toString();
    }

}
