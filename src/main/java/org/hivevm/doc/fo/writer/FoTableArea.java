// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoBackground;
import org.hivevm.doc.fo.writer.set.FoIndent;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoTableArea} class.
 */
public class FoTableArea extends FoNode implements FoIndent<FoTableArea>,
        FoBackground<FoTableArea> {

    /**
     * Constructs an instance of {@link FoTableArea}.
     */
    FoTableArea(String name, XmlBuilder builder) {
        super(name, builder);
    }

    public FoTableRow addRow() {
        FoTableRow row = new FoTableRow(getBuilder());
        addNode(row);
        return row;
    }
}
