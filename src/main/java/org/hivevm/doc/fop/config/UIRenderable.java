// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.util.Properties;

import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.template.Fluid;

/**
 * The {@link UIRenderable} class.
 */
interface UIRenderable extends Fluid {

  void render(FoBlock block, Properties properties);
}