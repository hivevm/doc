// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Image;

/**
 * The {@link SectionBuilder} class.
 */
public abstract class SectionBuilder extends FlowBuilder {

    public final ParagraphBuilder addParagraph() {
        return add(new ParagraphBuilder());
    }

    public final ListBuilder addList() {
        return add(new ListBuilder());
    }

    public final ListBuilder addOrderedList() {
        return add(new ListBuilder(true));
    }

    public final CodeBuilder addCode() {
        return add(new CodeBuilder());
    }

    public final void addImage(String url, String title, String align, String width, String height) {
        add(new Image(url, title, align, width, height));
    }

    public final TableBuilder addVirtualTable() {
        return add(new TableBuilder(true));
    }
}
