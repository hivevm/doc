// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Inline;

/**
 * The {@link InlineBuilder} class.
 */
public class InlineBuilder extends ContentBuilder implements Inline {

    private String color;
    private String background;

    private String paddingTop;
    private String paddingLeft;
    private String paddingRight;
    private String paddingBottom;

    private boolean isBold;
    private boolean isItalic;
    private boolean isOverline;
    private boolean isUnderline;
    private boolean isStrikethrough;
    private boolean isFootnote;

    @Override
    public final String getColor() {
        return this.color;
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

    public final InlineBuilder setColor(String color) {
        this.color = color;
        return this;
    }

    public final InlineBuilder setBackground(String background) {
        this.background = background;
        return this;
    }

    public final InlineBuilder setPadding(String horizontal, String vertical) {
        return setPadding(horizontal, horizontal, vertical, vertical);
    }

    public final InlineBuilder setPadding(String left, String right, String top, String bottom) {
        this.paddingTop = top;
        this.paddingLeft = left;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        return this;
    }

    @Override
    public final boolean isBold() {
        return this.isBold;
    }

    public final InlineBuilder setBold() {
        this.isBold = true;
        return this;
    }

    @Override
    public final boolean isItalic() {
        return this.isItalic;
    }

    public final InlineBuilder setItalic() {
        this.isItalic = true;
        return this;
    }

    @Override
    public final boolean isUnderline() {
        return this.isUnderline;
    }

    public final InlineBuilder setUnderline() {
        this.isUnderline = true;
        return this;
    }

    @Override
    public final boolean isOverline() {
        return this.isOverline;
    }

    public final InlineBuilder setOverline() {
        this.isOverline = true;
        return this;
    }

    @Override
    public final boolean isStrikethrough() {
        return this.isStrikethrough;
    }

    public final InlineBuilder setStrikethrough() {
        this.isStrikethrough = true;
        return this;
    }

    @Override
    public final boolean isFootnote() {
        return this.isFootnote;
    }

    public final void setFootnote() {
        this.isFootnote = true;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
