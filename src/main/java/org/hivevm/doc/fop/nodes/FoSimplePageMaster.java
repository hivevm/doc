// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoMargin;

/**
 * The {@link FoSimplePageMaster} class.
 */
public class FoSimplePageMaster extends FoNode implements FoMargin<FoSimplePageMaster> {

  private final String   name;
  private final FoRegion body;

  /**
   * Constructs an instance of {@link FoSimplePageMaster}.
   *
   * @param name
   */
  public FoSimplePageMaster(String name) {
    super("fo:simple-page-master");
    this.name = name;
    set("master-name", name);
    setPageSize("210mm", "297mm");
    this.body = new FoRegion();
    addNode(this.body);
  }

  public final String getPageName() {
    return this.name;
  }

  public void setPageSize(String width, String height) {
    setPageWidth(width);
    setPageHeight(height);
  }

  public void setPageWidth(String width) {
    set("page-width", width);
  }

  public void setPageHeight(String height) {
    set("page-height", height);
  }

  public FoRegion setBodyRegion(String name) {
    return this.body.setRegionName(name);
  }

  public FoRegion addRegionBefore(String name) {
    FoRegion region = new FoRegion(name, "before");
    addNode(region);
    return region;
  }

  public FoRegion addRegionAfter(String name) {
    FoRegion region = new FoRegion(name, "after");
    addNode(region);
    return region;
  }

  public FoRegion addRegionStart(String name) {
    FoRegion region = new FoRegion(name, "start");
    addNode(region);
    return region;
  }

  public FoRegion addRegionEnd(String name) {
    FoRegion region = new FoRegion(name, "end");
    addNode(region);
    return region;
  }
}
