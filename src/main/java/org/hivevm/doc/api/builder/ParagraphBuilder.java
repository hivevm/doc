// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Paragraph;

/**
 * The {@link ParagraphBuilder} class.
 */
public class ParagraphBuilder extends ContentBuilder implements Paragraph {

    private boolean isBlock;
    private boolean isSoftBreak;
    private boolean isLineBreak;

    private String background;
    private String paddingTop;
    private String paddingLeft;
    private String paddingRight;
    private String paddingBottom;

    @Override
    public final boolean isBlock() {
        return this.isBlock;
    }

    @Override
    public final boolean isSoftBreak() {
        return this.isSoftBreak;
    }

    @Override
    public final boolean isLineBreak() {
        return this.isLineBreak;
    }

    @Override
    public final String getBackground() {
        return this.background;
    }

    @Override
    public final String getPaddingTop() {
        return this.paddingTop;
    }

    @Override
    public final String getPaddingLeft() {
        return this.paddingLeft;
    }

    @Override
    public final String getPaddingRight() {
        return this.paddingRight;
    }

    @Override
    public final String getPaddingBottom() {
        return this.paddingBottom;
    }

    public final ParagraphBuilder setBlock() {
        this.isBlock = true;
        return this;
    }

    public final ParagraphBuilder setSoftBreak() {
        this.isSoftBreak = true;
        return this;
    }

    public final ParagraphBuilder setLineBreak() {
        this.isLineBreak = true;
        return this;
    }

    public final ParagraphBuilder setBackground(String background) {
        this.background = background;
        return this;
    }

    public final ParagraphBuilder setPadding(String horizontal, String vertical) {
        return setPadding(horizontal, horizontal, vertical, vertical);
    }

    public final ParagraphBuilder setPadding(String left, String right, String top, String bottom) {
        this.paddingTop = top;
        this.paddingLeft = left;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        return this;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
