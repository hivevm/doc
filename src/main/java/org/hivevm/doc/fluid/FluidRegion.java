// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fluid;

import java.net.URI;

/**
 * The {@link FluidRegion} class.
 */
public interface FluidRegion extends Fluid {

  void setExtent(String extent);

  void setBackground(URI uri);

  void setBackground(String background);
}
