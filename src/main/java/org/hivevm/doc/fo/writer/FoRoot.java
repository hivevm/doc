// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoRoot} class.
 */
public class FoRoot extends FoAbstract {

    /**
     * Constructs an instance of {@link FoRoot}.
     */
    public FoRoot(String font, XmlBuilder builder) {
        super("root", builder);
        builder.push("root");
        set("font-family", font).set("font-size", "10pt");
        set("font-selection-strategy", "character-by-character");
        set("text-align", "justify").set("line-height", "1.4em");
        set("line-height-shift-adjustment", "disregard-shifts");
        set("writing-mode", "lr-tb").set("language", "en");
    }

    /**
     * Get the {@link FoNode} for the layout master set.
     */
    public final FoAbstract createLayout() {
        return new FoAbstract("layout-master-set", this);
    }

    /**
     * Get the {@link FoNode} for the layout master set.
     */
    public final FoAbstract createBookmark() {
        return new FoAbstract("bookmark-tree", this);
    }

    /**
     * Set an attribute.
     *
     * @param name
     * @param value
     */
    @Override
    public final FoNode set(String name, String value) {
        if (hasChildren())
            throw new IllegalArgumentException();
        return setAttribute(name, value);
    }

    public FoPageSequence addPageSequence(String reference) {
        FoPageSequence sequence = new FoPageSequence(reference, getBuilder());
        addNode(sequence);
        return sequence;
    }

    /**
     * Build the FO document.
     */
    @Override
    public final void close() {
        XmlBuilder builder = getBuilder();
        getAttributes().keySet().stream()
                .filter(n -> getAttributes().get(n) != null)
                .forEach(n -> builder.set(n, getAttributes().get(n)));
        forEach(FoNode::close);
        builder.build();
    }
}
