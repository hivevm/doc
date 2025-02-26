// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import org.hivevm.doc.api.codeblock.CodeToken;
import org.hivevm.document.CodeBlock;
import org.hivevm.document.TextSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlGenerator implements CodeGenerator {

    private static final String PATTERN_TEXT = "^(?:(?:([^:']*):)?([^#]*))(#.+)?$";
    private static final Pattern PATTERN = Pattern.compile(YamlGenerator.PATTERN_TEXT,
            Pattern.CASE_INSENSITIVE);

    public void generate(String[] lines, List<CodeBlock.Line> rows) {
        for (String line : lines) {
            var spans = new ArrayList<TextSpan>();
            Matcher matcher = YamlGenerator.PATTERN.matcher(line);
            if (matcher.find()) {
                if (matcher.group(1) != null) { // Parameter
                    spans.add(new TextSpan.Builder().color(CodeToken.YAML_COLOR.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(1)).build());
                    spans.add(new TextSpan.Builder().end().build());
                    spans.add(new TextSpan.Builder(":").build());
                }
                if (matcher.group(2) != null) { // Value
                    spans.add(new TextSpan.Builder().color(CodeToken.YAML_VALUE.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(2)).build());
                    spans.add(new TextSpan.Builder().end().build());
                }

                if (matcher.group(3) != null) { // Comment
                    spans.add(new TextSpan.Builder().italic().color(CodeToken.YAML_COMMENT.COLOR).build());
                    spans.add(new TextSpan.Builder(line).build());
                    spans.add(new TextSpan.Builder().end().build());
                }
            } else
                spans.add(new TextSpan.Builder(line).build());
            rows.add(new CodeBlock.Line(spans));
        }
    }
}
