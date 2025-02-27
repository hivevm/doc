// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hivevm.doc.Table;

/**
 * The {@link TableBuilder} class.
 */
public class TableBuilder extends NodeBuilder implements Table {

  private final boolean       isVirtual;

  private final List<Area>    areas       = new ArrayList<>();
  private final List<Column>  columns     = new ArrayList<>();
  private final List<Integer> columnWidth = new ArrayList<>();

  private String              borderColor;
  private String              backgroundColor;

  public TableBuilder(boolean isVirtual) {
    this.isVirtual = isVirtual;
  }

  @Override
  public final boolean isVirtual() {
    return this.isVirtual;
  }

  @Override
  public final List<Table.Column> getColumns() {
    return this.columns;
  }

  @Override
  public final List<Table.Area> getAreas() {
    return this.areas;
  }

  @Override
  public final String getBorderColor() {
    return this.borderColor;
  }

  @Override
  public final String getBackgroundColor() {
    return this.backgroundColor;
  }

  public final void setBorderColor(String borderColor) {
    this.borderColor = borderColor;
  }

  public final void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  /**
   * Calculates the relative size of a {@link ColumnBuilder}.
   *
   * @param column
   */
  private int getColumnWidth(ColumnBuilder column) {
    if (this.columnWidth.isEmpty()) {
      int count = 0;
      int sum = this.columns.stream().collect(Collectors.summingInt(c -> ((ColumnBuilder) c).width));
      for (int index = 0; index < this.columns.size(); index++) {
        int size = (int) ((((ColumnBuilder) this.columns.get(index)).width * 100f) / sum);
        count += size;
        if ((index + 1) == this.columns.size()) {
          size += 100 - count;
        }
        this.columnWidth.add(size);
      }
    }
    return this.columnWidth.get(this.columns.indexOf(column));
  }

  /**
   * Add a Column definition.
   */
  public final void addColumn(int width, String align) {
    this.columns.add(new ColumnBuilder(width, align));
  }

  public final void addHead() {
    this.areas.add(new AreaBuilder(AreaType.HEAD));
  }

  public final void addBody() {
    this.areas.add(new AreaBuilder(AreaType.BODY));
  }

  public final void addFoot() {
    this.areas.add(new AreaBuilder(AreaType.TAIL));
  }

  public final RowBuilder addRow() {
    RowBuilder row = new RowBuilder();
    AreaBuilder area = (AreaBuilder) this.areas.get(this.areas.size() - 1);
    area.rows.add(row);
    return row;
  }

  public CellBuilder addCell(int rows, int cols) {
    AreaBuilder area = (AreaBuilder) this.areas.get(this.areas.size() - 1);
    RowBuilder row = (RowBuilder) area.rows.get(area.rows.size() - 1);
    CellBuilder cell = row.addCell(rows, cols);
    add(cell.getContent());
    return cell;
  }

  /**
   * Defines a column definition
   */
  private class ColumnBuilder implements Table.Column {

    private final int    width;
    private final String align;

    /**
     * Constructs an instance of {@link ColumnBuilder}.
     *
     * @param width
     * @param align
     */
    private ColumnBuilder(int width, String align) {
      this.width = width;
      this.align = align;
    }

    @Override
    public int getIndex() {
      return TableBuilder.this.columns.indexOf(this);
    }

    @Override
    public int getWidth() {
      return getColumnWidth(this);
    }

    @Override
    public String getAlign() {
      return this.align;
    }
  }

  private class AreaBuilder implements Table.Area {

    private final AreaType  type;
    private final List<Row> rows = new ArrayList<>();

    public AreaBuilder(AreaType type) {
      this.type = type;
    }

    @Override
    public final AreaType getType() {
      return this.type;
    }

    @Override
    public final List<Row> getRows() {
      return this.rows;
    }
  }

  public class RowBuilder implements Table.Row {

    private final List<Cell> cells = new ArrayList<>();

    public CellBuilder addCell(int rows, int cols) {
      CellBuilder cell = new CellBuilder(this, rows, cols);
      this.cells.add(cell);
      return cell;
    }

    @Override
    public final List<Cell> getCells() {
      return this.cells;
    }
  }

  /**
   * Defines a column definition
   */
  public class CellBuilder implements Table.Cell {

    private final RowBuilder       parent;
    private final int              rows;
    private final int              cols;
    private final ParagraphBuilder content;

    private CellBuilder(RowBuilder parent, int rows, int cols) {
      this.parent = parent;
      this.rows = rows;
      this.cols = cols;
      this.content = new ParagraphBuilder();
    }

    @Override
    public int getRowSpan() {
      return this.rows;
    }

    @Override
    public int getColSpan() {
      return this.cols;
    }

    @Override
    public String getAlign() {
      return TableBuilder.this.columns.get(this.parent.cells.indexOf(this)).getAlign();
    }

    @Override
    public ParagraphBuilder getContent() {
      return this.content;
    }
  }
}
