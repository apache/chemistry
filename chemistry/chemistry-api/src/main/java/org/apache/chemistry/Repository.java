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

import java.io.Serializable;
import java.util.Map;

/**
 * A CMIS Repository.
 * <p>
 * Basic repository information is available through {@link RepositoryEntry}.
 * <p>
 * Types are managed through {@link TypeManager}.
 */
public interface Repository extends RepositoryEntry, TypeManager {

    /**
     * Gets a new connection to this repository.
     *
     * @return the connection
     */
    Connection getConnection(Map<String, Serializable> parameters);

    /**
     * Gets a new connection using the SPI for this repository.
     * <p>
     * The SPI is a connection providing access to lower-level features.
     *
     * @return the SPI connection
     */
    SPI getSPI();

    /**
     * Gets an extension service on this repository.
     * <p>
     * This is an optional operation and may always return {@code null} if not
     * supported.
     *
     * @param klass the interface for the requested extension
     * @return the extension instance if any implementation was found, or
     *         {@code null} if not
     */
    <T> T getExtension(Class<T> klass);

    /*
     * ----- Repository Services -----
     */

    /**
     * Returns information about the repository and the capabilities it
     * supports.
     *
     * @return information about the repository
     */
    RepositoryInfo getInfo();

}
