// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link Link} class.
 */
public interface Link extends Node {

  String getLink();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
