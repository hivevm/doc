// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.List;

/**
 * The {@link ListBuilder} class.
 */
public class ListBuilder extends ContentBuilder implements List {

  private final boolean isOrdered;

  /**
   * Constructs an instance of {@link ListBuilder}.
   */
  public ListBuilder() {
    this(false);
  }

  /**
   * Constructs an instance of {@link ListBuilder}.
   *
   * @param type
   */
  public ListBuilder(boolean isOrdered) {
    this.isOrdered = isOrdered;
  }

  @Override
  public final boolean isOrdered() {
    return this.isOrdered;
  }
}
