// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import org.hivevm.doc.fluid.Fluid;

/**
 * The {@link UIContainer} defines a generic interface for all template items.
 */
public interface UIContainer extends Fluid {

  void addItem(UIRenderable item);

  UIContainer addContainer();

  void setTop(String top);

  void setLeft(String left);

  void setRight(String right);

  void setBottom(String bottom);

  void setColor(String color);

  void setBackground(String background);

  void setFontSize(String fontSize);

  void setFontStyle(String fontStyle);

  void setFontWeight(String fontWeight);

  void setTextAlign(String textAlign);

  void setLineHeight(String lineHeight);
}
