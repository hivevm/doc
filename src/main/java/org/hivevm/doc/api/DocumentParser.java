// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import org.commonmark.node.Document;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.node.*;
import org.hivevm.doc.api.builder.*;
import org.hivevm.doc.api.codeblock.CodeFactory;
import org.hivevm.doc.commonmark.Markdown;
import org.hivevm.doc.commonmark.MarkdownVisitor;
import org.hivevm.doc.commonmark.alerts.AlertBlock;
import org.hivevm.doc.commonmark.images.ImageAttributes;
import org.hivevm.doc.commonmark.markers.Marker;
import org.hivevm.doc.commonmark.tables.Table;
import org.hivevm.doc.commonmark.tables.*;

import java.io.IOException;
import java.util.Set;
import java.util.Stack;

/**
 * The {@link DocumentParser} implements a reader based on MARKDOWN. The Reader supports reading of
 * documents organized in multiple files.
 */
public class DocumentParser extends MarkdownVisitor {

    private final DocumentBuilder document;
    private final CodeFactory     factory;

    private final Stack<FlowBuilder> flows = new Stack<>();

    /**
     * Constructs an instance of {@link DocumentParser}.
     *
     * @param document
     */
    public DocumentParser(DocumentBuilder document) {
        this.document = document;
        this.factory = new CodeFactory();
    }

    /**
     * Visit a {@link org.commonmark.node.Document} node.
     *
     * @param node
     */
    @Override
    public final void visit(Document node) {
        flows.push(document);
        visitChildren(node);
        flows.pop();
    }

    /**
     * Visit a {@link Heading} node.
     *
     * @param node
     */
    @Override
    public final void visit(Heading node) {
        HeaderBuilder builder = this.document.createHeader(node.getLevel());

        flows.push(builder);
        visitChildren(node);
        flows.pop();
    }

    /**
     * Visit a {@link org.commonmark.node.Text} node.
     *
     * @param node
     */
    @Override
    public final void visit(org.commonmark.node.Text node) {
        this.flows.peek().addText(node.getLiteral());
    }

    /**
     * Visit a {@link Code} node.
     *
     * @param node
     */
    @Override
    public final void visit(Code node) {
        this.flows.peek().addCode(node.getLiteral());
    }

    /**
     * Visit a {@link Emphasis} node.
     *
     * @param node
     */
    @Override
    public final void visit(Emphasis node) {
        InlineBuilder builder = new InlineBuilder();
        builder.setItalic();
        this.flows.peek().addInline(builder);

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link StrongEmphasis} node.
     *
     * @param node
     */
    @Override
    public final void visit(StrongEmphasis node) {
        InlineBuilder builder = new InlineBuilder();
        builder.setBold();
        this.flows.peek().addInline(builder);

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link Link} node with optional footnote
     *
     * @param node
     */
    @Override
    public final void visit(Link node) {
        LinkBuilder builder = new LinkBuilder(node.getDestination());
        this.flows.peek().addInline(builder);

        if (node.getTitle() != null)
            this.flows.peek().addFootnote(node.getTitle(), node.getDestination());

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link CustomNode} node handles additional emphasis nodes.
     *
     * @param node
     */
    @Override
    public final void visit(CustomNode node) {
        if (node instanceof Marker) {
            Marker.Decoration decoration = ((Marker) node).getDecoration();
            InlineBuilder builder = new InlineBuilder();
            switch (decoration) {
                case Highlight:
                    builder.setOverline();
                    builder.setUnderline();
                    break;

                case Overline:
                    builder.setOverline();
                    break;

                case Underline:
                    builder.setUnderline();
                    break;

                default:
                    builder.setStrikethrough();
                    break;
            }
            this.flows.peek().addInline(builder);

            this.flows.push(builder);
            visitChildren(node);
            this.flows.pop();
        } else {
            super.visit(node);
        }
    }

    /**
     * Visit a {@link LinkReferenceDefinition} node.
     *
     * @param node
     */
    @Override
    public final void visit(LinkReferenceDefinition node) {
        System.out.printf("LinkReferenceDefinition %s(%s) => %s\n", node.getLabel(), node.getTitle(), node.getLabel());
    }


    /**
     * Process a {@link org.commonmark.node.Paragraph} node.
     *
     * @param node
     */
    @Override
    public final void visit(Paragraph node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();

        ParagraphBuilder builder = container.addParagraph();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link SoftLineBreak} node.
     *
     * @param node
     */
    @Override
    public final void visit(SoftLineBreak node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();

        ParagraphBuilder builder = container.addParagraph();
        builder.setSoftBreak();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link HardLineBreak} node.
     *
     * @param node
     */
    @Override
    public final void visit(HardLineBreak node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();

        ParagraphBuilder builder = container.addParagraph();
        builder.setLineBreak();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link ThematicBreak} node.
     *
     * @param node
     */
    @Override
    public final void visit(ThematicBreak node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();

        ParagraphBuilder builder = container.addParagraph();
        builder.setLineBreak();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link Link} node.
     *
     * @param node
     */
    @Override
    public final void visit(Image node) {
        String title = null;
        String align = null;
        String width = null;
        String height = null;

        if (node.getFirstChild() instanceof Text text) {
            title = text.getLiteral();
        }
        if (node.getLastChild() instanceof ImageAttributes attrs) {
            align = attrs.getAttributes().get("align");
            width = attrs.getAttributes().get("width");
            height = attrs.getAttributes().get("height");
        }

        SectionBuilder container = (SectionBuilder) this.flows.peek();
        container.addImage(node.getDestination(), title, align, width, height);
    }

    /**
     * Visit a {@link IndentedCodeBlock} node.
     *
     * @param node
     */
    @Override
    public final void visit(IndentedCodeBlock node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();
        this.factory.generate("indented", node.getLiteral(), container);
    }

    /**
     * Visit a {@link BlockQuote} node.
     *
     * @param node
     */
    @Override
    public final void visit(BlockQuote node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();

        ParagraphBuilder builder = container.addParagraph();
        builder.setBlock();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link BulletList} node.
     *
     * @param node
     */
    @Override
    public final void visit(BulletList node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();
        ListBuilder builder = container.addList();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link OrderedList} node.
     *
     * @param node
     */
    @Override
    public final void visit(OrderedList node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();
        ListBuilder builder = container.addOrderedList();

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link ListItem} node.
     *
     * @param node
     */
    @Override
    public final void visit(ListItem node) {
        ListBuilder list = (ListBuilder) this.flows.peek();
        ParagraphBuilder builder = list.addParagraph(); // TODO List Item

        this.flows.push(builder);
        visitChildren(node);
        this.flows.pop();
    }

    /**
     * Visit a {@link FencedCodeBlock} node.
     *
     * @param node
     */
    @Override
    public final void visit(FencedCodeBlock node) {
        SectionBuilder container = (SectionBuilder) this.flows.peek();
        this.factory.generate(node.getInfo(), node.getLiteral(), container);
    }

    /**
     * Visit a {@link FencedCodeBlock} node.
     *
     * @param node
     */
    @Override
    public final void visit(CustomBlock node) {
        if (node instanceof Table) {
            PageBuilder container = (PageBuilder) this.flows.peek();
            TableBuilder builder = container.addTable();

            MarkdownVisitor visitor = new TableVisitor(builder);
            visitor.visitChildren(node);
        } else if (node instanceof AlertBlock alert) {
            Message.Style style = switch (alert.getType()) {
                case SUCCESS -> Message.Style.SUCCESS;
                case WARNING -> Message.Style.WARNING;
                case ERROR -> Message.Style.ERROR;
                case NOTE -> Message.Style.INFO;
            };

            PageBuilder container = (PageBuilder) this.flows.peek();
            ContentBuilder builder = container.addNotification(style);

            this.flows.push(builder);
            visitChildren(node);
            this.flows.pop();
        } else {
            super.visit(node);
        }
    }

    /**
     * Visit a {@link HtmlInline} node.
     *
     * @param node
     */
    @Override
    public final void visit(HtmlInline node) {
        this.flows.peek().addText(node.getLiteral());
    }

    /**
     * Visit a {@link HtmlBlock} node.
     *
     * @param node
     */
    @Override
    public final void visit(HtmlBlock node) {
        super.visit(node);
    }

    /**
     *
     */
    private class TableVisitor extends MarkdownVisitor {

        private final TableBuilder table;

        /**
         * Constructs an instance of {@link TableVisitor}.
         *
         * @param table
         */
        public TableVisitor(TableBuilder table) {
            this.table = table;
        }

        /**
         * Processes the {@link CustomNode} of a table
         *
         * @param node
         */
        @Override
        public final void visit(CustomNode node) {
            if (node instanceof TableHead) {
                table.addHead();
                visitChildren(node);
            } else if (node instanceof TableBody) {
                table.addBody();
                visitChildren(node);
            } else if (node instanceof TableRow) {
                table.addRow();
                visitChildren(node);
            } else if (node instanceof TableCell cell) {
                if (node.getParent().getParent() instanceof TableHead)
                    table.addColumn(cell.getWidth(), cell.getAlignment().name().toLowerCase());

                TableBuilder.CellBuilder builder = table.addCell(1, 1);

                DocumentParser.this.flows.push(builder.getContent());
                DocumentParser.this.visitChildren(node);
                DocumentParser.this.flows.pop();
            } else {
                super.visitChildren(node);
            }
        }
    }

    /**
     * Parses a Document from the text.
     *
     * @param text
     * @param keywords
     */
    public static org.hivevm.doc.api.Document parse(String text, Set<String> keywords) throws IOException {
        DocumentBuilder builder = new DocumentBuilder(keywords);
        Node node = Markdown.newInstance().parse(text);
        node.accept(new DocumentParser(builder));
        return builder.build();
    }
}
