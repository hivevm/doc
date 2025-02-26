// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Message;

/**
 * The {@link MessageBuilder} class.
 */
public class MessageBuilder extends ContentBuilder implements Message {

    private final Style style;

    /**
     * Constructs an instance of {@link MessageBuilder}.
     *
     * @param style
     */
    public MessageBuilder(Style style) {
        this.style = style;
    }

    @Override
    public final Style getStyle() {
        return this.style;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
