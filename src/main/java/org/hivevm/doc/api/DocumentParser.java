// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.commonmark.ext.task.list.items.TaskListItemMarker;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.hivevm.commonmark.Markdown;
import org.hivevm.commonmark.MarkdownVisitor;
import org.hivevm.commonmark.alert.AlertBlock;
import org.hivevm.commonmark.image.ImageAttributes;
import org.hivevm.commonmark.marker.Marker;
import org.hivevm.commonmark.table.Table;
import org.hivevm.doc.api.builder.BlockBuilder;
import org.hivevm.doc.api.builder.ContainerBuilder;
import org.hivevm.doc.api.builder.DocumentBuilder;
import org.hivevm.doc.api.builder.ListBuilder;
import org.hivevm.doc.api.builder.ListItemBuilder;
import org.hivevm.doc.api.builder.NodeBuilder;
import org.hivevm.doc.api.builder.ParagraphBuilder;
import org.hivevm.doc.api.builder.TableBuilder;
import org.hivevm.doc.api.codeblock.CodeFactory;

/**
 * The {@link DocumentParser} implements a reader based on MARKDOWN. The Reader supports reading of
 * documents organized in multiple files.
 */
public class DocumentParser extends MarkdownVisitor {

    private final DocumentBuilder builder;
    private final CodeFactory     factory;

    private final Stack<NodeBuilder> nodes = new Stack<>();

    /**
     * Constructs an instance of {@link DocumentParser}.
     */
    public DocumentParser(DocumentBuilder document) {
        this.builder = document;
        this.factory = new CodeFactory();
    }

    /**
     * Gets the builder for a text flow.
     */
    protected final NodeBuilder node() {
        return this.nodes.peek();
    }

    /**
     * Add a text flow builder and processes the children of the node
     */
    protected final void processNode(Node node, NodeBuilder builder) {
        this.nodes.push(builder);
        visitChildren(node);
        this.nodes.pop();
    }

    /**
     * Gets the builder for a text flow.
     */
    protected final FlowBuilder flowBuilder() {
        try {
            return ((FlowBuilder.FlowContainer) node()).getFlowBuilder();
        } catch (ClassCastException e) {
            System.out.println();
        }
        return null;
    }

    /**
     * Visit a {@link Document} node.
     */
    @Override
    public final void visit(Document node) {
        processNode(node, builder);
    }

    /**
     * Visit a {@link Heading} node.
     */
    @Override
    public final void visit(Heading node) {
        DocumentBuilder builder = (DocumentBuilder) node();
        processNode(node, builder.addHeader(node.getLevel()));
    }

    /**
     * Visit a {@link Text} node.
     */
    @Override
    public final void visit(Text node) {
        flowBuilder().addText(node.getLiteral());
    }

    /**
     * Visit a {@link Emphasis} node.
     */
    @Override
    public final void visit(Emphasis node) {
        flowBuilder().addStyle(Flow.Kind.ITALIC);
        visitChildren(node);
        flowBuilder().restore();
    }

    /**
     * Visit a {@link StrongEmphasis} node.
     */
    @Override
    public final void visit(StrongEmphasis node) {
        flowBuilder().addStyle(Flow.Kind.BOLD);
        visitChildren(node);
        flowBuilder().restore();
    }

    /**
     * Visit a {@link Code} node.
     */
    @Override
    public final void visit(Code node) {
        flowBuilder().addStyle(Flow.Kind.CODE);
        flowBuilder().addText(node.getLiteral());
        visitChildren(node);
        flowBuilder().restore();
    }

    /**
     * Visit a {@link Link} node with optional footnote
     */
    @Override
    public final void visit(Link node) {
        flowBuilder().addLink(node.getDestination(), node.getTitle());
        visitChildren(node);
        flowBuilder().restore();
    }

    /**
     * Visit a {@link Image} node.
     */
    @Override
    public final void visit(Image node) {
        String title = null;
        String align = null;
        String width = null;
        String height = null;

        if (node.getFirstChild() instanceof Text text)
            title = text.getLiteral();
        if (node.getLastChild() instanceof ImageAttributes attrs) {
            align = attrs.getAttributes().get("align");
            width = attrs.getAttributes().get("width");
            height = attrs.getAttributes().get("height");
        }
        flowBuilder().addImage(node.getDestination(), title, align, width, height);
    }

    /**
     * Visit a {@link CustomNode} node handles additional emphasis nodes.
     */
    @Override
    public final void visit(CustomNode node) {
        if (node instanceof Marker marker) {
            switch (marker.getDecoration()) {
                case Overline -> flowBuilder().addStyle(Flow.Kind.OVERLINE);
                case Underline -> flowBuilder().addStyle(Flow.Kind.UNDERLINE);
                case Highlight -> flowBuilder().addStyle(Flow.Kind.HIGHLIGHT);
                case Strikethrough -> flowBuilder().addStyle(Flow.Kind.STRIKETHROUGH);
            }
            visitChildren(node);
            flowBuilder().restore();
        }
        else if (node instanceof TaskListItemMarker task) {
            ((ListItemBuilder) node()).setState(
                task.isChecked() ? ListItemBuilder.State.CHECKED : ListItemBuilder.State.UNCHECKED);
        }
        else {
            super.visit(node);
        }
    }

    /**
     * Visit a {@link LinkReferenceDefinition} node.
     */
    @Override
    public final void visit(LinkReferenceDefinition node) {
        System.out.printf("LinkReferenceDefinition %s(%s) => %s\n", node.getLabel(),
            node.getTitle(), node.getLabel());
    }

    /**
     * Process a {@link Paragraph} node.
     */
    @Override
    public final void visit(Paragraph node) {
        NodeBuilder container = node();
        ParagraphBuilder builder;
        if (container instanceof BlockBuilder)
            builder = ((BlockBuilder) container).addParagraph();
        else if (container instanceof ListItemBuilder)
            builder = ((ListItemBuilder) container).addParagraph();
        else
            builder = ((ContainerBuilder) container).addParagraph();

        processNode(node, builder);
    }

    /**
     * Visit a {@link BlockQuote} node.
     */
    @Override
    public final void visit(BlockQuote node) {
        NodeBuilder container = node();
        if (container instanceof BlockBuilder)
            processNode(node, ((BlockBuilder) container).addBlock());
        else
            processNode(node, ((ContainerBuilder) container).addBlock());
    }

    /**
     * Visit a {@link SoftLineBreak} node.
     */
    @Override
    public final void visit(SoftLineBreak node) {
        flowBuilder().addText(" ");
        visitChildren(node);
    }

    /**
     * Visit a {@link HardLineBreak} node.
     */
    @Override
    public final void visit(HardLineBreak node) {
        nodes.pop();
        if (nodes.peek() instanceof ListItemBuilder)
            nodes.push(((ListItemBuilder) nodes.peek()).addParagraph());
        else
            nodes.push(((ContainerBuilder) nodes.peek()).addParagraph());
    }

    /**
     * Visit a {@link ThematicBreak} node.
     */
    @Override
    public final void visit(ThematicBreak node) {
        DocumentBuilder container = (DocumentBuilder) node();
        container.addBreak();
    }

    /**
     * Visit a {@link IndentedCodeBlock} node.
     */
    @Override
    public final void visit(IndentedCodeBlock node) {
        DocumentBuilder container = (DocumentBuilder) node();
        this.factory.generate("indented", node.getLiteral(), container);
    }

    /**
     * Visit a {@link BulletList} node.
     */
    @Override
    public final void visit(BulletList node) {
        if (node() instanceof ListItemBuilder container)
            processNode(node, container.addList(false));
        else {
            ContainerBuilder container = (ContainerBuilder) node();
            processNode(node, container.addList(false));
        }
    }

    /**
     * Visit a {@link OrderedList} node.
     */
    @Override
    public final void visit(OrderedList node) {
        if (node() instanceof ListItemBuilder container)
            processNode(node, container.addList(true));
        else {
            ContainerBuilder container = (ContainerBuilder) node();
            processNode(node, container.addList(true));
        }
    }

    /**
     * Visit a {@link ListItem} node.
     */
    @Override
    public final void visit(ListItem node) {
        ListBuilder list = (ListBuilder) node();
        processNode(node, list.addItem());
    }

    /**
     * Visit a {@link FencedCodeBlock} node.
     */
    @Override
    public final void visit(FencedCodeBlock node) {
        ContainerBuilder container = (ContainerBuilder) node();
        this.factory.generate(node.getInfo(), node.getLiteral(), container);
    }

    /**
     * Visit a {@link FencedCodeBlock} node.
     */
    @Override
    public final void visit(CustomBlock node) {
        if (node instanceof Table) {
            DocumentBuilder container = (DocumentBuilder) node();
            TableBuilder builder = container.addTable();

            MarkdownVisitor visitor = new TableVisitor(builder);
            visitor.visitChildren(node);
        }
        else if (node instanceof AlertBlock alert) {
            Block.Kind style = switch (alert.getType()) {
                case SUCCESS -> Block.Kind.SUCCESS;
                case WARNING -> Block.Kind.WARNING;
                case ERROR -> Block.Kind.ERROR;
                case NOTE -> Block.Kind.INFO;
            };

            DocumentBuilder container = (DocumentBuilder) node();
            processNode(node, container.addBlockMessage(style));
        }
        else {
            super.visit(node);
        }
    }

    /**
     * Visit a {@link HtmlInline} node.
     */
    @Override
    public final void visit(HtmlInline node) {
        flowBuilder().addText(node.getLiteral());
    }

    /**
     * Visit a {@link HtmlBlock} node.
     */
    @Override
    public final void visit(HtmlBlock node) {
        super.visit(node);
    }

    /**
     * Processes a table.
     */
    private class TableVisitor extends MarkdownVisitor {

        private final TableBuilder table;

        /**
         * Constructs an instance of {@link TableVisitor}.
         */
        public TableVisitor(TableBuilder table) {
            this.table = table;
        }

        /**
         * Processes the {@link CustomNode} of a table
         */
        @Override
        public final void visit(CustomNode node) {
            if (node instanceof Table.Head) {
                table.addHead();
                visitChildren(node);
            }
            else if (node instanceof Table.Body) {
                table.addBody();
                visitChildren(node);
            }
            else if (node instanceof Table.Row) {
                table.addRow();
                visitChildren(node);
            }
            else if (node instanceof Table.Cell cell) {
                if (node.getParent().getParent() instanceof Table.Head)
                    table.addColumn(cell.getWidth(), cell.getAlignment().name().toLowerCase());

                TableBuilder.CellBuilder builder = table.addCell(1, 1);
                processNode(node, builder.getContent());
            }
            else {
                super.visitChildren(node);
            }
        }
    }

    /**
     * Parses a Document from the text.
     */
    public static org.hivevm.doc.api.Document parse(String text) throws IOException {
        var builder = new DocumentBuilder();
        var node = Markdown.parse(text);
        node.accept(new DocumentParser(builder));
        return builder.build();
    }

    /**
     * Parses a Document from the text.
     */
    public static org.hivevm.doc.api.Document parse(InputStream stream) throws IOException {
        var text = new String(stream.readAllBytes());
        return DocumentParser.parse(text);
    }
}
