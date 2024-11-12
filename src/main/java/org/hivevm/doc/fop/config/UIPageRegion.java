// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import org.hivevm.doc.fluid.FluidRegion;
import org.hivevm.doc.fop.nodes.FoBlockContainer;
import org.hivevm.doc.fop.nodes.FoBlockContainer.Position;
import org.hivevm.doc.fop.nodes.FoPageSequence;
import org.hivevm.doc.fop.nodes.FoRegion;
import org.hivevm.doc.fop.nodes.FoStaticContent;
import org.hivevm.doc.util.DataUri;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The {@link UIPageRegion} defines a single region of the page.
 */
public class UIPageRegion implements UIContainer, FluidRegion {

  private final String              name;
  private final FoRegion            region;

  private URI                       uri;
  private String                    background;
  private final List<UIPanelStatic> children = new ArrayList<>();

  /**
   * Constructs an instance of {@link UIPageRegion}.
   *
   * @param name
   * @param region
   */
  public UIPageRegion(String name, FoRegion region) {
    this.name = name;
    this.region = region;
  }

  @Override
  public final void setExtent(String extent) {
    this.region.setExtent(extent);
  }

  @Override
  public final void addItem(UIRenderable child) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final UIContainer addContainer() {
    UIPanelStatic container = new UIPanelStatic();
    this.children.add(container);
    return container;
  }

  @Override
  public final void setBackground(URI uri) {
    this.uri = uri;
  }

  @Override
  public final void setBackground(String background) {
    this.background = background;
  }

  @Override
  public void setTop(String top) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLeft(String left) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRight(String right) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setBottom(String bottom) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setColor(String color) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFontSize(String fontSize) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFontStyle(String fontStyle) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFontWeight(String fontWeight) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setTextAlign(String textAlign) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLineHeight(String lineHeight) {
    throw new UnsupportedOperationException();
  }

  public void render(FoPageSequence node, Properties properties) {
    FoStaticContent content = new FoStaticContent(this.name);

    if (this.uri != null) {
      FoBlockContainer container = content.blockContainer(Position.Absolute);
      container.setPosition("0", "0", "0", "0");
      container.setBackground(DataUri.loadImage(this.uri), "no-repeat");
      container.addBlock();
    } else if (this.background != null) {
      FoBlockContainer container = content.blockContainer(Position.Absolute);
      container.setPosition("0", "0", "0", "0");
      if (this.background.startsWith("#")) {
        container.setBackgroundColor(this.background);
      } else {
        container.setBackground(DataUri.loadImage(this.background), "no-repeat");
      }
      container.addBlock();
    }

    this.children.forEach(c -> c.render(content, properties));

    if (content.hasChildren()) {
      node.addNode(content);
    }
  }
}