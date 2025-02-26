// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserXml} class.
 */
class CodeParserXml implements CodeParser {

    private static final String  PATTERN_TEXT = "(<?xml .+?>)|(<[\\w\\-]+|\\s*/?>|</[\\w\\-]+>)|([^\\s]+=)(\"[^\"]+\")";
    private static final Pattern PATTERN      =
            Pattern.compile(CodeParserXml.PATTERN_TEXT, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        int index = 0;
        FlowBuilder flow = builder.getFlowBuilder();
        flow.addLine(); // Workaround
        Matcher matcher = CodeParserXml.PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.group(1) != null) { // Comment
                flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                flow.addText("<?" + matcher.group(1)).restore().restore();
                index = matcher.end(1);
            }

            if (matcher.group(2) != null) {
                if (matcher.start(2) > index) {
                    flow.addColor(CodeToken.TEXT.COLOR);
                    flow.addText(text.substring(index, matcher.start(2))).restore();
                }
                flow.addColor(CodeToken.KEYWORD.COLOR);
                flow.addText(matcher.group(2)).restore();
                index = matcher.end(2);
            }

            if (matcher.group(3) != null) {
                if (matcher.start(3) > index) {
                    flow.addColor(CodeToken.TEXT.COLOR);
                    flow.addText(text.substring(index, matcher.start(3))).restore();
                }

                flow.addColor(CodeToken.PARAMETER.COLOR);
                flow.addText(matcher.group(3)).restore();

                flow.addColor(CodeToken.VALUE.COLOR).addText(matcher.group(4)).restore();
                index = matcher.end(4);
            }
        }

        if (index < text.length()) { // Text
            flow.addColor(CodeToken.TEXT.COLOR);
            flow.addText(text.substring(index)).restore();
        }
        flow.restore(); // Workaround
    }
}
