// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import org.hivevm.doc.fluid.Fluid;
import org.hivevm.doc.fop.nodes.FoPageSequence;
import org.hivevm.doc.fop.nodes.FoPageSequenceMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The {@link UIPageSequence} defines a set of {@link UIPage}'s.
 */
class UIPageSequence implements Fluid {

  private final FoPageSequenceMaster master;
  private final List<UIPage>         pages = new ArrayList<>();

  /**
   * Constructs an instance of {@link UIPageSequence}.
   *
   * @param name
   */
  public UIPageSequence(String name) {
    this.master = new FoPageSequenceMaster(name);
  }

  /**
   * Gets the {@link FoPageSequenceMaster}.
   */
  public final FoPageSequenceMaster getPageSet() {
    return this.master;
  }

  /**
   * Adds a {@link UIPage}.
   *
   * @param page
   */
  public void addPage(UIPage page) {
    this.pages.add(page);
  }

  /**
   * Renders the {@link UIPage}'s.
   *
   * @param sequence
   * @param properties
   */
  public void render(FoPageSequence sequence, Properties properties) {
    this.pages.forEach(p -> p.render(sequence, properties));
  }
}
