// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Message.Style;

/**
 * The {@link PageBuilder} class.
 */
public abstract class PageBuilder extends SectionBuilder {

    public abstract PageBuilder setTitle(String title);

    public final MessageBuilder addNotification(Style style) {
        return add(new MessageBuilder(style));
    }

    public final TableBuilder addTable() {
        return add(new TableBuilder(false));
    }
}
