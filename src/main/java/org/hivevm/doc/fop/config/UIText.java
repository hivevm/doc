// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.doc.fop.nodes.FoBasicLink;
import org.hivevm.doc.fop.nodes.FoBlock;

/**
 * The {@link UIText} class.
 */
class UIText implements UIRenderable {

  private static final Pattern ENV  = Pattern.compile("\\{\\{\\$([^}]+)\\}\\}", Pattern.CASE_INSENSITIVE);
  private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)\\]\\(([^\\)]+)\\)", Pattern.CASE_INSENSITIVE);

  private String               text;
  private String               color;
  private String               fontSize;
  private String               fontStyle;
  private String               fontWeight;
  private String               textAlign;
  private String               lineHeight;

  private String               top;
  private String               left;
  private String               right;
  private String               bottom;

  /**
   * Constructs an instance of {@link UIText}.
   *
   */
  public UIText() {
    this.textAlign = "left";
    this.lineHeight = "1.5em";
  }

  public final void setText(String text) {
    this.text = text;
  }

  public final void setColor(String color) {
    this.color = color;
  }

  public final void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  public final void setFontStyle(String fontStyle) {
    this.fontStyle = fontStyle;
  }

  public final void setFontWeight(String fontWeight) {
    this.fontWeight = fontWeight;
  }

  public final void setTextAlign(String textAlign) {
    this.textAlign = textAlign;
  }

  public final void setLineHeight(String lineHeight) {
    this.lineHeight = lineHeight;
  }

  public final void setTop(String top) {
    this.top = top;
  }

  public final void setLeft(String left) {
    this.left = left;
  }

  public final void setRight(String right) {
    this.right = right;
  }

  public final void setBottom(String bottom) {
    this.bottom = bottom;
  }

  @Override
  public void render(FoBlock container, Properties properties) {
    properties.put("PAGE_NUMBER", "<fo:page-number/>");

    container.setColor(this.color);
    container.setFontSize(this.fontSize);
    container.setFontStyle(this.fontStyle);
    container.setFontWeight(this.fontWeight);
    container.setTextAlign(this.textAlign);
    container.setLineHeight(this.lineHeight);
    container.setPadding(this.left, this.right, this.top, this.bottom);

    UIText.addText(UIText.replaceText(this.text, properties), container);
  }

  private static String replaceText(String text, Properties properties) {
    int offset = 0;
    StringBuffer buffer = new StringBuffer();

    Matcher matcher = UIText.ENV.matcher(text);
    while (matcher.find()) {
      buffer.append(text.substring(offset, matcher.start()));
      buffer.append(properties.get(matcher.group(1)));
      offset = matcher.end();
    }
    buffer.append(text.substring(offset));
    return buffer.toString();
  }

  private static void addText(String text, FoBlock block) {
    int offset = 0;
    // StringBuffer buffer = new StringBuffer();
    //
    Matcher matcher = UIText.LINK.matcher(text);
    while (matcher.find()) {
      if (offset < matcher.start()) {
        block.addNode(FoBlock.inline().addContent(text.substring(offset, matcher.start())));
      }

      FoBasicLink link = new FoBasicLink("mailto:" + matcher.group(2));
      link.addText(matcher.group(1));
      block.addNode(link);

      offset = matcher.end();
    }
    if (offset < text.length()) {
      block.addNode(FoBlock.inline().addContent(text.substring(offset)));
    }
  }
}