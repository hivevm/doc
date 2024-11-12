// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.codeblock;

import org.hivevm.doc.tree.builder.CodeBuilder;
import org.hivevm.doc.tree.builder.CodeParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link CodeParserMeta} class.
 */
class CodeParserMeta implements CodeParser {

  private static final String  TYPES    = String.join("|", "void", "number", "integer", "string", "boolean");
  private static final String  KEYWORDS = String.join("|", "private", "public", "enum", "class", "interface",
      "implements", "extends", "return", "const", "final", "super", "this", "transient", "default");

  private static final Pattern PATTERN  = Pattern.compile(
      String.format("(?:(%s)|(%s)|([\\s\\t]+)|([{}<>(),]+))", CodeParserMeta.TYPES, CodeParserMeta.KEYWORDS),
      Pattern.CASE_INSENSITIVE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    boolean isComment = false;
    for (String line : text.split("\\n")) {
      if (isComment) {
        builder.addInline(line + "\n").setItalic().setColor(CodeToken.COMMENT.COLOR);
        if (line.contains("*/")) {
          isComment = false;
        }
      } else {
        String comment = null;
        if (line.contains("/**")) {
          isComment = true;
          comment = line.substring(line.indexOf("/**"));
          line = line.substring(0, line.indexOf("/**") - 1);
        } else if (line.contains("//")) {
          comment = line.substring(line.indexOf("//"));
          line = line.substring(0, line.indexOf("//") - 1);
        }

        Matcher matcher = CodeParserMeta.PATTERN.matcher(line);
        int offset = 0;
        while (matcher.find()) {
          if (matcher.start() > offset) {
            builder.addText(line.substring(offset, matcher.start()));
          }

          if (matcher.group(1) != null) {
            builder.addInline(matcher.group(1)).setBold().setColor(CodeToken.VALUE.COLOR);
          }
          if (matcher.group(2) != null) {
            builder.addInline(matcher.group(2)).setBold().setColor(CodeToken.KEYWORD.COLOR);
          }
          if (matcher.group(3) != null) {
            builder.addText(matcher.group(3));
          }
          if (matcher.group(4) != null) {
            builder.addText(matcher.group(4));
          }

          offset = matcher.end();
        }

        builder.addText(line.substring(offset));

        if (comment != null) {
          builder.addInline(comment).setItalic().setColor(CodeToken.COMMENT.COLOR);
        }

        builder.addText("\n");
      }
    }
  }
}
