// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.doc.builder.CodeBuilder;
import org.hivevm.doc.builder.CodeParser;

/**
 * The {@link CodeParserIni} class.
 */
class CodeParserIni implements CodeParser {

  private static final String  PATTERN_TEXT = "^(?:(\\[[^;]+)|([^=]+)(=[^;]*))?(;.+)?$";
  private static final Pattern PATTERN      = Pattern.compile(CodeParserIni.PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    for (String line : text.split("\\n")) {
      Matcher matcher = CodeParserIni.PATTERN.matcher(line);
      if (matcher.find()) {
        if (matcher.group(1) != null) { // Section
          builder.addInline(matcher.group(1)).setBold().setColor(CodeToken.SECTION.COLOR);
        } else if (matcher.group(2) != null) { // Parameter
          builder.addInline(matcher.group(2)).setColor(CodeToken.PARAMETER.COLOR);
          builder.addInline(matcher.group(3)).setColor(CodeToken.VALUE.COLOR);
        }

        if (matcher.group(4) != null) { // Comment
          builder.addInline(matcher.group(4)).setItalic().setColor(CodeToken.COMMENT.COLOR);
        }
      } else {
        builder.addText(line);
      }
      builder.addText("\n");
    }
  }
}
