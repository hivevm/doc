// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.codeblock;

import org.hivevm.doc.tree.builder.CodeBuilder;
import org.hivevm.doc.tree.builder.CodeParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link CodeParserXml} class.
 */
class CodeParserXml implements CodeParser {

  private static final String  PATTERN_TEXT = "(<?xml .+?>)|(<\\w+|\\s*/?>|</\\w+>)|([^\\s]+=)(\"[^\"]+\")";
  private static final Pattern PATTERN      =
      Pattern.compile(CodeParserXml.PATTERN_TEXT, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    int index = 0;
    Matcher matcher = CodeParserXml.PATTERN.matcher(text);
    while (matcher.find()) {
      if (matcher.group(1) != null) { // Comment
        builder.addInline("<?" + matcher.group(1)).setItalic().setColor(CodeToken.COMMENT.COLOR);
        index = matcher.end(1);
      }

      if (matcher.group(2) != null) {
        if (matcher.start(2) > index) {
          builder.addInline(text.substring(index, matcher.start(2))).setBold().setColor(CodeToken.TEXT.COLOR);
        }
        builder.addInline(matcher.group(2)).setBold().setColor(CodeToken.KEYWORD.COLOR);
        index = matcher.end(2);
      }

      if (matcher.group(3) != null) {
        if (matcher.start(3) > index) {
          builder.addInline(text.substring(index, matcher.start(3))).setBold().setColor(CodeToken.TEXT.COLOR);
        }

        builder.addInline(matcher.group(3)).setBold().setColor(CodeToken.PARAMETER.COLOR);
        builder.addInline(matcher.group(4)).setBold().setColor(CodeToken.VALUE.COLOR);
        index = matcher.end(4);
      }
    }

    if (index < text.length()) { // Text
      builder.addInline(text.substring(index)).setBold().setColor(CodeToken.TEXT.COLOR);
    }
  }
}
