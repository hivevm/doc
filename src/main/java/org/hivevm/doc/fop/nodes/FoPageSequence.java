// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoPageSequence} class.
 */
public class FoPageSequence extends FoNode {

  /**
   * Constructs an instance of {@link FoPageSequence}.
   *
   * @param reference
   */
  public FoPageSequence(String reference) {
    super("fo:page-sequence");
    set("master-reference", reference);
  }

  public FoPageSequence setLanguage(String language) {
    set("language", language);
    return this;
  }

  public FoPageSequence setFormat(String format) {
    set("format", format);
    return this;
  }

  public FoPageSequence setInitialPageNumber(String initial) {
    set("initial-page-number", initial);
    return this;
  }

  public FoPageSequence setForcePageCount(String force) {
    set("force-page-count", force);
    return this;
  }

  public FoFlow flow(String reference) {
    FoFlow builder = new FoFlow(reference);
    addNode(builder);
    return builder;
  }
}
