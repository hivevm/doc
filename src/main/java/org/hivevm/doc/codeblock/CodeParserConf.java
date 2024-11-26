// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.doc.builder.CodeBuilder;
import org.hivevm.doc.builder.CodeParser;

/**
 * The {@link CodeParserConf} class.
 */
class CodeParserConf implements CodeParser {

  private static final String  PATTERN_TEXT = "^(?:([^\\s#]+\\s)(?:([^=]+)=)?([^#]*))?(#.+)?$";
  private static final Pattern PATTERN      = Pattern.compile(CodeParserConf.PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    builder.setFontSize("10pt");
    builder.setTextColor(CodeToken.YAML_COLOR.COLOR);
    builder.setBorderColor(CodeToken.YAML_COMMENT.COLOR);
    builder.setBackground(CodeToken.YAML_BACKGROUND.COLOR);

    for (String line : text.split("\\n")) {
      Matcher matcher = CodeParserConf.PATTERN.matcher(line);
      if (matcher.find()) {
        if (matcher.group(1) != null) { // Parameter
          builder.addInline(matcher.group(1)).setBold().setColor(CodeToken.YAML_ATTR.COLOR);
          if (matcher.group(2) != null) {
            builder.addInline(matcher.group(2)).setColor(CodeToken.YAML_COLOR.COLOR);
            builder.addText("=");
          }
          builder.addInline(matcher.group(3)).setColor(CodeToken.YAML_VALUE.COLOR);
        }

        if (matcher.group(4) != null) { // Comment
          builder.addInline(matcher.group(4)).setItalic().setColor(CodeToken.YAML_COMMENT.COLOR);
        }
      } else {
        builder.addText(line);
      }
      builder.addText("\n");
    }
  }
}
