// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Text;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The {@link FlowBuilder} class.
 */
public abstract class FlowBuilder extends AbstractNode {

    public void addText(String text) {
        String content = Arrays.asList(text.split("\n")).stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("\n"));

        BookConfig.instance()
                .processKeywords(content, t -> add(new Text(t)), () -> add(new InlineBuilder()));
    }

    public final void addCode(String code) {
        String content = Arrays.asList(code.split("\n")).stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));

        add(new Text(content, true));
    }

    public final <N extends ContentBuilder, F extends FlowBuilder> F addInline(N node) {
        add(node);
        return (F) this;
    }

    public final <F extends FlowBuilder> F addFootnote(String title, String destination) {
        InlineBuilder builder = add(new InlineBuilder());
        builder.setFootnote();

        LinkBuilder link = new LinkBuilder(destination);
        link.addContent(title);
        builder.add(link);
        return (F) this;
    }
}
