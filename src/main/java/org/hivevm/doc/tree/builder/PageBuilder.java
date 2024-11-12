// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.Chapter;
import org.hivevm.doc.tree.Message.Style;

import java.util.Random;

/**
 * The {@link PageBuilder} class.
 */
public abstract class PageBuilder extends SectionBuilder implements Chapter {

  private String            title;

  private final String      id;
  private final int         level;
  private final PageBuilder parent;

  /**
   * Constructs an instance of {@link PageBuilder}.
   */
  protected PageBuilder(int level, PageBuilder parent) {
    this.level = level;
    this.parent = parent;
    this.id = Long.toHexString(new Random().nextLong());
  }

  @Override
  public final int getLevel() {
    return this.level;
  }

  @Override
  public final String getId() {
    return this.id;
  }

  @Override
  public final String getTitle() {
    return this.title == null ? "" : this.title;
  }

  @Override
  public final Chapter getParent() {
    return this.parent;
  }

  public final PageBuilder setTitle(String title) {
    this.title = (title == null) ? title : title.trim();
    addIndex(this);
    return this;
  }

  public abstract PageBuilder addSection();

  public final MessageBuilder addNotification(Style style) {
    return add(new MessageBuilder(style));
  }

  public final TableBuilder addTable() {
    return add(new TableBuilder(false));
  }

  protected void addIndex(Chapter node) {
    this.parent.addIndex(node);
  }
}
