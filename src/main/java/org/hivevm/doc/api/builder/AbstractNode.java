// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@link AbstractNode} implements a basic builder for the {@link Node} interface.
 */
public abstract class AbstractNode implements Node {

    private final List<Node> nodes = new ArrayList<>();

    /**
     * Returns an iterator over the child {@link Node}'s.
     */
    @Override
    public final Iterator<Node> iterator() {
        return nodes.iterator();
    }

    /**
     * Get the child nodes.
     */
    public final Stream<Node> stream() {
        return this.nodes.stream();
    }

    /**
     * Add a child {@link Node}.
     *
     * @param node
     */
    protected final <N extends Node> N add(N node) {
        this.nodes.add(node);
        return node;
    }
}
