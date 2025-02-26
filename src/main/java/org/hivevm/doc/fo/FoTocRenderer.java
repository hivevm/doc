// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.Template;

import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * The {@link FoTocRenderer} class.
 */
class FoTocRenderer extends FoAbstractRenderer<FoNode> {

    private final String id;

    /**
     * Creates an instance of {@link FoTocRenderer} for the provided getTemplate().
     */
    public FoTocRenderer(Template template, String tocId,
                         Map<String, BiConsumer<FoPageSequence, Properties>> pageSet) {
        super(template, pageSet);
        this.id = tocId;
    }

    @Override
    public final void visit(Document doc, FoNode root) {
        String title = "Table of Contents";
        Properties properties = new Properties();
        properties.put("TITLE", title);
        FoFlow flow = createPageSequence(id, Fo.PAGESET_STANDARD, (FoRoot) root, true, properties);

        FoBlock content = FoBlock.block(flow)
                .setBreakBefore("page")
                .setSpaceBefore("0.5em", "1.0em", "2.0em")
                .setSpaceAfter("0.5em", "1.0em", "2.0em")
                .setColor("#000000")
                .setTextAlign("left");
        content.setId(id);

        FoBlock block = content.addBlock();
        block.setSpaceBefore("1.0em", "1.5em", "2.0em");
        block.setSpaceAfter("0.5em").setStartIndent("0pt");
        block.setFontWeight("bold").setFontSize("18pt");
        block.addText(title);

        doc.stream().forEach(n -> n.accept(this, content));
    }

    @Override
    public final void visit(Header node, FoNode data) {
        int intent = node.getLevel() - 1;
        String title = PageUtil.encode(node.getTitle());

        FoBlock block = ((FoBlock) data).addBlock();
        block.setMarginLeft(String.format("%sem", intent));
        block.setTextAlignLast("justify");

        FoBasicLink link = new FoBasicLink(node.getId(), block);

        FoBlock inline = FoBlock.inline(link);
        inline.setKeepWithNext("always")
                .addText(PageUtil.encode(title));

        new FoLeader(inline)
                .setPattern("dots")
                .setWidth("3pt")
                .setAlign("reference-area")
                .setPaddingLeftRight("3pt");
        new FoPageNumberCitation(node.getId(), inline);
    }
}
