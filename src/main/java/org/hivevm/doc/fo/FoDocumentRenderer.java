// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import java.util.Map;
import java.util.Properties;
import org.hivevm.doc.api.Block;
import org.hivevm.doc.api.Break;
import org.hivevm.doc.api.CodeBlock;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.api.List;
import org.hivevm.doc.api.Paragraph;
import org.hivevm.doc.api.Table;
import org.hivevm.doc.api.Table.AreaType;
import org.hivevm.doc.api.Table.Column;
import org.hivevm.doc.api.Table.Row;
import org.hivevm.doc.fo.writer.FoBasicLink;
import org.hivevm.doc.fo.writer.FoBlock;
import org.hivevm.doc.fo.writer.FoBlockContainer;
import org.hivevm.doc.fo.writer.FoExternalGraphic;
import org.hivevm.doc.fo.writer.FoFlow;
import org.hivevm.doc.fo.writer.FoLeader;
import org.hivevm.doc.fo.writer.FoListBlock;
import org.hivevm.doc.fo.writer.FoListItem;
import org.hivevm.doc.fo.writer.FoPageSequence;
import org.hivevm.doc.fo.writer.FoStaticContent;
import org.hivevm.doc.fo.writer.FoTable;
import org.hivevm.doc.fo.writer.FoTableArea;
import org.hivevm.doc.fo.writer.FoTableCell;
import org.hivevm.doc.fo.writer.FoTableRow;
import org.hivevm.doc.template.Page;
import org.hivevm.doc.template.PageColumn;
import org.hivevm.doc.template.PageImage;
import org.hivevm.doc.template.PageMatch;
import org.hivevm.doc.template.PageRegion;
import org.hivevm.doc.template.PageSet;
import org.hivevm.doc.template.PageStyle;
import org.hivevm.doc.template.Template;
import org.hivevm.util.DataUri;

/**
 * The FoRenderer class is responsible for rendering FO (Formatting Objects) structures from
 * document nodes provided by the {@link Document} API. It provides methods to handle various
 * document elements such as headers, paragraphs, lists, tables, and more.
 */
public class FoDocumentRenderer implements Document.Visitor<FoContext> {

    private TableOfContent   toc;
    private FoInlineRenderer inline;

    /**
     * Visits a {@link Document} instance and processes its content to generate the FO structure.
     * This method handles the creation of the Table of Contents, rendering the cover page, and
     * processing the document elements.
     */
    @Override
    public final void visit(Document doc, FoContext context) {
        var root = context.root();
        var template = context.template();

        this.toc = TableOfContent.create(doc);
        this.inline = new FoInlineRenderer(template, doc);

        // Renders the cover page
        var properties = new Properties();
        properties.put("TITLE", doc.getTitle());
        properties.put("PAGE_NUMBER", "<fo:page-number/>");

        toc.renderBookmark(doc, root);

        var page = root.addPageSequence(Fo.BOOK_ID)
            .setReference(Fo.PAGESET_BOOK)
            .setLanguage("en")
            .setInitialPageNumber("1")
            .setFormat("I");

        context.setFlow(createFlow(page, Fo.PAGESET_BOOK, template, properties));
        var block = FoBlock.block(context.flow());
        applyStyle(block, "subtitle", template);

        doc.elements().forEach(c -> c.accept(this, context));

        toc.renderToc(doc, context);
    }


    /**
     * Renders a {@link Header} node.
     */
    @Override
    public final void visit(Header header, FoContext context) {
        var title = toc.getTitle(header);
        var template = context.template();

        Properties properties = new Properties();
        properties.put("CHAPTER", header.getLevel() /* PageUtil.getNumber(node)*/);
        properties.put("TITLE", title);

        if (header.getLevel() == 1) {
            var page = context.root()
                .addPageSequence(header.getId())
                .setReference(Fo.PAGESET_CHAPTER)
                .setLanguage("en")
                .setInitialPageNumber("auto")
                .setFormat("1");
            context.setFlow(createFlow(page, Fo.PAGESET_CHAPTER, template, properties));
        }

        FoBlock block = FoBlock.block(context.flow());
        block.setId(header.getId());
        if (header.getLevel() == 2)
            block.setBreakBefore("page");

//                // Partial Table of Content
//                FoBlock index = flow.addBlock()
//                        .setColor("#130806")
//                        .setFontSize("12pt")
//                        .setTextAlign("left")
//                        .setLineHeight("1.8em");
//
//                node.stream()
//                        .filter(n -> n instanceof Header)
//                        .map(n -> (Header) n)
//                        .forEach(p -> FoDocumentRenderer.createTocEntry(p.getId(), PageUtil.encode(p.getTitle()), index.addBlock()));

        applyStyle(block, "h" + header.getLevel(), template);

        block.addText(toc.getChapter(header));

        header.flow().accept(inline, block);
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     */
    @Override
    public final void visit(Break node, FoContext context) {
        var template = context.template();
        var content = FoBlock.block(context.top());
        applyStyle(content, "p", template);
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     */
    @Override
    public final void visit(Paragraph paragraph, FoContext context) {
        var template = context.template();
        FoBlock content = FoBlock.block(context.top());
        applyStyle(content, "p", template);
        paragraph.flow().accept(inline, content);
    }

    /**
     * Renders an alert {@link Block} node.
     */
    @Override
    public final void visit(Block block, FoContext context) {
        var template = context.template();
        var content = FoBlock.block(context.top());
        if (block.kind() == Block.Kind.BLOCK) // Blockquote
            applyStyle(content, "block", template);
        else {
            applyStyle(content, block.kind().name().toLowerCase(), template);
            content.setKeepWithPage("always");
        }

        context.push(content);
        block.elements().forEach(c -> c.accept(this, context));
        context.pop();
    }

    /**
     * Renders a {@link List} node.
     */
    @Override
    public final void visit(List list, FoContext context) {
        FoListBlock block = new FoListBlock(context.top());

        block.setEndIndent("1em")
            .setDistanceBetweenStarts("1.0em")
            .setLabelSeparation("0.2em");

        int index = 0;
        for (List.Item child : list.items()) {
            String label = list.isOrdered() ? String.format("%s.", ++index) : "•";
            FoListItem item = new FoListItem(label, block);

            context.push(item.getContent());
            child.elements().forEach(i -> i.accept(this, context));
            context.pop();
        }
    }

    @Override
    public final void visit(CodeBlock code, FoContext context) {
        FoBlock content = FoBlock.block(context.top());
        applyStyle(content, "code", context.template());
        var lines = code.lines();

        content.setWarp("wrap");
        content.setLineFeed("preserve");
// TODO: Disabled due XML
//        content.setKeepWithPage(lines.size() > 10 ? "auto" : "always");
        content.setWhiteSpaceCollapse("false");
        content.setWhiteSpaceTreatment("preserve");
        content.setBorderRadius("0.2em");

        lines.forEach(f -> f.accept(inline, FoBlock.block(content)));
    }

    /**
     * Renders an alert {@link Table} node.
     */
    @Override
    public final void visit(Table table, FoContext context) {
        FoTable foTable = new FoTable(context.top());
        foTable.setTableLayout("fixed");

        if (table.isVirtual()) {
            if (table.getBorderColor() != null) {
                foTable.setBorder(".5px", "solid", table.getBorderColor());
            }
            foTable.setBackgroundColor(table.getBackgroundColor());
        }
        else {
            foTable.setSpace("1em");
            foTable.setBorderTop("1px", "solid", "#777777");
            foTable.setBorderBottom("1px", "solid", "#777777");
            foTable.setBorderBefore("retain").setBorderCollapse("collapse");
        }

        // Render columns
        for (Column column : table.getColumns()) {
            foTable.addColumn("" + (column.getIndex() + 1), column.getWidth() + "%");
        }

        // Render the areas
        for (Table.Area a : table.getAreas()) {
            AreaType type = a.getType();
            FoTableArea area = switch (type) {
                case HEAD -> foTable.addHead();
                case TAIL -> foTable.addFoot();
                default -> foTable.addBody();
            };

            // Render rows
            int i = 0;
            for (Row r : a.getRows()) {
                FoTableRow row = area.addRow();
                switch (type) {
                    case HEAD:
                        row.setFontWeight("bold");
                        row.setBackgroundColor("#cccccc");
                        break;

                    case BODY:
                        if (!table.isVirtual()) {
                            row.setBackgroundColor(((i++ % 2) == 1) ? "#eeeeee" : "#f7f7f7");
                        }
                        break;

                    default:
                        break;
                }

                // Render Cells
                for (Table.Cell c : r.getCells()) {
                    FoTableCell cell = row.addCell();
                    cell.setRowSpan(c.getRowSpan());
                    cell.setColSpan(c.getColSpan());
                    cell.setTextAlign(c.getAlign());

                    if (table.isVirtual()) {
                        cell.setBorder("0", "none", "transparent");
                    }
                    else {
                        cell.setDisplayAlign("center");
                        cell.setPadding("2pt");
                        cell.setBorderBottom("0.5px", "solid", "#777777");
                    }

                    context.push(cell);
                    c.getContent().accept(this, context);
                    context.pop();
                }
            }
        }
    }

    static void applyStyle(FoBlock block, String name, Template template) {
        PageStyle style = template.getStyle(name);

        block.setSpan(style.span);
        block.setColor(style.color);

        if (style.background != null) {
            if (style.background.isImage()) {
                block.setBackground(style.background.get(), "no-repeat");
            }
            else {
                block.setBackgroundColor(style.background.get());
            }
        }
        block.setFontSize(style.fontSize);
        block.setFontStyle(style.fontStyle);
        block.setFontWeight(style.fontWeight);
        block.setFontFamily(style.fontFamily);

        block.setTextAlign(style.textAlign);
        block.setLineHeight(style.lineHeight);
        block.setKeepWithNext(style.keepWithNext);

        if (style.spaceBefore != null) {
            String[] spaces = style.spaceBefore.split(",");
            if (spaces.length == 3)
                block.setSpaceBefore(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceBefore(style.spaceBefore);
        }

        if (style.spaceAfter != null) {
            String[] spaces = style.spaceAfter.split(",");
            if (spaces.length == 3)
                block.setSpaceAfter(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceAfter(style.spaceAfter);
        }

        block.setBorderRadius(style.borderRadius);
        if (style.borderTop != null) {
            String[] border = style.borderTop.split(" ");
            block.setBorderTop(border[0], border[1], border[2]);
        }
        if (style.borderLeft != null) {
            String[] border = style.borderLeft.split(" ");
            block.setBorderLeft(border[0], border[1], border[2]);
        }
        if (style.borderRight != null) {
            String[] border = style.borderRight.split(" ");
            block.setBorderRight(border[0], border[1], border[2]);
        }
        if (style.borderBottom != null) {
            String[] border = style.borderBottom.split(" ");
            block.setBorderBottom(border[0], border[1], border[2]);
        }

        block.setMarginTop(style.marginTop);
        block.setMarginLeft(style.marginLeft);
        block.setMarginRight(style.marginRight);
        block.setMarginBottom(style.marginBottom);

        block.setPaddingTop(style.paddingTop);
        block.setPaddingLeft(style.paddingLeft);
        block.setPaddingRight(style.paddingRight);
        block.setPaddingBottom(style.paddingBottom);

        block.setBreakBefore(style.breakBefore);
        block.setBreakAfter(style.breakAfter);
    }

    static void applyLinkStyle(FoBasicLink block, Template template) {
        var style = template.getStyle("link");

        block.setColor(style.color);

        block.setFontSize(style.fontSize);
        block.setFontStyle(style.fontStyle);
        block.setFontWeight(style.fontWeight);
        block.setFontFamily(style.fontFamily);

        block.setTextAlign(style.textAlign);
        block.setLineHeight(style.lineHeight);
    }

    /**
     * Set the supplier that creates a {@link FoFlow}.
     */
    public static FoFlow createFlow(FoPageSequence sequence, String name, Template template,
        Properties properties) {
        // Foot separator
        FoStaticContent content = new FoStaticContent(sequence);
        content.setFlowName("xsl-footnote-separator");

        FoBlock block = content.addBlock();
        block.setTextAlignLast("justify")
            .setPadding("0.5em");

        var leader = new FoLeader(block);
        leader.setPattern("rule")
            .setLength("50%")
            .setRuleThickness("0.5pt")
            .setColor("#777777");

        // Gets the page set for this page flow
        for (PageSet ps : template.pageSet()) {
            if (!ps.getName().equals(name))
                continue;

            for (Map.Entry<Page, PageMatch> e : ps.pages()) {
                e.getKey().forEachRegion(r -> renderRegion(e.getKey(), r, sequence, properties));
            }
        }

        FoFlow flow = sequence.flow();
        flow.setName("region-body");
        flow.setStartIndent("0pt").setEndIndent("0pt");
        return flow;
    }

    private static void renderRegion(Page page, PageRegion region, FoPageSequence node,
        Properties properties) {
        if (region.hasChildren() || (region.getRegion() == PageRegion.Region.BEFORE
            && page.background != null)) {
            FoStaticContent content = new FoStaticContent(node);
            content.setFlowName(region.getName());

            // Render Page fixed layouts (Watermarks)
            if (region.getRegion() == PageRegion.Region.BEFORE) {
                if (page.background != null)
                    renderWatermark(page, content);

                page.items.stream()
                    .filter(i -> i instanceof PageImage)
                    .map(i -> (PageImage) i)
                    .forEach(i -> {
                        FoBlockContainer container = content.blockContainer();
                        container.setAbsolute(FoBlockContainer.Position.Fixed)
                            .setPositionTop(i.getTop())
                            .setPositionLeft(i.getLeft())
//                                    .setPositionRight(i.getRight())
//                                    .setPositionBottom(i.getBottom())
                            .setWidth(i.getWidth())
                            .setHeight(i.getHeight());

                        FoBlock block = container.addBlock();

                        FoExternalGraphic image = new FoExternalGraphic(block);
                        image.setURL(DataUri.loadImage(i.uri));
                        image.setSize(i.getWidth(), i.getHeight());
                        image.setContentWidth("scale-to-fit");
                        image.setContentHeight("scale-to-fit");
                    });
            }

            region.forEachItem(p -> renderAbsolute(p, content, properties));
        }
    }

    private static void renderAbsolute(PageColumn panel, FoStaticContent node,
        Properties properties) {
        FoBlockContainer container = node.blockContainer();
        container.setAbsolute(FoBlockContainer.Position.Absolute)
            .setPosition(panel.getLeft(), panel.getRight(), panel.getTop(), panel.getBottom());

        container.setColor(panel.color);
        if (panel.background != null) {
            if (panel.background.isImage()) {
                container.setBackground(panel.background.get(), "no-repeat");
            }
            else {
                container.setBackgroundColor(panel.background.get());
            }
        }
        container.setFontSize(panel.fontSize);
        container.setFontStyle(panel.fontStyle);
        container.setFontWeight(panel.fontWeight);
        container.setFontFamily(panel.fontFamily);
        container.setTextAlign(panel.textAlign);
        container.setLineHeight(panel.lineHeight);

        FoRendererAbsolute renderer = new FoRendererAbsolute(properties);
        panel.items.forEach(c -> c.accept(renderer, container.addBlock()));
    }

    private static void renderWatermark(Page page, FoStaticContent content) {
        FoBlockContainer container = content.blockContainer();
        container.setAbsolute(FoBlockContainer.Position.Fixed)
            .setPositionTop("0")
            .setPositionLeft("0")
            .setPositionRight("0")
            .setPositionBottom("0");

        if (!page.background.isImage())
            container.setBackgroundColor(page.background.get());

        FoBlock block = container.addBlock();

        if (page.background.isImage()) {
            FoExternalGraphic watermark = new FoExternalGraphic(block);
            watermark.setURL(page.background.get());
            watermark.setContentWidth(page.pageWidth());
            watermark.setContentHeight(page.pageHeight());
        }
    }
}
