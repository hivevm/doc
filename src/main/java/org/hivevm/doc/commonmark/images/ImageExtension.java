// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.images;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Extension for adding attributes to image nodes. <p> Create it with {@link #create()} and then
 * configure it on the builders ({@link org.commonmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link HtmlRenderer.Builder#extensions(Iterable)}). </p>
 *
 * @since 0.15.0
 */
public class ImageExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

  private ImageExtension() {}

  public static Extension create() {
    return new ImageExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customDelimiterProcessor(new ImageProcessor());
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder) {
    rendererBuilder.attributeProviderFactory(context -> ImageProvider.create());
  }
}
