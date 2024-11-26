// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link NodeVisitor} class.
 */
public interface NodeVisitor<D> {

  default void visit(Chapter node, D data) {}

  default void visit(Paragraph node, D data) {}

  default void visit(Inline node, D data) {}

  default void visit(Text node, D data) {}

  default void visit(Link node, D data) {}

  default void visit(Image node, D data) {}

  default void visit(List node, D data) {}

  default void visit(Table node, D data) {}

  default void visit(CodeNode node, D data) {}

  default void visit(Message node, D data) {}
}
