// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.builder.CodeBuilder;

/**
 * The {@link CodeParserShell} class.
 */
class CodeParserShell implements CodeParser {

    private static final Pattern PATTERN      = Pattern.compile("^(#.+)|([^\\s]+)(.+)?$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_ARGS = Pattern.compile("(\\s+-[^\\s]+)?(\\s+[^\\s]+)",
            Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        FlowBuilder flow = builder.getFlowBuilder();
        for (String line : text.split("\\n")) {
            flow.addLine();
            Matcher matcher = CodeParserShell.PATTERN.matcher(line);
            if (matcher.find()) {
                if (matcher.group(1) != null) { // Comment
                    flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                    flow.addText(line).restore().restore();
                }

                if (matcher.group(2) != null) { // Command
                    flow.addStyle(Flow.Kind.BOLD).addColor(CodeToken.PARAMETER.COLOR);
                    flow.addText(matcher.group(2)).restore().restore();

                    String arguments = matcher.group(3);
                    if (arguments == null) {
                        continue;
                    }

                    Matcher args = CodeParserShell.PATTERN_ARGS.matcher(arguments);
                    while (args.find()) {// Arguments
                        if (args.group(1) != null) {
                            flow.addColor(CodeToken.VALUE.COLOR);
                            flow.addText(args.group(1)).restore();
                        }
                        builder.getFlowBuilder().addText(args.group(2));
                    }
                }
            } else {
                builder.getFlowBuilder().addText(line);
            }
            flow.restore();
        }
    }
}
