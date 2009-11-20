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

import java.util.List;

/**
 * A {@link List} with paging information.
 * <p>
 * The list is a sublist (a page) of a bigger underlying list, and information
 * about the page in the underlying list can be retrieved.
 */
public interface ListPage<T> extends List<T> {

    /**
     * Checks whether the underlying list has more items (skipped due to paging)
     * after those included in the list.
     * <p>
     * If {@code true}, a request with a larger {@link Paging#skipCount} or
     * larger {@link Paging#maxItems} is expected to return additional results.
     * <p>
     * A client should never rely on this value to be exact, because the
     * contents of the repository may change between two calls.
     */
    boolean getHasMoreItems();

    /**
     * Gets the total number of items in the underlying list (ignoring any
     * paging).
     * <p>
     * If not known, this will be {@code -1}.
     * <p>
     * A client should never rely on this value to be exact, because the
     * contents of the repository may change between two calls.
     */
    int getNumItems();

}
