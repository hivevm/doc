// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoFont;
import org.hivevm.doc.fop.nodes.set.FoMargin;
import org.hivevm.doc.fop.nodes.set.FoSpace;

/**
 * The {@link FoListItem} class.
 */
public class FoListItem extends FoNode
    implements FoSpace<FoListItem>, FoMargin<FoListItem>, FoFont<FoListItem>, FoBackground<FoListItem> {

  private final FoBlock content;

  /**
   * Constructs an instance of {@link FoListItem}.
   *
   * @param label
   */
  public FoListItem(String label) {
    super("fo:list-item");

    FoNode head = FoNode.create("fo:list-item-label");
    head.set("end-indent", "label-end()");
    head.addNode(FoBlock.block().addContent(label));

    FoNode body = FoNode.create("fo:list-item-body");
    body.set("start-indent", "body-start()");

    this.content = FoBlock.block();
    body.addNode(this.content);

    addNode(head);
    addNode(body);
  }

  public FoListItem setKeepWithNext(String keep) {
    set("keep-with-next.within-column", keep);
    return this;
  }

  public FoBlock getContent() {
    return this.content;
  }
}
