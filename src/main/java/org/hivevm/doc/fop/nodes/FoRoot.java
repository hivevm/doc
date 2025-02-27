// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoRoot} class.
 */
public class FoRoot extends FoNode {

  private final FoNode layouts   = FoNode.create("fo:layout-master-set");
  private final FoNode bookmarks = FoNode.create("fo:bookmark-tree");

  /**
   * Constructs an instance of {@link FoRoot}.
   *
   */
  FoRoot(String font) {
    super("fo:root");
    set("xmlns:fo", "http://www.w3.org/1999/XSL/Format");
    set("font-family", font).set("font-size", "10pt");
    set("font-selection-strategy", "character-by-character");
    set("text-align", "justify").set("line-height", "1.4em");
    set("line-height-shift-adjustment", "disregard-shifts");
    set("writing-mode", "lr-tb").set("language", "en");
  }

  /**
   * Get the {@link FoNode} for the layout master set.
   */
  public final FoNode getLayouts() {
    return this.layouts;
  }

  public FoBookmark addBookmark(String id) {
    FoBookmark bookmark = new FoBookmark(id);
    this.bookmarks.addNode(bookmark);
    return bookmark;
  }

  public FoPageSequence addPageSequence(String reference) {
    FoPageSequence sequence = new FoPageSequence(reference);
    addNode(sequence);
    return sequence;
  }

  public FoPageSequence addPageSequence(FoPageSequenceMaster pages) {
    FoPageSequence sequence = new FoPageSequence(pages.getPageName());
    addNode(sequence);
    return sequence;
  }

  /**
   * Build the FO document.
   */
  @Override
  public final String build() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

    FoUtil.writeStart(buffer, getTagName(), getAttributes());

    if (getLayouts().hasChildren()) {
      buffer.append(getLayouts().build());
    }

    if (this.bookmarks.hasChildren()) {
      buffer.append(this.bookmarks.build());
    }

    forEach(b -> buffer.append(b.build()));

    FoUtil.writeEnd(buffer, getTagName());
    buffer.append("\n");
    return buffer.toString();
  }
}
