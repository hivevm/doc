// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.codeblock;

import org.hivevm.doc.tree.builder.CodeBuilder;
import org.hivevm.doc.tree.builder.CodeParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link CodeParserYaml} class.
 */
class CodeParserYaml implements CodeParser {

  private static final String  PATTERN_TEXT = "^(?:(?:([^:']*):)?([^#]*))(#.+)?$";
  private static final Pattern PATTERN      = Pattern.compile(CodeParserYaml.PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

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
      Matcher matcher = CodeParserYaml.PATTERN.matcher(line);
      if (matcher.find()) {
        if (matcher.group(1) != null) { // Parameter
          builder.addInline(matcher.group(1)).setColor(CodeToken.YAML_ATTR.COLOR);
          builder.addText(":");
        }
        if (matcher.group(2) != null) { // Value
          builder.addInline(matcher.group(2)).setColor(CodeToken.YAML_VALUE.COLOR);
        }

        if (matcher.group(3) != null) { // Comment
          builder.addInline(line).setItalic().setColor(CodeToken.YAML_COMMENT.COLOR);
        }
      } else {
        builder.addText(line);
      }
      builder.addText("\n");
    }
  }
}
