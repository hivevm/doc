// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoFont;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoFootnote} class.
 */
public class FoFootnote extends FoAbstract implements FoFont<FoFootnote> {

    private final FoBlock body;

    /**
     * Constructs an instance of {@link FoFootnote}.
     *
     * @param id
     */
    public FoFootnote(String id, XmlBuilder builder) {
        super("footnote", builder);
        FoBlock inline = FoBlock.inline(getBuilder());
        addNode(inline);
        inline.set("baseline-shift", "super").set("font-size", "smaller").addNode(FoNode.text(id, builder));

        FoNode content = new FoAbstract("footnote-body", builder);
        addNode(content);
        this.body = FoBlock.block(getBuilder());
        content.addNode(this.body);

        inline = FoBlock.inline(getBuilder());
        inline.set("baseline-shift", "super").set("font-size", "smaller").addNode(FoNode.text(id, builder));
        this.body.addNode(inline);
    }

    /**
     * Get the footnote body.
     */
    public FoBlock getBody() {
        return this.body;
    }
}
