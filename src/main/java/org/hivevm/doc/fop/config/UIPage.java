// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import org.hivevm.doc.fluid.FluidMargin;
import org.hivevm.doc.fluid.FluidTemplate;
import org.hivevm.doc.fop.Fo;
import org.hivevm.doc.fop.nodes.FoPageSequence;
import org.hivevm.doc.fop.nodes.FoRegion;
import org.hivevm.doc.fop.nodes.FoSimplePageMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The {@link UIPage} defines a page of the template.
 */
class UIPage implements FluidTemplate {

  private final String             name;
  private final FoSimplePageMaster simple;
  private final FoRegion           content;
  private final List<UIPageRegion> regions = new ArrayList<>();

  /**
   * Constructs an instance of {@link UIPage}.
   *
   * @param name
   */
  public UIPage(String name) {
    this.name = name;
    this.simple = new FoSimplePageMaster(name);
    this.content = this.simple.setBodyRegion(Fo.PAGE_CONTENT);
  }

  /**
   * Gets the name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Gets the {@link FoSimplePageMaster}.
   */
  public final FoSimplePageMaster getSimplePage() {
    return this.simple;
  }

  /**
   * Set a custom page size.
   *
   * @param width
   * @param height
   */
  @Override
  public void setPageSize(String width, String height) {
    this.simple.setPageSize(width, height);
  }

  /**
   * Get the {@link UIPage} padding.
   */
  @Override
  public final FluidMargin getPadding() {
    return new FluidMargin() {

      @Override
      public void setTop(String value) {
        UIPage.this.content.setMarginTop(value);
      }

      @Override
      public void setRight(String value) {
        UIPage.this.content.setMarginRight(value);
      }

      @Override
      public void setLeft(String value) {
        UIPage.this.content.setMarginLeft(value);
      }

      @Override
      public void setBottom(String value) {
        UIPage.this.content.setMarginBottom(value);
      }
    };
  }

  /**
   * Set the columns of the page.
   *
   * @param count
   * @param gap
   */
  @Override
  public void setColumns(String count, String gap) {
    this.content.setColumns(count, gap);
  }

  /**
   * Add a {@link UIPageRegion}.
   *
   * @param name
   */
  @Override
  public UIPageRegion setRegion(String name, String position) {
    if (name == null) {
      name = String.format("region-%s-%s", position, getName());
    }

    UIPageRegion region = null;

    switch (position.toLowerCase()) {
      case "top":
        region = new UIPageRegion(name, this.simple.addRegionBefore(name));
        break;
      case "left":
        region = new UIPageRegion(name, this.simple.addRegionStart(name));
        break;
      case "right":
        region = new UIPageRegion(name, this.simple.addRegionEnd(name));
        break;
      case "bottom":
        region = new UIPageRegion(name, this.simple.addRegionAfter(name));
        break;
    }

    this.regions.add(region);
    return region;
  }

  /**
   * Renders the regions of the page.
   *
   * @param page
   * @param properties
   */
  public void render(FoPageSequence sequence, Properties properties) {
    this.regions.forEach(r -> r.render(sequence, properties));
  }
}
