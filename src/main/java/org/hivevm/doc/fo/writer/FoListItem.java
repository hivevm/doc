// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoBackground;
import org.hivevm.doc.fo.writer.set.FoFont;
import org.hivevm.doc.fo.writer.set.FoMargin;
import org.hivevm.doc.fo.writer.set.FoSpace;

/**
 * The {@link FoListItem} class.
 */
public class FoListItem extends FoNode
        implements FoSpace<FoListItem>, FoMargin<FoListItem>, FoFont<FoListItem>,
        FoBackground<FoListItem> {

    private final FoBlock content;

    /**
     * Constructs an instance of {@link FoListItem}.
     *
     * @param label
     */
    public FoListItem(String label, FoListBlock list) {
        super("list-item", list);
        setSpace("0.3em");

        FoNode head = new FoNode("list-item-label", this);
        head.set("end-indent", "label-end()");
        FoBlock.block(head).addContent(label);

        FoNode body = new FoNode("list-item-body", this);
        body.set("start-indent", "body-start()");

        this.content = FoBlock.block(body);
    }

    public FoListItem setKeepWithNext(String keep) {
        set("keep-with-next.within-column", keep);
        return this;
    }

    public FoBlock getContent() {
        return this.content;
    }
}
