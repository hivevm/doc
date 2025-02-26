// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link DocumentVisitor} class.
 */
public interface DocumentVisitor<D> {

    default void visit(Document node, D data) {
    }

    default void visit(Header node, D data) {
    }

    default void visit(Paragraph node, D data) {
    }

    default void visit(Inline node, D data) {
    }

    default void visit(Text node, D data) {
    }

    default void visit(Link node, D data) {
    }

    default void visit(Image node, D data) {
    }

    default void visit(List node, D data) {
    }

    default void visit(Table node, D data) {
    }

    default void visit(CodeNode node, D data) {
    }

    default void visit(Message node, D data) {
    }
}
