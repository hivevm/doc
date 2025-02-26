// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserJson} class.
 */
class CodeParserJson implements CodeParser {

    private static final String  PATTERN_TEXT = "(\"[^\"]+\":)|(\"[^\"]+\")|([0-9.]+|true|false)";
    private static final Pattern PATTERN      = Pattern.compile(CodeParserJson.PATTERN_TEXT,
            Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        int offset = 0;
        FlowBuilder flow = builder.getFlowBuilder();
        Matcher matcher = CodeParserJson.PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                if (matcher.start(1) > offset) {
                    builder.getFlowBuilder().addText(text.substring(offset, matcher.start(1)));
                }

                flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.JSON_NAME.COLOR);
                flow.addText(matcher.group(1)).restore().restore();
                offset = matcher.end(1);
            } else if (matcher.group(2) != null) {
                if (matcher.start(2) > offset) {
                    builder.getFlowBuilder().addText(text.substring(offset, matcher.start(2)));
                }

                flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.JSON_TEXT.COLOR);
                flow.addText(matcher.group(2)).restore().restore();
                offset = matcher.end(2);
            } else if (matcher.group(3) != null) {
                if (matcher.start(3) > offset) {
                    builder.getFlowBuilder().addText(text.substring(offset, matcher.start(3)));
                }

                flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.JSON_VALUE.COLOR);
                flow.addText(matcher.group(3)).restore().restore();
                offset = matcher.end(3);
            }
        }
        if (offset < text.length()) {
            builder.getFlowBuilder().addText(text.substring(offset));
        }
    }
}
