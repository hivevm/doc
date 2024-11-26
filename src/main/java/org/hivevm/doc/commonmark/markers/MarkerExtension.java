// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.markers;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import org.hivevm.doc.commonmark.markers.Marker.Decoration;

/**
 * Extension for GFM strikethrough using ~~ (GitHub Flavored Markdown). <p> Create it with
 * {@link #create()} and then configure it on the builders
 * ({@link org.commonmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link HtmlRenderer.Builder#extensions(Iterable)}). </p> <p> The parsed strikethrough text
 * regions are turned into {@link Marker} nodes. </p>
 */
public class MarkerExtension implements Parser.ParserExtension {

  private MarkerExtension() {}

  public static Extension create() {
    return new MarkerExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customDelimiterProcessor(new MarkerProcessor());
    parserBuilder.customDelimiterProcessor(new MarkerProcessor(4, '_', Decoration.Underline)); // underline
    parserBuilder.customDelimiterProcessor(new MarkerProcessor(2, '~', Decoration.Strikethrough)); // strikethrough
  }
}
