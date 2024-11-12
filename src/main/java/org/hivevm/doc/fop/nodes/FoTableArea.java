// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoIndent;

/**
 * The {@link FoTableArea} class.
 */
public class FoTableArea extends FoNode implements FoIndent<FoTableArea>, FoBackground<FoTableArea> {

  /**
   * Constructs an instance of {@link FoTableArea}.
   */
  FoTableArea(String name) {
    super(name);
  }

  public FoTableRow addRow() {
    FoTableRow row = new FoTableRow();
    addNode(row);
    return row;
  }
}
