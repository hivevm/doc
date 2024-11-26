// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import org.hivevm.doc.CodeNode;

/**
 * The {@link CodeBuilder} class.
 */
public class CodeBuilder extends ContentBuilder implements CodeNode {

  private boolean styled;
  private boolean inline;
  private String  padding;
  private String  background;
  private String  textColor;
  private String  borderColor;
  private String  fontSize;

  /**
   * Constructs an instance of {@link CodeBuilder}.
   */
  public CodeBuilder() {
    this.fontSize = "11pt";
    this.padding = "2pt 4pt";
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

  @Override
  public String getPadding() {
    return this.padding;
  }

  /**
   * @param background
   */
  public final void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  /**
   * @param background
   */
  public final void setPadding(String padding) {
    this.padding = padding;
  }

  /**
   * @param background
   */
  public final void setBackground(String background) {
    this.background = background;
  }

  /**
   * @param borderColor
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
    addNode(new TextBuilder(content));
  }

  /**
   * Add a child {@link TextBuilder}.
   *
   * @param text
   */
  public final TextBuilder addText(String text) {
    return addNode(new TextBuilder(text));
  }

  /**
   * Add a child {@link InlineBuilder}.
   */
  public final InlineBuilder addInline(String text) {
    InlineBuilder inline = addInline();
    inline.add(new TextBuilder(text));
    return inline;
  }

  /**
   * Add a child {@link InlineBuilder}.
   */
  public final TableBuilder addCustomTable() {
    return addNode(new TableBuilder(true));
  }

  /**
   * Add a child {@link NodeBuilder}.
   *
   * @param node
   */
  public final <N extends NodeBuilder> N addNode(N node) {
    return add(node);
  }
}
