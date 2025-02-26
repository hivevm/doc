// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserCpp} class.
 */
class CodeParserCpp implements CodeParser {

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

    private static final Pattern CPP =
            Pattern.compile("(" + String.join("|", CodeParserCpp.KEYWORDS) + ")",
                    Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        boolean isComment = false;
        FlowBuilder flow = builder.getFlowBuilder();
        for (String line : text.split("\\n")) {
            flow.addLine();
            if (isComment) {
                flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                flow.addText(line).restore().restore();
                if (line.contains("*/")) {
                    isComment = false;
                }
            } else if (line.contains("/**")) {
                isComment = true;

                flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                flow.addText(line).restore().restore();
            } else if (line.startsWith("#")) {
                flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.DEFINES.COLOR);
                flow.addText(line).restore().restore();
            } else {
                Matcher matcher = CodeParserCpp.CPP.matcher(line);
                int offset = 0;
                while (matcher.find()) {
                    if (matcher.start() > offset) {
                        builder.getFlowBuilder().addText(line.substring(offset, matcher.start()));
                    }

                    flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.KEYWORD.COLOR);
                    flow.addText(matcher.group(1)).restore().restore();
                    offset = matcher.end();
                }
                builder.getFlowBuilder().addText(line.substring(offset));
            }
            flow.restore();
        }
    }
}
