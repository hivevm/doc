// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import org.hivevm.doc.fluid.Fluid;
import org.hivevm.doc.fop.nodes.FoBlock;

import java.util.Properties;

/**
 * The {@link UIRenderable} class.
 */
interface UIRenderable extends Fluid {

  void render(FoBlock block, Properties properties);
}