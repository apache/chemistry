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
 */
package org.apache.chemistry.tck.atompub.utils;

import org.apache.chemistry.tck.atompub.TCKMessageWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogMessageWriter implements TCKMessageWriter {

    public static final Log logger = LogFactory.getLog(TCKMessageWriter.class);

    public void info(String message) {
        if (logger.isInfoEnabled())
            logger.info(message);
    }

    public void warn(String message) {
        if (logger.isWarnEnabled())
            logger.warn(message);
    }

    public void trace(String message) {
        if (logger.isTraceEnabled())
            logger.trace(message);
    }

}
