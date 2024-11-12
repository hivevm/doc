// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.tables;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Extension for GFM tables using "|" pipes (GitHub Flavored Markdown). <p> Create it with
 * {@link #create()} and then configure it on the builders
 * ({@link org.commonmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link HtmlRenderer.Builder#extensions(Iterable)}). </p> <p> The parsed tables are turned into
 * {@link Table} blocks. </p>
 *
 * @see <a href="https://github.github.com/gfm/#tables-extension-">Tables (extension) in GitHub
 *      Flavored Markdown Spec</a>
 */
public class TableExtension implements Parser.ParserExtension {

  private TableExtension() {}

  public static Extension create() {
    return new TableExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customBlockParserFactory(new TableParser.Factory());
  }
}
