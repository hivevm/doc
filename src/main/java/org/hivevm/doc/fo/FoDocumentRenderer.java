// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.List;
import org.hivevm.doc.api.*;
import org.hivevm.doc.api.Table.AreaType;
import org.hivevm.doc.api.Table.Column;
import org.hivevm.doc.api.Table.Row;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.Template;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * The {@link FoDocumentRenderer} class.
 */
class FoDocumentRenderer extends FoAbstractRenderer<FoNode> {

    public static final String BOOK_ID = "ROOT";

    private final FoRoot        root;
    private final String        fontCode;
    private final Stack<FoNode> nodes;
    private       FoFlow        flow;

    /**
     * Creates an instance of {@link FoDocumentRenderer} for the provided getTemplate().
     */
    public FoDocumentRenderer(FoRoot root, Template template,
                              Map<String, BiConsumer<FoPageSequence, Properties>> pageSet,
                              String fontCode) {
        super(template, pageSet);
        this.root = root;
        this.fontCode = fontCode;
        this.nodes = new Stack<>();
    }

    protected final String getCodeFont() {
        return this.fontCode;
    }

    /**
     * Get the top {@link FoNode}.
     */
    protected final FoNode top() {
        return this.nodes.isEmpty() ? this.flow : this.nodes.peek();
    }

    /**
     * Pop the top {@link FoNode}.
     */
    protected final FoNode pop() {
        return this.nodes.pop();
    }

    /**
     * Push the a {@link FoNode}.
     */
    protected final void push(FoNode node) {
        this.nodes.push(node);
    }

    @Override
    public final void visit(Document doc, FoNode root) {
        // Renders the cover page
        Properties properties = new Properties();
        properties.put("TITLE", doc.getTitle());
        properties.put("PAGE_NUMBER", "<fo:page-number/>");

        this.flow = createPageSequence(FoDocumentRenderer.BOOK_ID, Fo.PAGESET_BOOK,  (FoRoot) root, false, properties);
        FoBlock block = FoBlock.block(flow);
        applyStyle(block,"subtitle");

        doc.stream().forEach(c -> c.accept(this, top()));
    }


    /**
     * Renders a {@link Header} node.
     */
    @Override
    public final void visit(Header header, FoNode node) {
        String title = PageUtil.encode(header.getTitle());

        Properties properties = new Properties();
        properties.put("CHAPTER", header.getLevel() /* PageUtil.getNumber(node)*/);
        properties.put("TITLE", title);

        if (header.getLevel() == 1)
            this.flow = createPageSequence(header.getId(), Fo.PAGESET_CHAPTER, this.root, false, properties);

        FoBlock block = FoBlock.block(flow);
        block.setId(header.getId());

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

        applyStyle(block, "h" + header.getLevel());

        push(block);
        header.stream().forEach(c -> c.accept(this, block));
        pop();
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     */
    @Override
    public final void visit(Paragraph paragraph, FoNode node) {
        FoBlock content = FoBlock.block(top());

        if (paragraph.isBlock()) // Blockquote
            applyStyle(content, "block");
        else if (paragraph.getPaddingTop() != null) { // Custom paragraph only for API Code
            content.setBackgroundColor(paragraph.getBackground())
                    .setLineHeight("1.4em")
                    .setMargin(paragraph.getPaddingLeft(), paragraph.getPaddingRight(),
                            paragraph.getPaddingTop(), paragraph.getPaddingBottom());
        } else if (paragraph.isSoftBreak()) // Soft break
            applyStyle(content, "soft");
        else if (paragraph.isLineBreak()) // Hard break
            applyStyle(content, "hard");
        else // Standard break
            applyStyle(content, "p");

        push(content);
        paragraph.stream().forEach(c -> c.accept(this, content));
        pop();
    }

    /**
     * Renders a {@link Text} node. The renderer encodes all XML keywords and symbols with font
     * icons.
     */
    @Override
    public final void visit(Text text, FoNode node) {
        String string = PageUtil.encode(node.text());
        if (text.isCode()) {
            FoBlock content = FoBlock.inline(node);
            content.setTextAlign("start");
            content.setFontSize("10pt");
            content.setFontFamily(getCodeFont());
            content.setBackgroundColor("#eeeeee");
            content.setPadding("0.2em", "0.2em");
            content.setBorder(".5pt", "solid", "#aaaaaa");
            content.setBorderRadius(".2em");
            content.addText(string);
        } else {
            top().addText(forEachSymbol(string,
                    (s, f) -> FoBlock.inline(data.getRoot().getBuilder()).setFontFamily(f).setFontSize("14pt").addContent(s).build()));
        }
    }

    /**
     * Renders an {@link Image} node. Optionally a description will be provided.
     */
    @Override
    public final void visit(Image image, FoNode node) {
        if (image.align() != null) {
            top().set("text-align", image.align());
        }

        FoExternalGraphic graphic = new FoExternalGraphic(image.url(), top());
        graphic.setSize(image.width(), image.height());
        graphic.set("content-width", "scale-to-fit");
        graphic.set("content-height", "scale-to-fit");

        if (image.text() != null) {
            String text = PageUtil.encode(image.text());
            FoBlock.block(top()).setFontStyle("italic").addContent(text);
        }
    }

    /**
     * Renders a {@link Link} node.
     */
    @Override
    public final void visit(Link link, FoNode node) {
        FoBasicLink basicLink = new FoBasicLink(link.getLink(), node)
                .setColor(Fo.COLOR);

        push(basicLink);
        link.forEach(c -> c.accept(this, basicLink));
        pop();
    }

    /**
     * Renders a {@link List} node.
     */
    @Override
    public final void visit(List list, FoNode node) {
        FoListBlock block = new FoListBlock(top()).setStartIndent("1pc");
        block.setDistanceBetweenStarts("1.0em").setLabelSeparation("0.2em");
        block.setSpace("0.8em", "1.0em", "1.2em");

        int index = 0;
        for (Node child : list) {
            FoListItem item = new FoListItem(list.isOrdered() ? String.format("%s.", index++) : "•", block);

            push(item.getContent());
            child.stream().forEach(i -> i.accept(this, item.getContent()));
            pop();
        }
    }

    /**
     * Renders an alert {@link Table} node.
     */
    @Override
    public final void visit(Table table, FoNode node) {
        FoTable foTable = new FoTable(top()).setTableLayout("fixed");

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
            FoTableArea area = null;
            AreaType type = a.getType();
            switch (type) {
                case HEAD:
                    area = foTable.addHead();
                    break;

                case TAIL:
                    area = foTable.addFoot();
                    break;

                default:
                    area = foTable.addBody();
            }

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

                    push(cell);
                    c.getContent().accept(this, cell);
                    pop();
                }
            }
        }
    }

    /**
     * Renders an alert {@link Message} node.
     */
    @Override
    public final void visit(Message message, FoNode node) {
        FoBlock content = FoBlock.block(flow);
        applyStyle(content, message.getStyle().name().toLowerCase());

        push(content);
        message.forEach(c -> c.accept(this, content));
        pop();
    }

    private int footnote = 0;

    @Override
    public final void visit(Inline inline, FoNode node) {
        FoBlock content = null;

        if (inline.isFootnote()) {
            FoFootnote footnote = new FoFootnote(String.format("(%s)", ++this.footnote), node);
            content = footnote.getBody();
            content.addContent(" ");
        } else {
            content = FoBlock.inline(node);
            if (inline.isBold()) {
                content.setFontWeight("bold");
            }
            if (inline.isItalic()) {
                content.setFontStyle("italic");
            }
            Set<String> decoration = new HashSet<>();
            if (inline.isOverline()) {
                decoration.add("overline");
            }
            if (inline.isUnderline()) {
                decoration.add("underline");
            }
            if (inline.isStrikethrough()) {
                decoration.add("line-through");
            }
            if (!decoration.isEmpty()) {
                content.setTextDecoration(decoration.stream().collect(Collectors.joining(" ")));
            }

            if (inline.getColor() != null) {
                content.setColor(inline.getColor());
            }
            if (inline.getBackground() != null) {
                content.setBackgroundColor(inline.getBackground());
            }
            if (inline.getPaddingTop() != null) {
                content.setPadding(inline.getPaddingLeft(), inline.getPaddingRight(), inline.getPaddingTop(),
                        inline.getPaddingBottom());
            }
        }

        FoBlock block = content;
        push(content);
        inline.stream().forEach(c -> c.accept(this, block));
        pop();
    }

    @Override
    public final void visit(CodeNode code, FoNode node) {
        FoBlock content = FoBlock.block(top());
        applyStyle(content,"code");

        content.setFontFamily(getCodeFont());
        content.setFontSize(code.getFontSize());
        content.setWarp("wrap");
        content.setLineFeed("preserve");
        content.setWhiteSpaceCollapse("false");
        content.setWhiteSpaceTreatment("preserve");

        if (code.getTextColor() != null) {
            content.setColor(code.getTextColor());
        }
        content.setBackgroundColor(code.getBackground());
        if (code.isStyled()) {
            content.setPadding("0.1in", "0.75in");
            content.setMarginLeft("-0.75in");
            content.setMarginRight("-0.75in");
            content.setPadding("0.1in", "0.75in");
        } else if (code.isInline()) {
            content.setPadding("0.1in");
            content.setMarginLeft("0");
            content.setMarginRight("0");
        } else {
            content.setBorderRadius("0.2em");
            content.setBorderLeft(".5pt", "solid", code.getBorderColor());
            content.setBorderRight(".5pt", "solid", code.getBorderColor());
        }
        content.setBorderTop(".5pt", "solid", code.getBorderColor());
        content.setBorderBottom(".5pt", "solid", code.getBorderColor());

        push(content);
        code.stream().forEach(c -> c.accept(this, content));
        pop();
    }
}
