// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop;

import org.hivevm.doc.fop.config.FoContext;
import org.hivevm.doc.fop.nodes.FoBookmark;
import org.hivevm.doc.tree.Chapter;
import org.hivevm.doc.tree.NodeVisitor;
import org.hivevm.doc.util.PageUtil;

/**
 * The {@link FoRendererBookmark} class.
 */
class FoRendererBookmark implements NodeVisitor<FoBookmark> {

  private final FoContext config;

  /**
   * Constructs an instance of {@link FoRendererBookmark}.
   *
   * @param config
   */
  public FoRendererBookmark(FoContext config) {
    this.config = config;
  }

  /**
   * Renders a {@link Chapter} node.
   *
   * @param node
   * @param data
   */
  @Override
  public final void visit(Chapter node, FoBookmark data) {
    FoBookmark bookmark =
        (node.getLevel() > 1) ? data.addBookmark(node.getId()) : this.config.addBookmark(node.getId());
    bookmark.setTitle(PageUtil.encode(node.getTitle().trim()));
    node.forEach(n -> n.accept(this, bookmark));
  }
}
