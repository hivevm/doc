// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.CodeNode;
import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Node;
import org.hivevm.doc.api.Text;

/**
 * The {@link CodeBuilder} class.
 */
public class CodeBuilder extends ContentBuilder implements CodeNode {

    private boolean styled;
    private boolean inline;
    private String  background;
    private String  textColor;
    private String  borderColor;
    private String  fontSize;

    /**
     * Constructs an instance of {@link CodeBuilder}.
     */
    public CodeBuilder() {
        this.fontSize = "11pt";
        this.background = "#eeeeee";
        this.textColor = null;
        this.borderColor = "#aaaaaa";
    }

    /**
     * Return <code>true</code> id the code is styled.
     */
    @Override
    public final boolean isStyled() {
        return this.styled;
    }

    public final void setStyled(boolean styled) {
        this.styled = styled;
    }

    /**
     * Return <code>true</code> id the code has no padding and radius
     */
    @Override
    public final boolean isInline() {
        return this.inline;
    }

    public final void setInline(boolean inline) {
        this.inline = inline;
    }

    /**
     * Gets the background color.
     */
    @Override
    public final String getFontSize() {
        return this.fontSize;
    }

    /**
     * Gets the background color.
     */
    @Override
    public final String getBackground() {
        return this.background;
    }

    /**
     * Gets the border color.
     */
    @Override
    public final String getTextColor() {
        return this.textColor;
    }

    /**
     * Gets the border color.
     */
    @Override
    public final String getBorderColor() {
        return this.borderColor;
    }

    /**
     * @param fontSize
     */
    public final void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @param background
     */
    public final void setBackground(String background) {
        this.background = background;
    }

    /**
     * @param textColor
     */
    public final void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    /**
     * @param borderColor
     */
    public final void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void addContent(String content) {
        addNode(new Text(content));
    }

    /**
     * Add a child {@link InlineBuilder}.
     */
    public final InlineBuilder addInline(String text) {
        InlineBuilder inline = addInline();
        inline.add(new Text(text));
        return inline;
    }

    /**
     * Add a child {@link Node}.
     *
     * @param node
     */
    public final <N extends Node> N addNode(N node) {
        return add(node);
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
