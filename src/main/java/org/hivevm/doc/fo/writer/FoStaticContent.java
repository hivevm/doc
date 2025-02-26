// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

/**
 * The {@link FoStaticContent} class.
 */
public class FoStaticContent extends FoNode {

    public FoStaticContent(String reference, FoPageSequence page) {
        super("static-content", page);
        set("flow-name", reference);
    }

    public FoBlock addBlock() {
        return FoBlock.block(this);
    }

    public FoBlockContainer blockContainer(FoBlockContainer.Position position) {
        return new FoBlockContainer(position, this);
    }
}
