// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoIndent;

/**
 * The {@link FoFlow} class.
 */
public class FoFlow extends FoNode implements FoIndent<FoFlow> {

    /**
     * Constructs an instance of {@link FoFlow}.
     *
     * @param reference
     */
    public FoFlow(String reference, FoPageSequence sequence) {
        super("flow", sequence);
        set("flow-name", reference);
    }
}
