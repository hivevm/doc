// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.doc.builder.CodeBuilder;
import org.hivevm.doc.builder.CodeParser;

/**
 * The {@link CodeParserShell} class.
 */
class CodeParserShell implements CodeParser {

  private static final Pattern PATTERN      = Pattern.compile("^(#.+)|([^\\s]+)(.+)?$", Pattern.CASE_INSENSITIVE);
  private static final Pattern PATTERN_ARGS = Pattern.compile("(\\s+-[^\\s]+)?(\\s+[^\\s]+)", Pattern.CASE_INSENSITIVE);

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    builder.setFontSize("10pt");

    for (String line : text.split("\\n")) {
      Matcher matcher = CodeParserShell.PATTERN.matcher(line);
      if (matcher.find()) {
        if (matcher.group(1) != null) { // Comment
          builder.addInline(line).setItalic().setColor(CodeToken.COMMENT.COLOR);
        }

        if (matcher.group(2) != null) { // Command
          builder.addInline(matcher.group(2)).setBold().setColor(CodeToken.PARAMETER.COLOR);

          String arguments = matcher.group(3);
          if (arguments == null) {
            continue;
          }

          Matcher args = CodeParserShell.PATTERN_ARGS.matcher(arguments);
          while (args.find()) {// Arguments
            if (args.group(1) != null) {
              builder.addInline(args.group(1)).setColor(CodeToken.VALUE.COLOR);
            }
            builder.addText(args.group(2));
          }
        }
      } else {
        builder.addText(line);
      }
      builder.addText("\n");
    }
  }
}
