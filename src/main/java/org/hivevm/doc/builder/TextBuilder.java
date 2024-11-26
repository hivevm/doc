// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import org.hivevm.doc.Text;

/**
 * The {@link TextBuilder} class.
 */
class TextBuilder extends NodeBuilder implements Text {

  private final String  text;
  private final boolean isCode;

  /**
   * Constructs an instance of {@link TextBuilder}.
   *
   * @param text
   */
  public TextBuilder(String text) {
    this(text, false);
  }

  /**
   * Constructs an instance of {@link TextBuilder}.
   *
   * @param text
   * @param isCode
   */
  public TextBuilder(String text, boolean isCode) {
    this.text = text;
    this.isCode = isCode;
  }

  /**
   * Gets the text value.
   */
  @Override
  public final String getText() {
    return this.text;
  }

  /**
   * Gets the text value.
   */
  @Override
  public final boolean isCode() {
    return this.isCode;
  }
}
