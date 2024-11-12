// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fluid;

import org.hivevm.doc.fop.config.UIPageRegion;

/**
 * The {@link FluidTemplate} class.
 */
public interface FluidTemplate extends Fluid {

  /**
   * Set the template size.
   *
   * @param width
   * @param height
   */
  void setPageSize(String width, String height);

  /**
   * Set the columns of the template.
   *
   * @param count
   * @param gap
   */
  void setColumns(String count, String gap);

  /**
   * Add a {@link UIPageRegion}.
   *
   * @param name
   */
  FluidRegion setRegion(String name, String position);

  /**
   * Get the padding of the template.
   *
   * @param count
   * @param gap
   */
  FluidMargin getPadding();
}
