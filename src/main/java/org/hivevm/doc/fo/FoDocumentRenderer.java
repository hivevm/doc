// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.List;
import org.hivevm.doc.api.*;
import org.hivevm.doc.api.Table.AreaType;
import org.hivevm.doc.api.Table.Column;
import org.hivevm.doc.api.Table.Row;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.PageStyle;
import org.hivevm.doc.template.Template;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The {@link FoDocumentRenderer} class.
 */
class FoDocumentRenderer implements DocumentVisitor<FoContext> {

    private static final String BOOK_ID = "ROOT";

    private final Template template;

    /**
     * Creates an instance of {@link FoDocumentRenderer} for the provided template.
     *
     * @param template
     */
    public FoDocumentRenderer(Template template) {
        this.template = template;
    }

    @Override
    public final void visit(Document doc, FoContext data) {
        // Renders the Bookmark
        BookmarkRenderer renderer = new BookmarkRenderer();
        data.addBookmark(FoDocumentRenderer.BOOK_ID).setTitle(doc.getTitle());
        doc.stream().forEach(n -> n.accept(renderer, data));

        // Renders the cover page
        Properties properties = new Properties();
        properties.put("TITLE", doc.getTitle());
        properties.put("PAGE_NUMBER", "<fo:page-number/>");

        data.createFlow(FoDocumentRenderer.BOOK_ID, Fo.PAGESET_BOOK, false, properties, data.getRoot().getBuilder());
        PageStyle style = template.getStyle("subtitle");
        style.items.forEach(r -> r.accept(new FoRendererStyle(properties), data.getFlow()));

        doc.stream().forEach(c -> c.accept(this, data));

        // Table of Content
        data.createFlow("id_toc", Fo.PAGESET_STANDARD, true, properties, data.getRoot().getBuilder());
        createToc(doc, data);
    }


    /**
     * Renders a {@link Header} node.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Header node, FoContext data) {
        String chapter = "" + node.getLevel(); // PageUtil.getNumber(node);
        String title = PageUtil.encode(node.getTitle());

        Properties properties = new Properties();
        properties.put("CHAPTER", node.getLevel() /* PageUtil.getNumber(node)*/);
        properties.put("TITLE", new Supplier<String>() {
            @Override
            public String get() {
                FoBlock inline = FoBlock.inline(data.getRoot().getBuilder());
                if (node.getLevel() > 1)
                    inline.setId(node.getId());
                data.push(inline);
                node.stream().forEach(c -> c.accept(FoDocumentRenderer.this, data));
                data.pop();
                return inline.build();
            }
        });
        FoRendererStyle renderer = new FoRendererStyle(properties);

        PageStyle style = template.getStyle("h" + node.getLevel());
        switch (node.getLevel()) {
            case 1:
                data.createFlow(node.getId(), Fo.PAGESET_CHAPTER, false, properties, data.getRoot().getBuilder());
                style.items.forEach(r -> r.accept(renderer, data.getFlow()));

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
                break;

            default:
                style.items.forEach(r -> r.accept(renderer, data.getFlow()));
                break;
        }
    }

    /**
     * Renders a {@link Paragraph} node with different kind of break's.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Paragraph node, FoContext data) {
        FoBlock content = FoBlock.block(data.getRoot().getBuilder());

        if (node.isBlock()) { // Blockquote
            PageStyle style = template.getStyle("block");
            style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));
            content = (FoBlock) data.top().last();
        } else if (node.getPaddingTop() != null) { // Custom paragraph only for API Code
            content.setBackgroundColor(node.getBackground())
                    .setLineHeight("1.4em")
                    .setMargin(node.getPaddingLeft(), node.getPaddingRight(),
                            node.getPaddingTop(), node.getPaddingBottom());
            data.top().addNode(content);
        } else if (node.isSoftBreak()) { // Soft break
            PageStyle style = template.getStyle("soft");
            style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));
            content = (FoBlock) data.top().last();
        } else if (node.isLineBreak()) { // Hard break
            PageStyle style = template.getStyle("hard");
            style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));
            content = (FoBlock) data.top().last();
        } else { // Standard break
            PageStyle style = template.getStyle("p");
            style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));
            content = (FoBlock) data.top().last();
        }

        data.push(content);
        node.stream().forEach(c -> c.accept(this, data));
        data.pop();
    }

    /**
     * Renders a {@link Text} node. The renderer encodes all XML keywords and symbols with font
     * icons.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Text node, FoContext data) {
        String text = PageUtil.encode(node.text());
        if (node.isCode()) {
            FoBlock content = FoBlock.inline(data.getRoot().getBuilder());
            content.setTextAlign("start");
            content.setFontSize("10pt");
            content.setFontFamily(data.getCodeFont());
            content.setBackgroundColor("#eeeeee");
            content.setPadding("0.2em", "0.2em");
            content.setBorder(".5pt", "solid", "#aaaaaa");
            content.setBorderRadius(".2em");
            content.addText(text);
            data.top().addNode(content);
        } else {
            data.top().addText(data.forEachSymbol(text,
                    (s, f) -> FoBlock.inline(data.getRoot().getBuilder()).setFontFamily(f).setFontSize("14pt").addContent(s).build()));
        }
    }

    /**
     * Renders an {@link Image} node. Optionally a description will be provided.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Image node, FoContext data) {
        if (node.align() != null) {
            data.top().set("text-align", node.align());
        }

        FoExternalGraphic graphic = new FoExternalGraphic(node.url(), data.getRoot().getBuilder());
        graphic.setSize(node.width(), node.height());
        graphic.set("content-width", "scale-to-fit");
        graphic.set("content-height", "scale-to-fit");
        data.top().addNode(graphic);

        if (node.text() != null) {
            String text = PageUtil.encode(node.text());
            data.top().addNode(FoBlock.block(data.getRoot().getBuilder()).setFontStyle("italic").addContent(text));
        }
    }

    /**
     * Renders a {@link Link} node.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Link node, FoContext data) {
        FoBasicLink link = new FoBasicLink(node.getLink(), data.getRoot().getBuilder())
                .setColor(Fo.COLOR);
        data.top().addNode(link);

        data.push(link);
        node.forEach(c -> c.accept(this, data));
        data.pop();
    }

    /**
     * Renders a {@link List} node.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(List node, FoContext data) {
        FoListBlock list = new FoListBlock(data.getRoot().getBuilder()).setStartIndent("1pc");
        list.setDistanceBetweenStarts("1.0em").setLabelSeparation("0.2em");
        list.setSpace("0.8em", "1.0em", "1.2em");
        data.top().addNode(list);

        int index = 0;
        for (Node child : node) {
            FoListItem item = new FoListItem(node.isOrdered() ? String.format("%s.", index++) : "•", data.getRoot().getBuilder())
                    .setSpace("0.3em");
            list.addNode(item);

            data.push(item.getContent());
            child.stream().forEach(i -> i.accept(this, data));
            data.pop();
        }
    }

    /**
     * Renders an alert {@link Table} node.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Table node, FoContext data) {
        FoTable table = new FoTable(data.getRoot().getBuilder()).setTableLayout("fixed");

        if (node.isVirtual()) {
            if (node.getBorderColor() != null) {
                table.setBorder(".5px", "solid", node.getBorderColor());
            }
            table.setBackgroundColor(node.getBackgroundColor());
        } else {
            table.setSpace("1em");
            table.setBorderTop("1px", "solid", "#777777");
            table.setBorderBottom("1px", "solid", "#777777");
            table.setBorderBefore("retain").setBorderCollapse("collapse");
        }

        data.top().addNode(table);

        // Render columns
        for (Column column : node.getColumns()) {
            table.addColumn("" + (column.getIndex() + 1), column.getWidth() + "%");
        }

        // Render the areas
        for (Table.Area a : node.getAreas()) {
            FoTableArea area = null;
            AreaType type = a.getType();
            switch (type) {
                case HEAD:
                    area = table.addHead();
                    break;

                case TAIL:
                    area = table.addFoot();
                    break;

                default:
                    area = table.addBody();
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
                        if (!node.isVirtual()) {
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

                    if (node.isVirtual()) {
                        cell.setBorder("0", "none", "transparent");
                    } else {
                        cell.setDisplayAlign("center");
                        cell.setPadding("2pt");
                        cell.setBorderBottom("0.5px", "solid", "#777777");
                    }

                    data.push(cell);
                    c.getContent().accept(this, data);
                    data.pop();
                }
            }
        }
    }

    /**
     * Renders an alert {@link Message} node.
     *
     * @param node
     * @param data
     */
    @Override
    public final void visit(Message node, FoContext data) {
        PageStyle style = template.getStyle(node.getStyle().name().toLowerCase());
        style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));

        data.push(data.top().last());
        node.forEach(c -> c.accept(this, data));
        data.pop();
    }

    private int footnote = 0;

    @Override
    public final void visit(Inline node, FoContext data) {
        FoBlock content = null;

        if (node.isFootnote()) {
            FoFootnote footnote = new FoFootnote(String.format("(%s)", ++this.footnote), data.getRoot().getBuilder());
            data.top().addNode(footnote);
            content = footnote.getBody();
            content.addContent(" ");
        } else {
            content = FoBlock.inline(data.getRoot().getBuilder());
            if (node.isBold()) {
                content.setFontWeight("bold");
            }
            if (node.isItalic()) {
                content.setFontStyle("italic");
            }
            Set<String> decoration = new HashSet<>();
            if (node.isOverline()) {
                decoration.add("overline");
            }
            if (node.isUnderline()) {
                decoration.add("underline");
            }
            if (node.isStrikethrough()) {
                decoration.add("line-through");
            }
            if (!decoration.isEmpty()) {
                content.setTextDecoration(decoration.stream().collect(Collectors.joining(" ")));
            }

            if (node.getColor() != null) {
                content.setColor(node.getColor());
            }
            if (node.getBackground() != null) {
                content.setBackgroundColor(node.getBackground());
            }
            if (node.getPaddingTop() != null) {
                content.setPadding(node.getPaddingLeft(), node.getPaddingRight(), node.getPaddingTop(),
                        node.getPaddingBottom());
            }

            data.top().addNode(content);
        }

        data.push(content);
        node.stream().forEach(c -> c.accept(this, data));
        data.pop();
    }

    @Override
    public final void visit(CodeNode node, FoContext data) {
        PageStyle style = template.getStyle("code");
        style.items.forEach(r -> r.accept(new FoRendererStyle(), data.top()));

        FoBlock content = (FoBlock) data.top().last();
        content.setFontFamily(data.getCodeFont());
        content.setFontSize(node.getFontSize());
        content.setWarp("wrap");
        content.setLineFeed("preserve");
        content.setWhiteSpaceCollapse("false");
        content.setWhiteSpaceTreatment("preserve");

        if (node.getTextColor() != null) {
            content.setColor(node.getTextColor());
        }
        content.setBackgroundColor(node.getBackground());
        if (node.isStyled()) {
            content.setPadding("0.1in", "0.75in");
            content.setMarginLeft("-0.75in");
            content.setMarginRight("-0.75in");
            content.setPadding("0.1in", "0.75in");
        } else if (node.isInline()) {
            content.setPadding("0.1in");
            content.setMarginLeft("0");
            content.setMarginRight("0");
        } else {
            content.setBorderRadius("0.2em");
            content.setBorderLeft(".5pt", "solid", node.getBorderColor());
            content.setBorderRight(".5pt", "solid", node.getBorderColor());
        }
        content.setBorderTop(".5pt", "solid", node.getBorderColor());
        content.setBorderBottom(".5pt", "solid", node.getBorderColor());

        data.push(content);
        node.stream().forEach(c -> c.accept(this, data));
        data.pop();
    }

    private static void createToc(Document doc, FoContext data) {
        String id = Long.toHexString(new Random().nextLong());
        data.addBookmark(id).setTitle("Table of Contents");

        FoBlock content = FoBlock.block(data.getRoot().getBuilder())
                .setBreakBefore("page")
                .setSpaceBefore("0.5em", "1.0em", "2.0em")
                .setSpaceAfter("0.5em", "1.0em", "2.0em")
                .setColor("#000000")
                .setTextAlign("left");

        data.getFlow().addNode(content.setId(id));

        FoBlock block = content.addBlock();
        block.setSpaceBefore("1.0em", "1.5em", "2.0em");
        block.setSpaceAfter("0.5em").setStartIndent("0pt");
        block.setFontWeight("bold").setFontSize("18pt");
        block.addText("Table of Contents");

        doc.stream().forEach(n -> n.accept(new DocumentVisitor<FoBlock>() {

            @Override
            public void visit(Header node, FoBlock data) {
                int intent = node.getLevel() - 1;
                String title = PageUtil.encode(node.getTitle());

                FoBlock content = data.addBlock();
                content.setMarginLeft(String.format("%sem", intent));
                FoDocumentRenderer.createTocEntry(node.getId(), title, content);

                node.forEach(n -> n.accept(this, data));
            }
        }, content));
    }

    /**
     * Creates an entry for the Table of Content.
     *
     * @param id
     * @param title
     * @param block
     */
    private static void createTocEntry(String id, String title, FoBlock block) {
        FoLeader leader = new FoLeader(block.getBuilder())
                .setPattern("dots")
                .setWidth("3pt")
                .setAlign("reference-area")
                .setPaddingLeftRight("3pt");

        FoBlock inline = FoBlock.inline(block.getBuilder());
        inline.setKeepWithNext("always")
                .addText(PageUtil.encode(title))
                .addNode(leader)
                .addNode(new FoPageNumberCitation(id, block.getBuilder()));

        block.setTextAlign("start")
                .setTextAlignLast("justify")
                .addNode(new FoBasicLink(id, block.getBuilder()).addNode(inline));
    }

    /**
     * The {@link BookmarkRenderer} class.
     */
    private class BookmarkRenderer implements DocumentVisitor<FoContext> {

        private final Stack<FoBookmark> bookmarks;

        /**
         * Constructs an instance of {@link BookmarkRenderer}.
         */
        public BookmarkRenderer() {
            this.bookmarks = new Stack<>();
        }

        /**
         * Renders a {@link Header} node.
         *
         * @param node
         * @param data
         */
        @Override
        public final void visit(Header node, FoContext data) {
            while (bookmarks.size() >= node.getLevel())
                bookmarks.pop();

            FoBookmark bookmark = (node.getLevel() > 1)
                    ? bookmarks.peek().addBookmark(node.getId())
                    : data.addBookmark(node.getId());
            bookmark.setTitle(PageUtil.encode(node.getTitle().trim()));
            bookmarks.push(bookmark);
        }
    }
}
