// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoBookmark} class.
 */
public class FoBookmark extends FoNode {

  private final FoNode title;

  /**
   * Constructs an instance of {@link FoBookmark}.
   *
   * @param name
   */
  public FoBookmark(String id) {
    super("fo:bookmark");
    set("internal-destination", id);
    set("starting-state", "hide");

    this.title = FoNode.create("fo:bookmark-title");
    addNode(this.title);
  }

  /**
   * Set the title of the bookmark.
   *
   * @param title
   */
  public FoBookmark setTitle(String title) {
    this.title.addText(title);
    return this;
  }

  /**
   * Add a child bookmark.
   *
   * @param id
   */
  public FoBookmark addBookmark(String id) {
    FoBookmark builder = new FoBookmark(id);
    addNode(builder);
    return builder;
  }
}
