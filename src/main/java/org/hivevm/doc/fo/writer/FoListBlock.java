// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.*;

/**
 * The {@link FoListBlock} class.
 */
public class FoListBlock extends FoNode implements FoSpace<FoListBlock>, FoMargin<FoListBlock>,
        FoFont<FoListBlock>,
        FoBreak<FoListBlock>, FoBackground<FoListBlock>, FoIndent<FoListBlock> {

    /**
     * Constructs an instance of {@link FoListBlock}.
     */
    public FoListBlock(FoNode parent) {
        super("list-block", parent);
    }

    public FoListBlock setKeepWithNext(String keep) {
        set("keep-with-next.within-column", keep);
        return this;
    }

    public FoListBlock setLabelSeparation(String value) {
        set("provisional-label-separation", value);
        return this;
    }

    public FoListBlock setDistanceBetweenStarts(String value) {
        set("provisional-distance-between-starts", value);
        return this;
    }
}
