// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The {@link NodeBuilder} class defines a generic node for a {@link Node} structure.
 */
public abstract class NodeBuilder implements Node {

  private final List<Node> nodes = new ArrayList<>();

  /**
   * Returns an iterator over the child {@link Node}'s.
   */
  @Override
  public final Iterator<Node> iterator() {
    return this.nodes.iterator();
  }

  /**
   * Iterates over the child nodes.
   */
  @Override
  public final List<Node> nodes() {
    return this.nodes;
  }

  /**
   * Add a child {@link NodeBuilder}.
   *
   * @param node
   */
  protected final <N extends NodeBuilder> N add(N node) {
    this.nodes.add(node);
    return node;
  }
}
