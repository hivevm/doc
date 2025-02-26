// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

/**
 * The {@link FoPageSequence} class.
 */
public class FoPageSequence extends FoNode {

    /**
     * Constructs an instance of {@link FoPageSequence}.
     *
     * @param reference
     */
    public FoPageSequence(String reference, FoRoot root) {
        super("page-sequence", root);
        set("master-reference", reference);
    }

    public FoPageSequence setLanguage(String language) {
        set("language", language);
        return this;
    }

    public FoPageSequence setFormat(String format) {
        set("format", format);
        return this;
    }

    public FoPageSequence setInitialPageNumber(String initial) {
        set("initial-page-number", initial);
        return this;
    }

    public FoPageSequence setForcePageCount(String force) {
        set("force-page-count", force);
        return this;
    }

    public FoFlow flow(String reference) {
        return new FoFlow(reference, this);
    }
}
