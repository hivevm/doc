// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoBackground;
import org.hivevm.doc.fop.nodes.set.FoBorder;
import org.hivevm.doc.fop.nodes.set.FoBreak;
import org.hivevm.doc.fop.nodes.set.FoFont;
import org.hivevm.doc.fop.nodes.set.FoIndent;
import org.hivevm.doc.fop.nodes.set.FoMargin;
import org.hivevm.doc.fop.nodes.set.FoPadding;
import org.hivevm.doc.fop.nodes.set.FoSpace;

/**
 * The {@link FoBlock} class.
 */
public class FoBlock extends FoNode implements FoSpace<FoBlock>, FoMargin<FoBlock>, FoBorder<FoBlock>,
    FoPadding<FoBlock>, FoFont<FoBlock>, FoBreak<FoBlock>, FoBackground<FoBlock>, FoIndent<FoBlock> {

  /**
   * Constructs an instance of {@link FoBlock}.
   *
   * @param name
   */
  private FoBlock(String name) {
    super(name);
  }

  public FoBlock setTextAlignLast(String align) {
    set("text-align-last", align);
    return this;
  }

  public FoBlock setKeepWithNext(String keep) {
    set("keep-with-next.within-column", keep);
    return this;
  }

  public FoBlock setWarp(String value) {
    set("wrap-option", value);
    return this;
  }

  public FoBlock setWhiteSpaceCollapse(String value) {
    set("white-space-collapse", value);
    return this;
  }

  public FoBlock setWhiteSpaceTreatment(String value) {
    set("white-space-treatment", value);
    return this;
  }

  public FoBlock setLineFeed(String value) {
    set("linefeed-treatment", value);
    return this;
  }

  public FoBlock setSpan(String value) {
    set("span", value);
    return this;
  }

  public FoBlock setWidth(String value) {
    set("width", value);
    return this;
  }

  public FoBlock setHeight(String value) {
    set("height", value);
    return this;
  }

  /**
   * Add a simple content.
   *
   * @param content
   */
  public FoBlock addContent(String content) {
    addText(content);
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

  public static FoBlock block() {
    return new FoBlock("fo:block");
  }

  public static FoBlock inline() {
    return new FoBlock("fo:inline");
  }
}
