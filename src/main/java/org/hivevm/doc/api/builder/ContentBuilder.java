// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Text;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The {@link ContentBuilder} class.
 */
public abstract class ContentBuilder extends SectionBuilder {

    public final InlineBuilder addInline() {
        return add(new InlineBuilder());
    }

    public void addContent(String content) {
        content = Arrays.asList(content.split("\n")).stream().filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
        BookConfig.instance()
                .processKeywords(content, t -> add(new Text(t)), () -> add(new InlineBuilder()));
    }
}
