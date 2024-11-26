// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

/**
 * The {@link Message} class.
 */
public interface Message extends Node {

  enum Style {
    INFO,
    SUCCESS,
    WARNING,
    ERROR;
  }

  Style getStyle();

  @Override
  default <R> void accept(NodeVisitor<R> visitor, R data) {
    visitor.visit(this, data);
  }
}
