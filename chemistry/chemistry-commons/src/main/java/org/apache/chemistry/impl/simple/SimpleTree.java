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

import java.util.Collections;
import java.util.List;

import org.apache.chemistry.Tree;

/**
 * A simple implementation of {@link Tree}.
 */
public class SimpleTree<T> implements Tree<T> {

    private static final long serialVersionUID = 1L;

    protected T node;

    protected List<Tree<T>> children;

    public SimpleTree(T node, List<Tree<T>> children) {
        setNode(node);
        setChildren(children);
    }

    public T getNode() {
        return node;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public void setNode(T node) {
        this.node = node;
    }

    public void setChildren(List<Tree<T>> children) {
        if (children == null) {
            children = Collections.emptyList();
        }
        this.children = children;
    }

    public int size() {
        int n = 0;
        if (node != null) {
            n++;
        }
        for (Tree<T> t : children) {
            n += t.size();
        }
        return n;
    }

}
