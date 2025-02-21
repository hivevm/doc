// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoBackground;
import org.hivevm.doc.fo.writer.set.FoFont;
import org.hivevm.doc.fo.writer.set.FoMargin;
import org.hivevm.doc.fo.writer.set.FoSpace;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoListItem} class.
 */
public class FoListItem extends FoAbstract
        implements FoSpace<FoListItem>, FoMargin<FoListItem>, FoFont<FoListItem>,
        FoBackground<FoListItem> {

    private final FoBlock content;

    /**
     * Constructs an instance of {@link FoListItem}.
     *
     * @param label
     */
    public FoListItem(String label, XmlBuilder builder) {
        super("list-item", builder);

        FoNode head = FoNode.create("list-item-label", builder);
        head.set("end-indent", "label-end()");
        head.addNode(FoBlock.block(builder).addContent(label));

        FoNode body = FoNode.create("list-item-body", builder);
        body.set("start-indent", "body-start()");

        this.content = FoBlock.block(builder);
        body.addNode(this.content);

        addNode(head);
        addNode(body);
    }

    public FoListItem setKeepWithNext(String keep) {
        set("keep-with-next.within-column", keep);
        return this;
    }

    public FoBlock getContent() {
        return this.content;
    }
}
