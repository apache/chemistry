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
 * A tree of objects of type T, where the children of a node are a {@link List}.
 */
public interface Tree<T> {

    /**
     * The root node of this tree. This is may be {@code null} to represent a
     * forest.
     */
    T getNode();

    /**
     * The children of this tree. This is never {@code null}.
     */
    List<Tree<T>> getChildren();

    /**
     * The number of nodes in this tree. {@code null} nodes are not counted.
     */
    int size();

}
