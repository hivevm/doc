// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree;

import java.util.List;

/**
 * The {@link Node} class defines a generic class for all FOP based elements.
 */
public interface Node extends Iterable<Node> {

  List<Node> nodes();

  <R> void accept(NodeVisitor<R> visitor, R data);
}
