// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link Inline} class.
 */
public interface Inline extends Node {

  String getColor();

  String getBackground();

  String getPaddingTop();

  String getPaddingLeft();

  String getPaddingRight();

  String getPaddingBottom();

  String getRadius();

  boolean isBold();

  boolean isItalic();

  boolean isUnderline();

  boolean isOverline();

  boolean isStrikethrough();

  boolean isFootnote();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
