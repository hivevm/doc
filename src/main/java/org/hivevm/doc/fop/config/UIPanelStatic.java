// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hivevm.doc.fop.nodes.FoBlockContainer;
import org.hivevm.doc.fop.nodes.FoBlockContainer.Position;
import org.hivevm.util.DataUri;
import org.hivevm.doc.fop.nodes.FoStaticContent;

/**
 * The {@link UIPanelStatic} class.
 */
class UIPanelStatic implements UIContainer {

  private String                   top;
  private String                   left;
  private String                   right;
  private String                   bottom;

  private String                   color;
  private String                   background;

  private String                   fontSize;
  private String                   fontStyle;
  private String                   fontWeight;
  private String                   textAlign;
  private String                   lineHeight;

  private final List<UIRenderable> items = new ArrayList<>();

  /**
   * Constructs an instance of {@link UIPanelStatic}.
   *
   */
  public UIPanelStatic() {
    this.top = "0";
    this.left = "0";
    this.right = "0";
    this.bottom = "0";
    this.textAlign = "left";
    this.lineHeight = "1.5em";
  }

  @Override
  public final void addItem(UIRenderable child) {
    this.items.add(child);
  }

  @Override
  public final UIContainer addContainer() {
    UIPanel container = new UIPanel();
    this.items.add(container);
    return container;
  }

  @Override
  public final void setTop(String top) {
    this.top = top;
  }

  @Override
  public final void setLeft(String left) {
    this.left = left;
  }

  @Override
  public final void setRight(String right) {
    this.right = right;
  }

  @Override
  public final void setBottom(String bottom) {
    this.bottom = bottom;
  }

  @Override
  public final void setColor(String color) {
    this.color = color;
  }

  @Override
  public final void setBackground(String background) {
    this.background = background;
  }

  @Override
  public final void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  @Override
  public final void setFontStyle(String fontStyle) {
    this.fontStyle = fontStyle;
  }

  @Override
  public final void setFontWeight(String fontWeight) {
    this.fontWeight = fontWeight;
  }

  @Override
  public final void setTextAlign(String textAlign) {
    this.textAlign = textAlign;
  }

  @Override
  public final void setLineHeight(String lineHeight) {
    this.lineHeight = lineHeight;
  }

  public void render(FoStaticContent node, Properties properties) {
    FoBlockContainer container = node.blockContainer(Position.Absolute);
    container.setPosition(this.left, this.right, this.top, this.bottom);

    container.setColor(this.color);
    if (this.background != null) {
      if (this.background.startsWith("#")) {
        container.setBackgroundColor(this.background);
      } else {
        container.setBackground(DataUri.loadImage(this.background), "no-repeat");
      }
    }
    container.setFontSize(this.fontSize);
    container.setFontStyle(this.fontStyle);
    container.setFontWeight(this.fontWeight);
    container.setTextAlign(this.textAlign);
    container.setLineHeight(this.lineHeight);

    this.items.forEach(c -> c.render(container.addBlock(), properties));
  }
}