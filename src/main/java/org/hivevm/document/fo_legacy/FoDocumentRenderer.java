// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.fo_legacy;

import org.hivevm.doc.api.*;
import org.hivevm.doc.api.Table.AreaType;
import org.hivevm.doc.api.Table.Column;
import org.hivevm.doc.api.Table.Row;
import org.hivevm.doc.fo.Fo;
import org.hivevm.doc.fo.writer.*;

import java.util.Properties;

/**
 * The FoDocument class represents a visitor that processes a Document and its nodes (content
 * elements) to generate an XSL-FO (Extensible Stylesheet Language Formatting Objects) structure.
 * This structure can then be used for generating printable documents such as PDFs.
 * <p>
 * The class supports rendering various structural elements such as headers, paragraphs, tables,
 * lists, and other content types. It also manages the creation of specialized sections like the
 * Table of Contents and applies styles to ensure proper formatting of the generated document.
 */
public class FoDocumentRenderer implements Document.Visitor<FoWriter> {

    private Headers headers;
    private FoParagraphRenderer renderer;

    /**
     * Visits a {@link Document} instance and processes its content to generate the FO structure.
     * This method handles the creation of the Table of Contents, rendering the cover page, and
     * processing the document elements.
     */
    @Override
    public final void visit(Document doc, FoWriter writer) {
        this.headers = FoTOCRenderer.createHeaders(doc);
        this.renderer = new FoParagraphRenderer(writer, doc);

        // Renders the cover page
        var properties = new Properties();
        properties.put("TITLE", doc.getTitle());
        properties.put("PAGE_NUMBER", "<fo:page-number/>");

        var toc = new FoTOCRenderer(headers);
        toc.renderBookmark(doc, writer);

        writer.createFlow(Fo.BOOK_ID, Fo.PAGESET_BOOK, properties);
        var block = FoBlock.block(writer.flow());

        writer.renderStyle(block, "subtitle");
        doc.elements().forEach(c -> c.accept(this, writer));

        toc.renderToc(doc, writer);
    }


    /**
     * Renders a {@link Header} node.
     */
    @Override
    public final void visit(Header header, FoWriter writer) {
        var title = headers.getTitle(header);

        Properties properties = new Properties();
        properties.put("CHAPTER", header.getLevel() /* PageUtil.getNumber(node)*/);
        properties.put("TITLE", title);

        if (header.getLevel() == 1)
            writer.createFlow(header.getId(), Fo.PAGESET_CHAPTER, properties);

        FoBlock block = FoBlock.block(writer.flow());
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

        writer.renderStyle(block, "h" + header.getLevel());

        block.addText(headers.getChapter(header));

        header.flow().accept(renderer, block);
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     */
    @Override
    public final void visit(Break node, FoWriter writer) {
        var content = FoBlock.block(writer.top());
        writer.renderStyle(content, "p");
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     */
    @Override
    public final void visit(Paragraph paragraph, FoWriter writer) {
        FoBlock content = FoBlock.block(writer.top());
        writer.renderStyle(content, "p");
        paragraph.flow().accept(renderer, content);
    }

    /**
     * Renders an alert {@link Block} node.
     */
    @Override
    public final void visit(Block block, FoWriter writer) {
        var content = FoBlock.block(writer.top());
        if (block.kind() == Block.Kind.BLOCK) // Blockquote
            writer.renderStyle(content, "block");
        else {
            writer.renderStyle(content, block.kind().name().toLowerCase());
            content.setKeepWithPage("always");
        }

        writer.push(content);
        block.elements().forEach(c -> c.accept(this, writer));
        writer.pop();
    }

    /**
     * Renders a {@link List} node.
     */
    @Override
    public final void visit(List list, FoWriter writer) {
        FoListBlock block = new FoListBlock(writer.top());

        block.setEndIndent("1em")
                .setDistanceBetweenStarts("1.0em")
                .setLabelSeparation("0.2em");

        int index = 0;
        for (List.Item child : list.items()) {
            String label = list.isOrdered() ? String.format("%s.", ++index) : "•";
            FoListItem item = new FoListItem(label, block);

            writer.push(item.getContent());
            child.elements().forEach(i -> i.accept(this, writer));
            writer.pop();
        }
    }

    @Override
    public final void visit(CodeBlock code, FoWriter writer) {
        FoBlock content = FoBlock.block(writer.top());
        writer.renderStyle(content, "code");
        var lines = code.lines();

        content.setWarp("wrap");
        content.setLineFeed("preserve");
// TODO: Disabled due XML
//        content.setKeepWithPage(lines.size() > 10 ? "auto" : "always");
        content.setWhiteSpaceCollapse("false");
        content.setWhiteSpaceTreatment("preserve");
        content.setBorderRadius("0.2em");

        lines.forEach(f -> f.accept(renderer, FoBlock.block(content)));
    }

    /**
     * Renders an alert {@link Table} node.
     */
    @Override
    public final void visit(Table table, FoWriter writer) {
        FoTable foTable = new FoTable(writer.top());
        foTable.setTableLayout("fixed");

        if (table.isVirtual()) {
            if (table.getBorderColor() != null) {
                foTable.setBorder(".5px", "solid", table.getBorderColor());
            }
            foTable.setBackgroundColor(table.getBackgroundColor());
        } else {
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
                    } else {
                        cell.setDisplayAlign("center");
                        cell.setPadding("2pt");
                        cell.setBorderBottom("0.5px", "solid", "#777777");
                    }

                    writer.push(cell);
                    c.getContent().accept(this, writer);
                    writer.pop();
                }
            }
        }
    }
}
