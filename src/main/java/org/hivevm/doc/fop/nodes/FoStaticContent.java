// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.FoBlockContainer.Position;

/**
 * The {@link FoStaticContent} class.
 */
public class FoStaticContent extends FoNode {

  /**
   * Constructs an instance of {@link FoStaticContent}.
   *
   * @param reference
   */
  public FoStaticContent(String reference) {
    super("fo:static-content");
    set("flow-name", reference);
  }

  public FoBlock addBlock() {
    FoBlock builder = FoBlock.block();
    addNode(builder);
    return builder;
  }

  public FoBlockContainer blockContainer(Position position) {
    FoBlockContainer builder = new FoBlockContainer(position);
    addNode(builder);
    return builder;
  }
}
