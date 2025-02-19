// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.List;

/**
 * The {@link Table} class.
 */
public interface Table extends Node {

    enum AreaType {
        HEAD,
        BODY,
        TAIL
    }

    boolean isVirtual();

    String getBorderColor();

    String getBackgroundColor();

    List<Column> getColumns();

    List<Area> getAreas();

    interface Column {

        int getIndex();

        int getWidth();

        String getAlign();
    }

    interface Area {

        AreaType getType();

        List<Row> getRows();
    }

    interface Row {

        List<Cell> getCells();
    }

    interface Cell {

        int getRowSpan();

        int getColSpan();

        String getAlign();

        Paragraph getContent();
    }
}
