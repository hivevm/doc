// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.List;

/**
 * The {@link ListBuilder} class.
 */
public class ListBuilder extends ContentBuilder implements List {

    private final boolean isOrdered;

    /**
     * Constructs an instance of {@link ListBuilder}.
     */
    public ListBuilder() {
        this(false);
    }

    /**
     * Constructs an instance of {@link ListBuilder}.
     *
     * @param type
     */
    public ListBuilder(boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    @Override
    public final boolean isOrdered() {
        return this.isOrdered;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
