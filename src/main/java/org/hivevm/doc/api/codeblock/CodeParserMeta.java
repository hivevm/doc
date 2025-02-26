// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserMeta} class.
 */
class CodeParserMeta implements CodeParser {

    private static final String TYPES    = String.join("|", "void", "number", "integer", "string",
            "boolean");
    private static final String KEYWORDS = String.join("|", "private", "public", "enum", "class",
            "interface",
            "implements", "extends", "return", "const", "final", "super", "this", "transient", "default");

    private static final Pattern PATTERN = Pattern.compile(
            String.format("(?:(%s)|(%s)|([\\s\\t]+)|([{}<>(),]+))", CodeParserMeta.TYPES,
                    CodeParserMeta.KEYWORDS),
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
                flow.addText(line + "\n").restore().restore();
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
                        builder.getFlowBuilder().addText(line.substring(offset, matcher.start()));
                    }

                    if (matcher.group(1) != null) {
                        flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.VALUE.COLOR);
                        flow.addText(matcher.group(1)).restore().restore();
                    }
                    if (matcher.group(2) != null) {
                        flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.KEYWORD.COLOR);
                        flow.addText(matcher.group(2)).restore().restore();
                    }
                    if (matcher.group(3) != null) {
                        builder.getFlowBuilder().addText(matcher.group(3));
                    }
                    if (matcher.group(4) != null) {
                        builder.getFlowBuilder().addText(matcher.group(4));
                    }

                    offset = matcher.end();
                }

                builder.getFlowBuilder().addText(line.substring(offset));

                if (comment != null) {
                    flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                    flow.addText(comment).restore().restore();
                }
            }
            flow.restore();
        }
    }
}
