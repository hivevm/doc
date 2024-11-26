// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link Paragraph} class.
 */
public interface Paragraph extends Node {

  int getIntent();

  boolean isSoftBreak();

  boolean isLineBreak();

  String getBackground();

  String getPaddingTop();

  String getPaddingLeft();

  String getPaddingRight();

  String getPaddingBottom();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
