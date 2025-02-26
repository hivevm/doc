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
 * The {@link CodeParserSql} class.
 */
class CodeParserSql implements CodeParser {

    private static final List<String> KEYWORDS =
            Arrays.asList("SELECT", "FROM", "WHERE", "DISTINCT", "ON", "AS", "USING", "WHERE", "GROUP",
                    "BY", "ORDER",
                    "LIMIT", "OFFSET", "CREATE", "INSERT", "UPDATE", "DELETE", "INTO", "SET", "VALUES",
                    "IMPORT", "EXPORT",
                    "WITH", "DPI", "SIZE", "ANGLE", "LAYER", "SYNC", "BINARY", "SCHEMA", "UNCAST", "IS",
                    "NOT", "NULL");


    private static final Pattern LANGUAGE =
            Pattern.compile(String.format("(%s)", String.join("|", CodeParserSql.KEYWORDS)),
                    Pattern.CASE_INSENSITIVE);

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        FlowBuilder flow = builder.getFlowBuilder();
        for (String line : text.split("\\n")) {
            flow.addLine();
            if (line.startsWith("--")) {
                flow.addStyle(Flow.Kind.ITALIC).addColor(CodeToken.COMMENT.COLOR);
                flow.addText(line).restore().restore();
            } else {
                Matcher matcher = CodeParserSql.LANGUAGE.matcher(line);
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
