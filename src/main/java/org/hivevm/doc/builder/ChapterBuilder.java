// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

/**
 * The {@link ChapterBuilder} class.
 */
class ChapterBuilder extends PageBuilder {

  private final int offset;
  private int       sectionOffset;

  /**
   * Constructs an instance of {@link ChapterBuilder}.
   *
   */
  public ChapterBuilder(PageBuilder parent, int level, int offset) {
    super(level, parent);
    this.offset = offset;
    this.sectionOffset = 0;
  }

  @Override
  public final int getOffset() {
    return this.offset;
  }

  @Override
  public final PageBuilder addSection() {
    return add(new ChapterBuilder(this, getLevel() + 1, this.sectionOffset++));
  }
}
