// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.tables;

import org.commonmark.node.CustomNode;

/**
 * Table cell of a {@link TableRow} containing inline nodes.
 */
public class TableCell extends CustomNode {

  private boolean   header;
  private int       width;
  private Alignment alignment;

  /**
   * @return whether the cell is a header or not
   */
  public boolean isHeader() {
    return this.header;
  }

  public void setHeader(boolean header) {
    this.header = header;
  }

  /**
   * @return the cell alignment
   */
  public int getWidth() {
    return this.width == 0 ? 1 : this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return the cell alignment
   */
  public Alignment getAlignment() {
    return this.alignment == null ? Alignment.LEFT : this.alignment;
  }

  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }

  /**
   * How the cell is aligned horizontally.
   */
  public enum Alignment {
    LEFT,
    CENTER,
    RIGHT
  }

}
