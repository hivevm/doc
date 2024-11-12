// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.List;

/**
 * The {@link SectionBuilder} class.
 */
public abstract class SectionBuilder extends NodeBuilder {

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final ParagraphBuilder addParagraph() {
    return add(new ParagraphBuilder());
  }

  public final void addBreak() {
    add(new ParagraphBuilder().setSoftBreak());
  }

  public final void addLineBreak() {
    add(new ParagraphBuilder().setLineBreak());
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final ListBuilder addList() {
    return add(new ListBuilder());
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final ListBuilder addOrderedList() {
    return add(new ListBuilder(true));
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final CodeBuilder addCode() {
    return add(new CodeBuilder());
  }

  /**
   * Add a new {@link Item} to the {@link List}.
   */
  public final void addImage(String url, String title, String align, String width, String height) {
    add(new ImageBuilder(url, title, align, width, height));
  }

  public final TableBuilder addVirtualTable() {
    return add(new TableBuilder(true));
  }
}
