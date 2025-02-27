// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.markdown;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import org.hivevm.doc.Message.Style;
import org.hivevm.doc.builder.ContentBuilder;
import org.hivevm.doc.builder.ListBuilder;
import org.hivevm.doc.builder.PageBuilder;
import org.hivevm.doc.builder.ParagraphBuilder;
import org.hivevm.doc.codeblock.CodeFactory;
import org.hivevm.doc.commonmark.MarkdownVisitor;
import org.hivevm.doc.commonmark.alerts.AlertBlock;
import org.hivevm.doc.commonmark.images.ImageAttributes;
import org.hivevm.doc.commonmark.tables.Table;

/**
 * The {@link MarkdownPage} class.
 */
class MarkdownPage extends MarkdownVisitor {

  private final PageBuilder content;
  private final CodeFactory factory;

  /**
   * Constructs an instance of {@link MarkdownPage}.
   *
   * @param content
   * @param factory
   */
  public MarkdownPage(PageBuilder content, CodeFactory factory) {
    this.content = content;
    this.factory = factory;
  }

  /**
   * Get the current {@link ContentBuilder} node.
   */
  protected PageBuilder getContent() {
    return this.content;
  }

  /**
   * Visit the {@link SoftLineBreak}.
   *
   * @param node
   */
  @Override
  public final void visit(SoftLineBreak node) {
    getContent().addBreak();
  }

  /**
   * Visit the {@link ThematicBreak}.
   *
   * @param node
   */
  @Override
  public void visit(ThematicBreak node) {
    getContent().addLineBreak();
  }

  /**
   * Visit the {@link Paragraph}.
   *
   * @param node
   */
  @Override
  public final void visit(Paragraph node) {
    ContentBuilder content = getContent().addParagraph();
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, 0);
    builder.visitChildren(node);
  }

  /**
   * Process the {@link BlockQuote}.
   *
   * @param node
   */
  @Override
  public final void visit(BlockQuote node) {
    ParagraphBuilder content = getContent().addParagraph();
    content.setIntent(1);
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, 0);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link Image}.
   *
   * @param node
   */
  @Override
  public final void visit(Image node) {
    String title = null;
    String align = null;
    String width = null;
    String height = null;

    if (node.getFirstChild() instanceof Text) {
      Text text = (Text) node.getFirstChild();
      title = text.getLiteral();
    }
    if (node.getLastChild() instanceof ImageAttributes) {
      ImageAttributes attrs = (ImageAttributes) node.getLastChild();
      align = attrs.getAttributes().get("align");
      width = attrs.getAttributes().get("width");
      height = attrs.getAttributes().get("height");
    }
    getContent().addImage(node.getDestination(), title, align, width, height);
  }

  /**
   * Visit the {@link LinkReferenceDefinition}.
   *
   * @param node
   */
  @Override
  public final void visit(LinkReferenceDefinition node) {
    visitChildren(node);
  }

  /**
   * Visit the {@link FencedCodeBlock}.
   *
   * @param node
   */
  @Override
  public final void visit(FencedCodeBlock node) {
    this.factory.generate(node.getInfo(), node.getLiteral(), getContent());
  }

  /**
   * Process the {@link BulletList}.
   *
   * @param node
   */
  @Override
  public final void visit(BulletList node) {
    ListBuilder list = getContent().addList();
    MarkdownBuilder builder = new MarkdownBuilder(list, this.factory, 0);
    builder.visitChildren(node);
  }

  /**
   * Process the {@link OrderedList}.
   *
   * @param node
   */
  @Override
  public final void visit(OrderedList node) {
    ListBuilder list = getContent().addOrderedList();
    MarkdownBuilder builder = new MarkdownBuilder(list, this.factory, 0);
    builder.visitChildren(node);
  }

  /**
   * Process the {@link IndentedCodeBlock}.
   *
   * @param node
   */
  @Override
  public final void visit(IndentedCodeBlock node) {
    ContentBuilder code = getContent().addCode();
    code.addContent(node.getLiteral());
  }

  /**
   * Process the {@link CustomBlock}.
   *
   * @param node
   */
  @Override
  public final void visit(CustomBlock node) {
    if (node instanceof Table) {
      PageBuilder page = getContent();
      MarkdownTable builder = new MarkdownTable(page.addTable(), this.factory);
      builder.visitChildren(node);
    } else if (node instanceof AlertBlock) {
      PageBuilder page = getContent();
      ContentBuilder content = page.addNotification(MarkdownPage.getNotification(node));

      MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, 0);
      builder.visitChildren(node);
    } else {
      super.visit(node);
    }
  }

  /**
   * Get the {@link AlertBlock}.
   *
   * @param block
   */
  protected static Style getNotification(CustomBlock node) {
    AlertBlock block = (AlertBlock) node;
    switch (block.getType()) {
      case SUCCESS:
        return Style.SUCCESS;
      case WARNING:
        return Style.WARNING;
      case ERROR:
        return Style.ERROR;
      case NOTE:
      default:
        return Style.INFO;
    }
  }
}
