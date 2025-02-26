// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.hivevm.doc.api.DocumentNode;

/**
 * The {@link NodeBuilder} implements a basic builder for the {@link DocumentNode} interface.
 */
public abstract class NodeBuilder implements DocumentNode {

    private final List<DocumentNode> nodes    = new ArrayList<>();
    private final List<NodeBuilder>  builders = new ArrayList<>();

    /**
     * Get the child nodes.
     */
    protected final <N extends DocumentNode> Collection<N> nodes() {
        return (Collection<N>) builders;
    }

    protected final void forEach(Consumer<NodeBuilder> action) {
        builders.forEach(n -> action.accept(n));
    }

    /**
     * Add a child {@link DocumentNode}.
     *
     * @param node
     */
    protected final <N extends NodeBuilder> N add(N node) {
        this.nodes.add(node);
        this.builders.add(node);
        return node;
    }
}
