// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause
package org.hivevm.doc.fop;

import org.hivevm.doc.fop.nodes.FoBasicLink;
import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.template.PageRenderer;

import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FoRenderer<C> implements PageRenderer<C> {

    private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)\\]\\(([^\\)]+)\\)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern ENV  = Pattern.compile("\\{\\{\\$([^}]+)\\}\\}",
            Pattern.CASE_INSENSITIVE);

    private final Properties properties;

    protected FoRenderer(Properties properties) {
        this.properties = properties;
    }

    protected final void addAndReplaceText(String text, FoBlock block) {
        int offset = 0;

        text = replaceText(text);
        Matcher matcher = FoRenderer.LINK.matcher(text);
        while (matcher.find()) {
            if (offset < matcher.start()) {
                block.addNode(FoBlock.inline().addContent(text.substring(offset, matcher.start())));
            }

            FoBasicLink link = new FoBasicLink("mailto:" + matcher.group(2));
            link.addText(matcher.group(1));
            block.addNode(link);

            offset = matcher.end();
        }
        if (offset < text.length()) {
            block.addNode(FoBlock.inline().addContent(text.substring(offset)));
        }
    }

    private String replaceText(String text) {
        int offset = 0;
        StringBuffer buffer = new StringBuffer();

        Matcher matcher = FoRenderer.ENV.matcher(text);
        while (matcher.find()) {
            Object value = properties.get(matcher.group(1));
            buffer.append(text, offset, matcher.start());
            buffer.append(value instanceof Supplier ? ((Supplier<String>) value).get() : value);
            offset = matcher.end();
        }
        buffer.append(text.substring(offset));
        return buffer.toString();
    }
}
