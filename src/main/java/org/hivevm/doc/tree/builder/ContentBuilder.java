// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.List;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The {@link ContentBuilder} class.
 */
public abstract class ContentBuilder extends SectionBuilder {

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final InlineBuilder addInline() {
    return add(new InlineBuilder());
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final InlineBuilder addFootnote() {
    InlineBuilder builder = add(new InlineBuilder());
    builder.setFootnote();
    return builder;
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final LinkBuilder addLink(String url) {
    return add(new LinkBuilder(url));
  }

  public void addContent(String content) {
    content = Arrays.asList(content.split("\n")).stream().filter(s -> !s.isEmpty()).collect(Collectors.joining(" "));
    BookConfig.instance().processKeywords(content, t -> add(new TextBuilder(t)), () -> add(new InlineBuilder()));
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final void addInlineCode(String text) {
    add(new TextBuilder(text, true));
  }
}
