// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoBackground;
import org.hivevm.doc.fo.writer.set.FoBorder;
import org.hivevm.doc.fo.writer.set.FoFont;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoTableRow} class.
 */
public class FoTableRow extends FoNode implements FoBorder<FoTableRow>, FoFont<FoTableRow>,
        FoBackground<FoTableRow> {

    /**
     * Constructs an instance of {@link FoTableRow}.
     */
    FoTableRow(XmlBuilder builder) {
        super("fo:table-row", builder);
    }

    public FoTableRow setDisplayAlign(String align) {
        set("display-align", align);
        return this;
    }

    public FoTableRow setProgessionDimensionMin(String value) {
        set("block-progression-dimension.minimum", value);
        return this;
    }

    public FoTableCell addCell() {
        FoTableCell cell = new FoTableCell(getBuilder());
        addNode(cell);
        return cell;
    }
}
