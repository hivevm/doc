// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.markdown;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.hivevm.doc.commonmark.MarkdownVisitor;
import org.hivevm.doc.commonmark.images.ImageAttributes;
import org.hivevm.doc.commonmark.markers.Marker;
import org.hivevm.doc.tree.builder.ContentBuilder;
import org.hivevm.doc.tree.builder.InlineBuilder;
import org.hivevm.doc.tree.builder.ListBuilder;
import org.hivevm.doc.tree.builder.ParagraphBuilder;
import org.hivevm.doc.tree.codeblock.CodeFactory;

/**
 * The {@link MarkdownBuilder} class.
 */
class MarkdownBuilder extends MarkdownVisitor {

  private final int            intent;
  private final ContentBuilder content;
  private final CodeFactory    factory;

  /**
   * Constructs an instance of {@link MarkdownBuilder}.
   *
   * @param content
   * @param intent
   */
  public MarkdownBuilder(ContentBuilder content, CodeFactory factory, int intent) {
    super(false);
    this.content = content;
    this.factory = factory;
    this.intent = intent;
  }

  /**
   * Get the current {@link ContentBuilder} node.
   */
  protected ContentBuilder getContent() {
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
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
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
    content.setIntent(this.intent + 1);
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent + 1);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link Text}.
   *
   * @param node
   */
  @Override
  public final void visit(Text node) {
    getContent().addContent(node.getLiteral());
  }

  /**
   * Visit the {@link Emphasis}.
   *
   * @param node
   */
  @Override
  public final void visit(Emphasis node) {
    InlineBuilder content = getContent().addInline();
    content.setItalic();
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link StrongEmphasis}.
   *
   * @param node
   */
  @Override
  public final void visit(StrongEmphasis node) {
    InlineBuilder content = getContent().addInline();
    content.setBold();
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link Link}.
   *
   * @param node
   */
  @Override
  public final void visit(Link node) {
    if (node.getFirstChild() != null) {
      ContentBuilder content = getContent().addLink(node.getDestination());
      MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
      builder.visitChildren(node);
    }

    if (node.getTitle() != null) {
      InlineBuilder content = getContent().addFootnote();
      content.addLink(node.getDestination()).addContent(node.getTitle());
    }
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
   * Process the {@link BulletList}.
   *
   * @param node
   */
  @Override
  public final void visit(BulletList node) {
    ListBuilder list = getContent().addList();
    MarkdownBuilder builder = new MarkdownBuilder(list, this.factory, this.intent);
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
    MarkdownBuilder builder = new MarkdownBuilder(list, this.factory, this.intent);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link ListItem}.
   *
   * @param node
   */
  @Override
  public final void visit(ListItem node) {
    if (getContent() == null) {
      throw new IllegalArgumentException();
    }

    ContentBuilder content = getContent();// ((ListBuilder) getContent()).newItem();
    MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
    builder.visitChildren(node);
  }

  /**
   * Visit the {@link Code}.
   *
   * @param node
   */
  @Override
  public final void visit(Code node) {
    getContent().addInlineCode(node.getLiteral());
  }

  /**
   * Visit the {@link IndentedCodeBlock}.
   *
   * @param node
   */
  @Override
  public final void visit(IndentedCodeBlock node) {
    ContentBuilder code = getContent().addCode();
    code.addContent(node.getLiteral());
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
   * Visit the {@link HtmlInline}.
   *
   * @param node
   */
  @Override
  public final void visit(HtmlInline node) {
    getContent().addContent(node.getLiteral());
  }

  /**
   * Visit the {@link CustomNode}.
   *
   * @param node
   */
  @Override
  public final void visit(CustomNode node) {
    if (node instanceof Marker) {
      InlineBuilder content = getContent().addInline();
      switch (((Marker) node).getDecoration()) {
        case Highlight:
          content.setOverline();
          content.setUnderline();
          break;
        case Overline:
          content.setOverline();
          break;
        case Underline:
          content.setUnderline();
          break;
        default:
          content.setStrikethrough();
          break;
      }

      MarkdownBuilder builder = new MarkdownBuilder(content, this.factory, this.intent);
      builder.visitChildren(node);
    } else {
      super.visit(node);
    }
  }
}
