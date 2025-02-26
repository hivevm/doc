// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link DocumentNode} defines a common interface for al concrete {@link Document} elements.
 */
public interface DocumentNode {

    <R> void accept(DocumentNode.Visitor<R> visitor, R data);

    interface Visitor<D> {

        void visit(Document node, D data);

        void visit(Header node, D data);

        void visit(Break node, D data);

        void visit(Paragraph node, D data);

        void visit(Block node, D data);

        void visit(List node, D data);

        void visit(Table node, D data);

        void visit(CodeBlock node, D data);
    }
}
