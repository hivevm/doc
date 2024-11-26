// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link CodeNode} class.
 */
public interface CodeNode extends Node {

  boolean isInline();

  boolean isStyled();

  String getFontSize();

  String getBackground();

  String getTextColor();

  String getBorderColor();

  String getPadding();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
