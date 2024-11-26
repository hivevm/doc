// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import org.hivevm.doc.Image;

/**
 * The {@link ImageBuilder} class.
 */
public class ImageBuilder extends ContentBuilder implements Image {

  private final String url;
  private final String text;
  private final String align;
  private final String width;
  private final String height;

  /**
   * Constructs an instance of {@link ImageBuilder}.
   */
  public ImageBuilder(String url, String text, String align, String width, String height) {
    this.url = url;
    this.text = text;
    this.align = (align == null) ? "center" : align;
    this.width = width;
    this.height = height;
  }

  /**
   * Gets the URL.
   */
  @Override
  public final String getUrl() {
    return this.url;
  }

  /**
   * Gets the title.
   */
  @Override
  public final String getText() {
    return this.text;
  }

  /**
   * Gets the align.
   */
  @Override
  public final String getAlign() {
    return this.align;
  }

  /**
   * Gets the width.
   */
  @Override
  public final String getWidth() {
    return this.width;
  }

  /**
   * Gets the height.
   */
  @Override
  public final String getHeight() {
    return this.height;
  }
}
