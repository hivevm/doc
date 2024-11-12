// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree;

/**
 * The {@link Image} class.
 */
public interface Image extends Node {

  String getUrl();

  String getText();

  String getAlign();

  String getWidth();

  String getHeight();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
