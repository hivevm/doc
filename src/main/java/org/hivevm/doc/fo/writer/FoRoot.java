// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoRoot} class.
 */
public class FoRoot extends FoNode {

    private final FoNode layouts;
    private final FoNode bookmarks;

    /**
     * Constructs an instance of {@link FoRoot}.
     */
    public FoRoot(String font, XmlBuilder builder) {
        super("fo:root", builder);
        this.layouts = FoNode.create("fo:layout-master-set", builder);
        this.bookmarks = FoNode.create("fo:bookmark-tree", builder);
        set("font-family", font).set("font-size", "10pt");
        set("font-selection-strategy", "character-by-character");
        set("text-align", "justify").set("line-height", "1.4em");
        set("line-height-shift-adjustment", "disregard-shifts");
        set("writing-mode", "lr-tb").set("language", "en");
    }

    /**
     * Get the {@link FoNode} for the layout master set.
     */
    public final FoNode getLayouts() {
        return this.layouts;
    }

    public FoBookmark addBookmark(String id) {
        FoBookmark bookmark = new FoBookmark(id, getBuilder());
        this.bookmarks.addNode(bookmark);
        return bookmark;
    }

    public FoPageSequence addPageSequence(String reference) {
        FoPageSequence sequence = new FoPageSequence(reference, getBuilder());
        addNode(sequence);
        return sequence;
    }

    public FoPageSequence addPageSequence(FoPageSequenceMaster pages) {
        FoPageSequence sequence = new FoPageSequence(pages.getPageName(), getBuilder());
        addNode(sequence);
        return sequence;
    }

    /**
     * Build the FO document.
     */
    @Override
    public final String build() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        FoUtil.writeStart(buffer, getTagName(), getAttributes());

        if (getLayouts().hasChildren()) {
            buffer.append(getLayouts().build());
        }

        if (this.bookmarks.hasChildren()) {
            buffer.append(this.bookmarks.build());
        }

        forEach(b -> buffer.append(b.build()));

        FoUtil.writeEnd(buffer, getTagName());
        buffer.append("\n");
        return buffer.toString();
    }
}
