// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

/**
 * The {@link MessageBuilder} class.
 */
public class MessageBuilder extends BlockBuilder {

    private final Kind style;

    /**
     * Constructs an instance of {@link MessageBuilder}.
     *
     * @param style
     */
    public MessageBuilder(Kind style) {
        this.style = style;
    }

    @Override
    public final Kind kind() {
        return this.style;
    }
}
