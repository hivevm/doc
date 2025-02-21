// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.*;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoBlock} class.
 */
public class FoBlock extends FoAbstract implements FoSpace<FoBlock>, FoMargin<FoBlock>,
        FoBorder<FoBlock>,
        FoPadding<FoBlock>, FoFont<FoBlock>, FoBreak<FoBlock>, FoBackground<FoBlock>,
        FoIndent<FoBlock> {

    /**
     * Constructs an instance of {@link FoBlock}.
     *
     * @param name
     */
    private FoBlock(String name, XmlBuilder builder) {
        super(name, builder);
    }

    public FoBlock setTextAlignLast(String align) {
        set("text-align-last", align);
        return this;
    }

    public FoBlock setKeepWithNext(String keep) {
        set("keep-with-next.within-column", keep);
        return this;
    }

    public FoBlock setWarp(String value) {
        set("wrap-option", value);
        return this;
    }

    public FoBlock setWhiteSpaceCollapse(String value) {
        set("white-space-collapse", value);
        return this;
    }

    public FoBlock setWhiteSpaceTreatment(String value) {
        set("white-space-treatment", value);
        return this;
    }

    public FoBlock setLineFeed(String value) {
        set("linefeed-treatment", value);
        return this;
    }

    public FoBlock setSpan(String value) {
        set("span", value);
        return this;
    }

    public FoBlock setWidth(String value) {
        set("width", value);
        return this;
    }

    public FoBlock setHeight(String value) {
        set("height", value);
        return this;
    }

    /**
     * Add a simple content.
     *
     * @param content
     */
    public FoBlock addContent(String content) {
        addText(content);
        return this;
    }

    public FoBlock addBlock() {
        FoBlock block = FoBlock.block(getBuilder());
        addNode(block);
        return block;
    }

    public FoBlock addInline() {
        FoBlock block = FoBlock.inline(getBuilder());
        addNode(block);
        return block;
    }

    public static FoBlock block(XmlBuilder builder) {
        return new FoBlock("block", builder);
    }

    public static FoBlock inline(XmlBuilder builder) {
        return new FoBlock("inline", builder);
    }
}
