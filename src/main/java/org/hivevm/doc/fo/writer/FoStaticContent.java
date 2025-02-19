// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fop.nodes.FoBlockContainer.Position;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoStaticContent} class.
 */
public class FoStaticContent extends FoNode {

    /**
     * Constructs an instance of {@link FoStaticContent}.
     *
     * @param reference
     */
    public FoStaticContent(String reference, XmlBuilder builder) {
        super("fo:static-content", builder);
        set("flow-name", reference);
    }

    public FoBlock addBlock() {
        FoBlock builder = FoBlock.block(getBuilder());
        addNode(builder);
        return builder;
    }

    public FoBlockContainer blockContainer(FoBlockContainer.Position position) {
        FoBlockContainer builder = new FoBlockContainer(position, getBuilder());
        addNode(builder);
        return builder;
    }
}
