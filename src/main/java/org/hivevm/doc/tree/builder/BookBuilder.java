// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import org.hivevm.doc.tree.Book;
import org.hivevm.doc.tree.Chapter;
import org.hivevm.doc.tree.Node;
import org.hivevm.doc.util.PageUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link BookBuilder} class.
 */
public class BookBuilder extends PageBuilder implements Book {

  private int                       prefaceOffset = 0;
  private int                       chapterOffset = 0;
  private final Map<String, String> identifiers   = new HashMap<>();

  /**
   * Constructs an instance of {@link BookBuilder}.
   */
  public BookBuilder() {
    super(0, null);
  }

  @Override
  public final int getOffset() {
    return -1;
  }

  @Override
  public PageBuilder addSection() {
    return add(new ChapterBuilder(this, 2, this.prefaceOffset++));
  }

  public PageBuilder addChapter() {
    return add(new ChapterBuilder(this, 1, this.chapterOffset++));
  }

  private final String getId(String key) {
    String id = PageUtil.getLinkId(key.substring(1));
    return this.identifiers.containsKey(id) ? this.identifiers.get(id) : key;
  }

  @Override
  protected void addIndex(Chapter node) {
    if (!node.getTitle().isEmpty()) {
      String title = PageUtil.getLinkId(node.getTitle());
      this.identifiers.put(title, node.getId());
    }
  }

  private void processNode(Node node) {
    if (node instanceof LinkBuilder) {
      LinkBuilder link = (LinkBuilder) node;
      if (link.getLink().startsWith("#")) {
        link.setLink(getId(link.getLink()));
      }
    }
    node.nodes().forEach(this::processNode);
  }

  public final Book build() {
    processNode(this);
    return this;
  }
}
