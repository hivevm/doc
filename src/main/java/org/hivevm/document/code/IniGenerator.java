// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import org.hivevm.doc.api.codeblock.CodeToken;
import org.hivevm.document.CodeBlock;
import org.hivevm.document.TextSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IniGenerator implements CodeGenerator {

    private static final String PATTERN_TEXT = "^(?:(\\[[^;]+)|([^=]+)(=[^;]*))?(;.+)?$";
    private static final Pattern PATTERN = Pattern.compile(IniGenerator.PATTERN_TEXT,
            Pattern.CASE_INSENSITIVE);

    public void generate(String[] lines, List<CodeBlock.Line> rows) {
        for (String line : lines) {
            var spans = new ArrayList<TextSpan>();
            var matcher = IniGenerator.PATTERN.matcher(line);
            if (matcher.find()) {
                if (matcher.group(1) != null) { // Section
                    spans.add(new TextSpan.Builder().bold().color(CodeToken.SECTION.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(1)).build());
                    spans.add(new TextSpan.Builder().end().build());
                } else if (matcher.group(2) != null) { // Parameter
                    spans.add(new TextSpan.Builder().color(CodeToken.PARAMETER.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(2)).build());
                    spans.add(new TextSpan.Builder().end().build());

                    spans.add(new TextSpan.Builder().color(CodeToken.VALUE.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(3)).build());
                    spans.add(new TextSpan.Builder().end().build());
                }

                if (matcher.group(4) != null) { // Comment
                    spans.add(new TextSpan.Builder().italic().color(CodeToken.COMMENT.COLOR).build());
                    spans.add(new TextSpan.Builder(matcher.group(4)).build());
                    spans.add(new TextSpan.Builder().end().build());
                }
            } else
                spans.add(new TextSpan.Builder(line).build());
            rows.add(new CodeBlock.Line(spans));
        }
    }
}
