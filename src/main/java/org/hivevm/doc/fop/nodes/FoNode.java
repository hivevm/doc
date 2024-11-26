// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hivevm.doc.fop.nodes.set.Fo;

/**
 * The {@link FoNode} is a builder implementation for Apache Formating Objects.
 */
public class FoNode implements Fo, Iterable<FoNode> {

  private final String              name;
  private final Map<String, String> attributes = new LinkedHashMap<>();
  private final List<FoNode>        children   = new ArrayList<>();

  /**
   * Constructs an instance of {@link FoNode}.
   *
   * @param name
   */
  protected FoNode(String name) {
    this.name = name;
  }

  /**
   * Get the name.
   */
  protected final String getTagName() {
    return this.name;
  }

  /**
   * Get the attributes.
   */
  protected final Map<String, String> getAttributes() {
    return this.attributes;
  }

  /**
   * Returns {@code true} if this list contains no elements.
   *
   * @return {@code true} if this list contains no elements
   */
  public final boolean hasChildren() {
    return !this.children.isEmpty();
  }

  /**
   * Set the ID.
   *
   * @param id
   */
  public final FoNode setId(String id) {
    return set("id", id);
  }

  /**
   * Set an attribute.
   *
   * @param name
   * @param value
   */
  @Override
  public final FoNode set(String name, String value) {
    this.attributes.put(name, value);
    return this;
  }

  /**
   * Add a new child {@link FoNode}.
   *
   * @param node
   */
  public final FoNode addNode(FoNode builder) {
    this.children.add(builder);
    return this;
  }

  /**
   * Add a new child text {@link FoNode}.
   *
   * @param text
   */
  public final void addText(String text) {
    addNode(FoNode.text(text));
  }

  /**
   * Returns an iterator over child {@link FoNode}.
   */
  @Override
  public final Iterator<FoNode> iterator() {
    return this.children.iterator();
  }

  /**
   * Build the String.
   */
  public String build() {
    StringBuffer buffer = new StringBuffer();
    if (hasChildren()) {
      FoUtil.writeStart(buffer, getTagName(), getAttributes());
      forEach(n -> buffer.append(n.build()));
      FoUtil.writeEnd(buffer, getTagName());
    } else {
      FoUtil.writeEmpty(buffer, getTagName(), getAttributes());
    }
    return buffer.toString();
  }

  /**
   * Creates a new instance of a {@link FoRoot}.
   */
  public static FoRoot root(String font) {
    return new FoRoot(font);
  }

  /**
   * Constructs a text {@link FoNode}.
   *
   * @param text
   */
  public static FoNode text(String text) {
    return new FoNode(text) {

      @Override
      public final String build() {
        return getTagName();
      }
    };
  }

  /**
   * Creates a new instance of a {@link FoNode}.
   *
   * @param name
   */
  public static FoNode create(String name) {
    return new FoNode(name);
  }
}
