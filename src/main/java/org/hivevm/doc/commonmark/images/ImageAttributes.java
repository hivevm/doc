// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.images;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

import java.util.Map;

/**
 * A node containing text and other inline nodes as children.
 */
public class ImageAttributes extends CustomNode implements Delimited {

  private final Map<String, String> attributes;

  public ImageAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getOpeningDelimiter() {
    return "{";
  }

  @Override
  public String getClosingDelimiter() {
    return "}";
  }

  public Map<String, String> getAttributes() {
    return this.attributes;
  }

  @Override
  protected String toStringAttributes() {
    return "imageAttributes=" + this.attributes;
  }
}
