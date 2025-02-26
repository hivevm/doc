// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoRoot} class.
 */
public class FoRoot extends FoNode {

    /**
     * Constructs an instance of {@link FoRoot}.
     */
    public FoRoot(String font, XmlBuilder builder) {
        super("root", builder);
        set("font-family", font).set("font-size", "10pt");
        set("font-selection-strategy", "character-by-character");
        set("text-align", "justify").set("line-height", "1.4em");
        set("line-height-shift-adjustment", "disregard-shifts");
        set("writing-mode", "lr-tb").set("language", "en");
    }

    /**
     * Get the {@link FoNode} for the layout master set.
     */
    public final FoNode createLayout() {
        return new FoNode("layout-master-set", this);
    }

    /**
     * Get the {@link FoNode} for the layout master set.
     */
    public final FoNode createBookmark() {
        return new FoNode("bookmark-tree", this);
    }

    public FoPageSequence addPageSequence(String reference) {
        return new FoPageSequence(reference, this);
    }
}
