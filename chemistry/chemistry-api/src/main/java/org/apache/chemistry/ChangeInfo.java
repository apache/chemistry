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

import java.util.Calendar;

/**
 * Information about a change event returned from
 * {@link ObjectEntry#getChangeInfo}.
 *
 * @see ObjectEntry#getChangeInfo
 * @see SPI#getChangeLog
 */
public interface ChangeInfo {

    /**
     * Gets the type of change to the object.
     */
    ChangeType getType();

    /**
     * Gets the time of change to the object.
     */
    Calendar getDateTime();

    // TODO extensions

}
