// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoPageSequence} class.
 */
public class FoPageSequence extends FoAbstract {

    /**
     * Constructs an instance of {@link FoPageSequence}.
     *
     * @param reference
     */
    public FoPageSequence(String reference, XmlBuilder builder) {
        super("page-sequence", builder);
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
        FoFlow builder = new FoFlow(reference, getBuilder());
        addNode(builder);
        return builder;
    }
}
