// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fluid;


/**
 * The {@link FluidMargin} class.
 */
public interface FluidMargin {

  /**
   * Set the template top padding.
   *
   * @param value
   */
  void setTop(String value);

  /**
   * Set the template left padding.
   *
   * @param value
   */
  void setLeft(String value);

  /**
   * Set the template right padding.
   *
   * @param value
   */
  void setRight(String value);

  /**
   * Set the template bottom padding.
   *
   * @param value
   */
  void setBottom(String value);
}
