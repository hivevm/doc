// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree;

/**
 * The {@link Chapter} class.
 */
public interface Chapter extends Node {

  String getId();

  String getTitle();

  int getLevel();

  int getOffset();

  Chapter getParent();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
