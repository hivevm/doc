// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hivevm.doc.Book;
import org.hivevm.doc.Chapter;
import org.hivevm.doc.Node;

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
  public BookBuilder(Set<String> keywords) {
    super(0, null);
    BookConfig.init(keywords);
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
    String id = BookBuilder.getLinkId(key.substring(1));
    return this.identifiers.containsKey(id) ? this.identifiers.get(id) : key;
  }

  @Override
  protected void addIndex(Chapter node) {
    if (!node.getTitle().isEmpty()) {
      String title = BookBuilder.getLinkId(node.getTitle());
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

  /**
   * Gets the id of the link.
   *
   * @param title
   */
  public static String getLinkId(String title) {
    return title.toLowerCase().replace(" ", "-");
  }

  public final Book build() {
    processNode(this);
    return this;
  }
}
