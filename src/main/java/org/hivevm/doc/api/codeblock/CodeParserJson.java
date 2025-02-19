// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.CodeParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link CodeParserJson} class.
 */
class CodeParserJson implements CodeParser {

    private static final String  PATTERN_TEXT = "(\"[^\"]+\":)|(\"[^\"]+\")|([0-9.]+|true|false)";
    private static final Pattern PATTERN      = Pattern.compile(CodeParserJson.PATTERN_TEXT,
            Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     *
     * @param node
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        int offset = 0;
        Matcher matcher = CodeParserJson.PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                if (matcher.start(1) > offset) {
                    builder.addText(text.substring(offset, matcher.start(1)));
                }

                builder.addInline(matcher.group(1)).setBold().setColor(CodeToken.JSON_NAME.COLOR);
                offset = matcher.end(1);
            } else if (matcher.group(2) != null) {
                if (matcher.start(2) > offset) {
                    builder.addText(text.substring(offset, matcher.start(2)));
                }

                builder.addInline(matcher.group(2)).setBold().setColor(CodeToken.JSON_TEXT.COLOR);
                offset = matcher.end(2);
            } else if (matcher.group(3) != null) {
                if (matcher.start(3) > offset) {
                    builder.addText(text.substring(offset, matcher.start(3)));
                }

                builder.addInline(matcher.group(3)).setBold().setColor(CodeToken.JSON_VALUE.COLOR);
                offset = matcher.end(3);
            }
        }
        if (offset < text.length()) {
            builder.addText(text.substring(offset));
        }
    }
}
