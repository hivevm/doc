// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.markdown;

import org.commonmark.node.CustomNode;
import org.hivevm.doc.commonmark.MarkdownVisitor;
import org.hivevm.doc.commonmark.tables.TableBody;
import org.hivevm.doc.commonmark.tables.TableCell;
import org.hivevm.doc.commonmark.tables.TableHead;
import org.hivevm.doc.commonmark.tables.TableRow;
import org.hivevm.doc.tree.builder.TableBuilder;
import org.hivevm.doc.tree.builder.TableBuilder.CellBuilder;
import org.hivevm.doc.tree.codeblock.CodeFactory;

/**
 * The {@link MarkdownTable} class.
 */
class MarkdownTable extends MarkdownVisitor {

  private final TableBuilder table;
  private final CodeFactory  factory;

  /**
   * Constructs an instance of {@link MarkdownTable}.
   *
   * @param table
   * @param factory
   */
  public MarkdownTable(TableBuilder table, CodeFactory factory) {
    this.table = table;
    this.factory = factory;
  }

  /**
   * Get the current {@link TableBuilder} .
   */
  protected final TableBuilder getTable() {
    return this.table;
  }

  /**
   * Processes the {@link CustomNode} of a table
   *
   * @param node
   */
  @Override
  public final void visit(CustomNode node) {
    if (node instanceof TableHead) {
      getTable().addHead();
      visitChildren(node);
    } else if (node instanceof TableBody) {
      getTable().addBody();
      visitChildren(node);
    } else if (node instanceof TableRow) {
      getTable().addRow();
      visitChildren(node);
    } else if (node instanceof TableCell) {
      TableCell cell = (TableCell) node;
      if (node.getParent().getParent() instanceof TableHead) {
        getTable().addColumn(cell.getWidth(), cell.getAlignment().name().toLowerCase());
      }
      CellBuilder content = getTable().addCell(1, 1);
      MarkdownBuilder builder = new MarkdownBuilder(content.getContent(), this.factory, 0);
      builder.visitChildren(node);
    } else {
      super.visitChildren(node);
    }
  }
}
