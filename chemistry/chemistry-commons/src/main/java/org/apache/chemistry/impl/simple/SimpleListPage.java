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
package org.apache.chemistry.impl.simple;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Paging;

/**
 * A simple implementation of {@link ListPage} based on {@link ArrayList}.
 */
public class SimpleListPage<T> extends ArrayList<T> implements ListPage<T> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    private static final ListPage EMPTY_LIST = new EmptyPagedList();

    @SuppressWarnings("unchecked")
    public static final <T> ListPage<T> emptyList() {
        return (ListPage<T>) EMPTY_LIST;
    }

    private static class EmptyPagedList<T> extends AbstractList<T> implements
            ListPage<T>, RandomAccess, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object obj) {
            return false;
        }

        @Override
        public T get(int index) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }

        // singleton
        private Object readResolve() {
            return EMPTY_LIST;
        }

        public boolean getHasMoreItems() {
            return false;
        }

        public int getNumItems() {
            return 0;
        }
    }

    protected boolean hasMoreItems;

    protected int numItems = -1;

    /**
     * @see ArrayList#ArrayList()
     */
    public SimpleListPage() {
        super();
    }

    /**
     * @see ArrayList#ArrayList(int)
     */
    public SimpleListPage(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @see ArrayList#ArrayList(Collection)
     */
    public SimpleListPage(Collection<? extends T> collection) {
        super(collection);
    }

    /**
     * Extracts part of a list according to given paging parameters.
     *
     * @param all the complete list
     * @param paging the paging info, which may be {@code null}
     * @return the page
     */
    public static ListPage<ObjectEntry> fromPaging(List<ObjectEntry> all,
            Paging paging) {
        int total = all.size();
        int fromIndex = paging == null ? 0 : paging.skipCount;
        if (fromIndex < 0 || fromIndex > total) {
            return emptyList();
        }
        int maxItems = paging == null ? -1 : paging.maxItems;
        if (maxItems <= 0) {
            maxItems = total;
        }
        int toIndex = fromIndex + maxItems;
        if (toIndex > total) {
            toIndex = total;
        }
        List<ObjectEntry> slice;
        if (fromIndex == 0 && toIndex == total) {
            slice = all;
        } else {
            slice = all.subList(fromIndex, toIndex);
        }
        SimpleListPage<ObjectEntry> page = new SimpleListPage<ObjectEntry>(
                slice);
        page.setHasMoreItems(toIndex < total);
        page.setNumItems(total);
        return page;
    }

    public boolean getHasMoreItems() {
        return hasMoreItems;
    }

    public int getNumItems() {
        return numItems;
    }

    /**
     * Sets whether the underlying collection has more items (skipped due to
     * paging) after those included in the list.
     */
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    /**
     * Sets the total number of items in the list (ignoring any paging).
     */
    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

}
