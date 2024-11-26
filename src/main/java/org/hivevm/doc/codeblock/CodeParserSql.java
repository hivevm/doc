// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.codeblock;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.doc.builder.CodeBuilder;
import org.hivevm.doc.builder.CodeParser;

/**
 * The {@link CodeParserSql} class.
 */
class CodeParserSql implements CodeParser {

  private static final List<String> KEYWORDS =
      Arrays.asList("SELECT", "FROM", "WHERE", "DISTINCT", "ON", "AS", "USING", "WHERE", "GROUP", "BY", "ORDER",
          "LIMIT", "OFFSET", "CREATE", "INSERT", "UPDATE", "DELETE", "INTO", "SET", "VALUES", "IMPORT", "EXPORT",
          "WITH", "DPI", "SIZE", "ANGLE", "LAYER", "SYNC", "BINARY", "SCHEMA", "UNCAST", "IS", "NOT", "NULL");


  private static final Pattern LANGUAGE =
      Pattern.compile(String.format("(%s)", String.join("|", CodeParserSql.KEYWORDS)), Pattern.CASE_INSENSITIVE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    for (String line : text.split("\\n")) {
      if (line.startsWith("--")) {
        builder.addInline(line + "\n").setItalic().setColor(CodeToken.COMMENT.COLOR);
      } else {
        Matcher matcher = CodeParserSql.LANGUAGE.matcher(line);
        int offset = 0;
        while (matcher.find()) {
          if (matcher.start() > offset) {
            builder.addText(line.substring(offset, matcher.start()));
          }
          builder.addInline(matcher.group(1)).setBold().setColor(CodeToken.KEYWORD.COLOR);
          offset = matcher.end();
        }
        builder.addText(line.substring(offset) + "\n");
      }
    }
  }
}
