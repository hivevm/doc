// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoBorder;
import org.hivevm.doc.fop.nodes.set.FoFont;

/**
 * The {@link FoTableRow} class.
 */
public class FoTableRow extends FoNode implements FoBorder<FoTableRow>, FoFont<FoTableRow>, FoBackground<FoTableRow> {

  /**
   * Constructs an instance of {@link FoTableRow}.
   */
  FoTableRow() {
    super("fo:table-row");
  }

  public FoTableRow setDisplayAlign(String align) {
    set("display-align", align);
    return this;
  }

  public FoTableRow setProgessionDimensionMin(String value) {
    set("block-progression-dimension.minimum", value);
    return this;
  }

  public FoTableCell addCell() {
    FoTableCell cell = new FoTableCell();
    addNode(cell);
    return cell;
  }
}
