// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoBorder;
import org.hivevm.doc.fop.nodes.set.FoMargin;
import org.hivevm.doc.fop.nodes.set.FoPadding;

/**
 * The {@link FoRegion} class.
 */
public class FoRegion extends FoNode
    implements FoMargin<FoRegion>, FoBorder<FoRegion>, FoPadding<FoRegion>, FoBackground<FoRegion> {

  /**
   * Constructs an instance of {@link FoRegion}.
   */
  public FoRegion() {
    super("fo:region-body");
  }

  /**
   * Constructs an instance of {@link FoRegion}.
   *
   * @param name
   * @param region
   */
  public FoRegion(String name, String region) {
    super("fo:region-" + region);
    setRegionName(name);
  }

  public FoRegion setRegionName(String name) {
    set("region-name", name);
    return this;
  }

  public FoRegion setDisplayAlign(String displayAlign) {
    set("display-align", displayAlign);
    return this;
  }

  public FoRegion setColumns(String count, String gap) {
    set("column-gap", gap);
    set("column-count", count);
    return this;
  }

  public FoRegion setExtent(String extent) {
    set("extent", extent);
    return this;
  }

  public FoRegion setPrecedence(String precedence) {
    set("precedence", precedence);
    return this;
  }

  public FoRegion setReferenceOrientation(String referenceOrientation) {
    set("reference-orientation", referenceOrientation);
    return this;
  }
}
