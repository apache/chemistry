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
 * Paging requested.
 * <p>
 * An instance of this class is provided to methods that will return a
 * {@link ListPage}.
 */
public class Paging {

    /**
     * The maximum number of objects to return, or {@code 0} for a
     * repository-specific default
     */
    public final int maxItems;

    /**
     * The number of objects to skip in the returned list. {@code 0} means start
     */
    public final int skipCount;

    /**
     * Constructs paging information with the given parameters
     *
     * @param maxItems the maximum number of objects to return, or {@code 0} for
     *            a repository-specific default
     * @param skipCount the number of objects to skip in the returned list
     */
    public Paging(int maxItems, int skipCount) {
        this.maxItems = maxItems;
        this.skipCount = skipCount;
    }

}
