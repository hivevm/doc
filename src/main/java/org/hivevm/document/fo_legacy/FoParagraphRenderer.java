// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.fo_legacy;

import java.util.HashSet;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.fo.Fo;
import org.hivevm.doc.fo.writer.FoBasicLink;
import org.hivevm.doc.fo.writer.FoBlock;
import org.hivevm.doc.fo.writer.FoExternalGraphic;
import org.hivevm.doc.fo.writer.FoFootnote;
import org.hivevm.doc.fo.writer.FoNode;

/**
 * The {@code FoRendererFlow} class implements the {@link Flow.Visitor} interface for rendering
 * flows into Apache Formatting Object (FO) nodes. This is used to transform structured flow objects
 * (text, images, links, styles, etc.) into FO-compatible output for document generation.
 */
class FoParagraphRenderer implements Flow.Visitor<FoNode> {

    private final FoWriter writer;
    private final Document document;

    private int footnoteCounter;

    /**
     * Constructs an instance of the FoRendererFlow class.
     */
    public FoParagraphRenderer(FoWriter writer, Document document) {
        this.writer = writer;
        this.document = document;
        this.footnoteCounter = 1;
    }

    /**
     * Processes a {@link Flow.Text} node and appends its content to an {@link FoNode}. Renders the
     * text content using template symbols and delegates handling of child flows.
     */
    @Override
    public final void visit(Flow.Text text, FoNode data) {
        var string = text.text() != null ? text.text() : "";//PageUtil.encode(text.text());
        writer.template().getSymbols()
            .forEach(string, (s, f) -> {
                if (f == null)
                    data.addText(s);
                else
                    data.create("character").set("font-family", f).set("character", s);
            });
        text.flows().forEach(f -> f.accept(this, data));
    }

    /**
     * Visits a {@link Flow.Image} node and processes it to create a corresponding {@link FoNode}.
     * This method processes the alignment, dimensions, and textual description of the image and
     * integrates it within the given data node structure.
     */
    @Override
    public final void visit(Flow.Image image, FoNode data) {
        if (image.align() != null) {
            var align = image.align();
            if (align.equals("float-left")) {
                var floating = new FoNode("float", data);
                floating.set("float", "left");
                data = FoBlock.block(floating);
                data.set("padding", "5px");
            }
            else if (align.equals("float-right")) {
                var floating = new FoNode("float", data);
                floating.set("float", "right");
                data = FoBlock.block(floating);
                data.set("padding", "5px");
            }
            else
                data.set("text-align", image.align());
        }

        var graphic = new FoExternalGraphic(data);
        graphic.setURL(image.url());
        graphic.setSize(image.width(), image.height());
        graphic.set("content-width", "scale-to-fit");
        graphic.set("content-height", "scale-to-fit");
        graphic.set("max-width", "95%");

        if (image.text() != null) {
            String text = Fo.encode(image.text());
            FoBlock block = FoBlock.block(data);
            block.setFontStyle("italic").addContent(text);
        }
    }

    /**
     * Processes a {@link Flow.Link} node and constructs its representation in the document
     * structure. Converts the given link into an {@link FoBasicLink} and applies the appropriate
     * styling. Handles both internal and external destinations while managing link titles as
     * footnotes if available. This method also delegates the processing of child flows associated
     * with the link.
     *
     * @param link the {@link Flow.Link} instance representing the link to be processed
     * @param data the parent {@link FoNode} to which the link and its contents should be added
     */
    @Override
    public final void visit(Flow.Link link, FoNode data) {
        var destination = link.link();
        if (destination.startsWith("#"))
            destination = document.getId(destination);

        var basicLink = new FoBasicLink(data);
        basicLink.setDestination(destination);
        writer.renderStyle(basicLink);
        link.flows().forEach(f -> f.accept(this, basicLink));

        if (link.title() != null) {
            var id = String.format("(%s)", footnoteCounter++);
            var footnote = new FoFootnote(id, data);
            var content = footnote.getBody().addContent(" ");

            var fn = new FoBasicLink(content);
            writer.renderStyle(fn);
            fn.setDestination(destination);
            fn.addText(link.title());
        }
    }

    /**
     * Processes a {@link Flow.Style} node and applies its styling properties to the corresponding
     * {@link FoNode}. The method adjusts text appearance, including font weight, style,
     * decorations, and color, based on the style's characteristics, and delegates further
     * processing to child flows.
     */
    @Override
    public final void visit(Flow.Style style, FoNode data) {
        var inline = FoBlock.inline(data);

        if (style.kinds().contains(Flow.Kind.CODE))
            writer.renderStyle(inline, "styled");
        if (style.kinds().contains(Flow.Kind.BOLD))
            inline.setFontWeight("bold");
        if (style.kinds().contains(Flow.Kind.ITALIC))
            inline.setFontStyle("italic");

        var decorations = new HashSet<String>();
        if (style.kinds().contains(Flow.Kind.OVERLINE))
            decorations.add("overline");
        if (style.kinds().contains(Flow.Kind.UNDERLINE))
            decorations.add("underline");
        if (style.kinds().contains(Flow.Kind.STRIKETHROUGH))
            decorations.add("line-through");
        if (style.kinds().contains(Flow.Kind.HIGHLIGHT)) {
            decorations.add("overline");
            decorations.add("underline");
        }
        if (!decorations.isEmpty())
            inline.setTextDecoration(String.join(" ", decorations));

        if (style.color() != null)
            inline.setColor(style.color());

        style.flows().forEach(f -> f.accept(this, inline));
    }
}
