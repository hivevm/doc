// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoPageNumberCitation} class.
 */
public class FoPageNumberCitation extends FoNode {

  public FoPageNumberCitation(String id) {
    super("fo:page-number-citation");
    set("ref-id", id);
  }
}
