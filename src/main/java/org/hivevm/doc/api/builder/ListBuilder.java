// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.List;

/**
 * The {@link ListBuilder} class.
 */
public class ListBuilder extends NodeBuilder implements List {

    private final boolean isOrdered;

    /**
     * Constructs an instance of {@link ListBuilder}.
     *
     * @param isOrdered
     */
    public ListBuilder(boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    public final Iterable<Item> items() {
        return nodes();
    }

    public final ListItemBuilder addItem() {
        return add(new ListItemBuilder());
    }

    @Override
    public final boolean isOrdered() {
        return this.isOrdered;
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
