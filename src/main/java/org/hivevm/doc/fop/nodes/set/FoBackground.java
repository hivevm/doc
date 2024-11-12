// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes.set;

/**
 * The {@link FoBackground} class.
 */
public interface FoBackground<F extends FoBackground<?>> extends Fo {

  @SuppressWarnings("unchecked")
  default F setBackgroundColor(String color) {
    set("background-color", color);
    return (F) this;
  }

  @SuppressWarnings("unchecked")
  default F setBackground(String image, String repeat) {
    set("background-image", image);
    set("background-repeat", repeat);
    return (F) this;
  }
}
