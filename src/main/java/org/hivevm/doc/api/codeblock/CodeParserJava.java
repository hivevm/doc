// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.CodeParser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link CodeParserJava} class.
 */
class CodeParserJava implements CodeParser {

    private static final List<String> KEYWORDS = Arrays.asList("synchronized", "implements",
            "instanceof", "interface",
            "protected", "transient", "abstract", "continue", "strictfp", "volatile", "boolean",
            "default", "extends",
            "finally", "package", "private", "assert", "double", "import", "native", "public", "return",
            "static", "switch",
            "throws", "break", "catch", "class", "const", "final", "float", "short", "super", "throw",
            "while", "byte",
            "case", "char", "else", "enum", "goto", "long", "this", "void", "for", "int", "new", "try",
            "do", "if");

    private static final Pattern JAVA =
            Pattern.compile(String.format("(%s)", String.join("|", CodeParserJava.KEYWORDS)),
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
            } else if (line.contains("/**")) {
                isComment = true;
                builder.addInline(line + "\n").setItalic().setColor(CodeToken.COMMENT.COLOR);
            } else {

                Matcher matcher = CodeParserJava.JAVA.matcher(line);
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
