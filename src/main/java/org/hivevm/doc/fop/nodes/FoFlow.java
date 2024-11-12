// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.FoBlockContainer.Position;
import org.hivevm.doc.fop.nodes.set.FoIndent;

/**
 * The {@link FoFlow} class.
 */
public class FoFlow extends FoNode implements FoIndent<FoFlow> {

  /**
   * Constructs an instance of {@link FoFlow}.
   *
   * @param reference
   */
  public FoFlow(String reference) {
    super("fo:flow");
    set("flow-name", reference);
  }

  public FoBlockContainer blockContainer(Position position) {
    FoBlockContainer builder = new FoBlockContainer(position);
    addNode(builder);
    return builder;
  }

  public FoBlock addBlock() {
    FoBlock builder = FoBlock.block();
    addNode(builder);
    return builder;
  }

  public FoNode addBlock(FoNode builder) {
    addNode(builder);
    return builder;
  }
}
