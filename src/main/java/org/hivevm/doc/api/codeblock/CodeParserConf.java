// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserConf} class.
 */
class CodeParserConf implements CodeParser {

    private static final String  PATTERN_TEXT = "^(?:([^\\s#]+\\s)(?:([^=]+)=)?([^#]*))?(#.+)?$";
    private static final Pattern PATTERN      = Pattern.compile(CodeParserConf.PATTERN_TEXT,
            Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
//        builder.setTextColor(CodeToken.YAML_COLOR.COLOR);
//        builder.setBorderColor(CodeToken.YAML_COMMENT.COLOR);
//        builder.setBackground(CodeToken.YAML_BACKGROUND.COLOR);

        FlowBuilder flow = builder.getFlowBuilder();
        for (String line : text.split("\\n")) {
            flow.addLine();
            Matcher matcher = CodeParserConf.PATTERN.matcher(line);
            if (matcher.find()) {
                if (matcher.group(1) != null) { // Parameter
                    flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.YAML_ATTR.COLOR);
                    flow.addText(matcher.group(1)).restore().restore();
                    if (matcher.group(2) != null) {
                        flow.addColor(CodeToken.YAML_COLOR.COLOR);
                        flow.addText(matcher.group()).restore();
                        builder.getFlowBuilder().addText("=");
                    }
                    flow.addColor(CodeToken.YAML_VALUE.COLOR);
                    flow.addText(matcher.group(3)).restore();
                }

                if (matcher.group(4) != null) { // Comment
                    flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.YAML_COMMENT.COLOR);
                    flow.addText(matcher.group(4)).restore().restore();
                }
            } else {
                builder.getFlowBuilder().addText(line);
            }
        }
        flow.restore();
    }
}
