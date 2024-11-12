// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoBorder;
import org.hivevm.doc.fop.nodes.set.FoFont;
import org.hivevm.doc.fop.nodes.set.FoPadding;

/**
 * The {@link FoTableCell} class.
 */
public class FoTableCell extends FoNode
    implements FoBorder<FoTableCell>, FoPadding<FoTableCell>, FoFont<FoTableCell>, FoBackground<FoTableCell> {

  /**
   * Constructs an instance of {@link FoTableCell}.
   */
  FoTableCell() {
    super("fo:table-cell");
  }

  public FoTableCell setDisplayAlign(String align) {
    set("display-align", align);
    return this;
  }

  public FoTableCell setRelativeAlign(String align) {
    set("relative-align", align);
    return this;
  }

  public FoTableCell setRowSpan(int span) {
    if (span > 1) {
      set("number-rows-spanned", "" + span);
    }
    return this;
  }

  public FoTableCell setColSpan(int span) {
    if (span > 1) {
      set("number-columns-spanned", "" + span);
    }
    return this;
  }

  public FoBlock addBlock() {
    FoBlock block = FoBlock.block();
    addNode(block);
    return block;
  }

  public FoBlock addInline() {
    FoBlock block = FoBlock.inline();
    addNode(block);
    return block;
  }
}
