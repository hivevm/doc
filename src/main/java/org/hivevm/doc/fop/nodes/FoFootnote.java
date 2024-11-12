// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoFont;

/**
 * The {@link FoFootnote} class.
 */
public class FoFootnote extends FoNode implements FoFont<FoFootnote> {

  private final FoBlock body;

  /**
   * Constructs an instance of {@link FoFootnote}.
   *
   * @param id
   */
  public FoFootnote(String id) {
    super("fo:footnote");
    FoBlock inline = FoBlock.inline();
    addNode(inline);
    inline.set("baseline-shift", "super").set("font-size", "smaller").addNode(FoNode.text(id));

    FoNode content = new FoNode("fo:footnote-body");
    addNode(content);
    this.body = FoBlock.block();
    content.addNode(this.body);

    inline = FoBlock.inline();
    inline.set("baseline-shift", "super").set("font-size", "smaller").addNode(FoNode.text(id));
    this.body.addNode(inline);
  }

  /**
   * Get the footnote body.
   */
  public FoBlock getBody() {
    return this.body;
  }
}
