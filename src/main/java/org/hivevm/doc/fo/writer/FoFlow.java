// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.FoBlockContainer.Position;
import org.hivevm.doc.fo.writer.set.FoIndent;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoFlow} class.
 */
public class FoFlow extends FoAbstract implements FoIndent<FoFlow> {

    /**
     * Constructs an instance of {@link FoFlow}.
     *
     * @param reference
     */
    public FoFlow(String reference, XmlBuilder builder) {
        super("flow", builder);
        set("flow-name", reference);
    }

    public FoBlockContainer blockContainer(Position position) {
        FoBlockContainer builder = new FoBlockContainer(position, getBuilder());
        addNode(builder);
        return builder;
    }

    public FoBlock addBlock() {
        FoBlock builder = FoBlock.block(getBuilder());
        addNode(builder);
        return builder;
    }

    public FoNode addBlock(FoNode node) {
        addNode(node);
        return node;
    }
}
