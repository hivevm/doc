// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hivevm.doc.fop.Fo;
import org.hivevm.doc.fop.nodes.FoNode;
import org.hivevm.doc.fop.nodes.FoRoot;
import org.hivevm.doc.template.FluidBuilder;

/**
 * The {@link FoBuilder} class.
 */
public class FoBuilder extends FluidBuilder {

  private final Map<String, UIPage>         templates = new LinkedHashMap<>();
  private final Map<String, UIPageSequence> pageSets  = new LinkedHashMap<>();

  /**
   * Constructs an instance of {@link FoBuilder}.
   *
   * @param workingDir
   */
  public FoBuilder(File workingDir) {
    super(workingDir);
  }

  /**
   * Get {@link UIPage} by name.
   *
   * @param name
   */
  public final UIPage getTemplate(String name) {
    return this.templates.get(name);
  }

  /**
   * Add a named {@link UIPage}.
   *
   * @param name
   */
  @Override
  public final UIPage addTemplate(String name) {
    UIPage page = new UIPage(name);
    this.templates.put(name, page);
    return page;
  }

  /**
   * Add a named {@link UIPage}.
   *
   * @param name
   */
  public final UIPageSequence addPageSet(String name) {
    this.pageSets.put(name, new UIPageSequence(name));
    return this.pageSets.get(name);
  }


  /**
   * Creates the {@link FoContext}.
   *
   * @param config
   */
  public FoContext build() {
    FoRoot root = FoNode.root(Fo.FONT_TEXT);
    root.set("xmlns:fox", "http://xmlgraphics.apache.org/fop/extensions");

    this.templates.values().stream().map(UIPage::getSimplePage).forEach(p -> root.getLayouts().addNode(p));
    this.pageSets.values().stream().map(UIPageSequence::getPageSet).forEach(s -> root.getLayouts().addNode(s));

    return new FoContext(root, getSymbols(), this.pageSets);
  }
}
