// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoBreak;
import org.hivevm.doc.fop.nodes.set.FoFont;
import org.hivevm.doc.fop.nodes.set.FoIndent;
import org.hivevm.doc.fop.nodes.set.FoMargin;
import org.hivevm.doc.fop.nodes.set.FoSpace;

/**
 * The {@link FoListBlock} class.
 */
public class FoListBlock extends FoNode implements FoSpace<FoListBlock>, FoMargin<FoListBlock>, FoFont<FoListBlock>,
    FoBreak<FoListBlock>, FoBackground<FoListBlock>, FoIndent<FoListBlock> {

  /**
   * Constructs an instance of {@link FoListBlock}.
   */
  public FoListBlock() {
    super("fo:list-block");
  }

  public FoListBlock setKeepWithNext(String keep) {
    set("keep-with-next.within-column", keep);
    return this;
  }

  public FoListBlock setLabelSeparation(String value) {
    set("provisional-label-separation", value);
    return this;
  }

  public FoListBlock setDistanceBetweenStarts(String value) {
    set("provisional-distance-between-starts", value);
    return this;
  }

  public FoListItem addItem(String label) {
    FoListItem item = new FoListItem(label);
    addNode(item);
    return item;
  }

  public FoListBlock addItem(FoListItem item) {
    addNode(item);
    return this;
  }
}
