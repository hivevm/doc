// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Text} class.
 */
public record Text(String text, boolean isCode) implements Node {

    /**
     * Constructs an instance of {@link Text}.
     *
     * @param text
     */
    public Text(String text) {
        this(text, false);
    }

    @Override
    public <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
