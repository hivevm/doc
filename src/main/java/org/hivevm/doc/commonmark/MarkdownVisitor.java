// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark;

import org.commonmark.node.AbstractVisitor;
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
import org.hivevm.doc.commonmark.images.ImageAttributes;
import org.hivevm.doc.commonmark.tables.TableRow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link MarkdownVisitor} class.
 */
public class MarkdownVisitor extends AbstractVisitor {

  private final boolean isRequired;

  /**
   * Constructs an instance of {@link MarkdownVisitor}.
   */
  protected MarkdownVisitor() {
    this.isRequired = true;
  }

  /**
   * Constructs an instance of {@link MarkdownVisitor}.
   */
  protected MarkdownVisitor(boolean isRequired) {
    this.isRequired = isRequired;
  }

  /**
   * Visit the named {@link Node}.
   *
   * @param node
   * @param name
   */
  protected final void visit(Node node, String name) {
    if (this.isRequired) {
      throw new UnsupportedOperationException(name);
    }

    visitChildren(node);
  }

  /**
   * Visit the {@link Document}.
   *
   * @param node
   */
  @Override
  public void visit(Document node) {
    visit(node, "Document");
  }

  /**
   * Visit the {@link Heading}.
   *
   * @param node
   */
  @Override
  public void visit(Heading node) {
    visit(node, "Heading");
  }

  /**
   * Visit the {@link Paragraph}.
   *
   * @param node
   */
  @Override
  public void visit(Paragraph node) {
    visit(node, "Paragraph");
  }

  /**
   * Visit the {@link SoftLineBreak}.
   *
   * @param node
   */
  @Override
  public void visit(SoftLineBreak node) {
    visit(node, "SoftLineBreak");
  }

  /**
   * Visit the {@link HardLineBreak}.
   *
   * @param node
   */
  @Override
  public void visit(HardLineBreak node) {
    visit(node, "HardLineBreak");
  }

  /**
   * Visit the {@link ThematicBreak}.
   *
   * @param node
   */
  @Override
  public void visit(ThematicBreak node) {
    visit(node, "ThematicBreak");
  }

  /**
   * Visit the {@link BlockQuote}.
   *
   * @param node
   */
  @Override
  public void visit(BlockQuote node) {
    visit(node, "BlockQuote");
  }

  /**
   * Visit the {@link Text}.
   *
   * @param node
   */
  @Override
  public void visit(Text node) {
    visit(node, "Text");
  }

  /**
   * Visit the {@link Emphasis}.
   *
   * @param node
   */
  @Override
  public void visit(Emphasis node) {
    visit(node, "Emphasis");
  }

  /**
   * Visit the {@link StrongEmphasis}.
   *
   * @param node
   */
  @Override
  public void visit(StrongEmphasis node) {
    visit(node, "StrongEmphasis");
  }

  /**
   * Visit the {@link Link}.
   *
   * @param node
   */
  @Override
  public void visit(Link node) {
    visit(node, "Link");
  }

  /**
   * Visit the {@link LinkReferenceDefinition}.
   *
   * @param node
   */
  @Override
  public void visit(LinkReferenceDefinition node) {
    visit(node, "LinkReferenceDefinition");
  }

  /**
   * Visit the {@link Image}.
   *
   * @param node
   */
  @Override
  public void visit(Image node) {
    visit(node, "Image");
  }

  /**
   * Visit the {@link BulletList}.
   *
   * @param node
   */
  @Override
  public void visit(BulletList node) {
    visit(node, "BulletList");
  }

  /**
   * Visit the {@link OrderedList}.
   *
   * @param node
   */
  @Override
  public void visit(OrderedList node) {
    visit(node, "OrderedList");
  }

  /**
   * Visit the {@link ListItem}.
   *
   * @param node
   */
  @Override
  public void visit(ListItem node) {
    visit(node, "ListItem");
  }

  /**
   * Visit the {@link Code}.
   *
   * @param node
   */
  @Override
  public void visit(Code node) {
    visit(node, "Code");
  }

  /**
   * Visit the {@link IndentedCodeBlock}.
   *
   * @param node
   */
  @Override
  public void visit(IndentedCodeBlock node) {
    visit(node, "IndentedCodeBlock");
  }

  /**
   * Visit the {@link FencedCodeBlock}.
   *
   * @param node
   */
  @Override
  public void visit(FencedCodeBlock node) {
    visit(node, "FencedCodeBlock");
  }

  /**
   * Visit the {@link HtmlBlock}.
   *
   * @param node
   */
  @Override
  public void visit(HtmlBlock node) {
    String literal = node.getLiteral().trim();
    if(literal.startsWith("<img") && literal.endsWith(">")) {
      Matcher matcher = Pattern.compile("(\\w+)=\"([^\"]+)\"").matcher(literal);
      Image image = new Image();
      Text text = new Text();
      Map<String, String> attrs = new HashMap<>();
      while (matcher.find()) {
        switch (matcher.group(1)) {
          case "src":
          image.setDestination(matcher.group(2));
            break;
          case "width":
            attrs.put("width", matcher.group(2));
            break;
          case "align":
            attrs.put("align", matcher.group(2));
            break;
          case "alt":
            text.setLiteral(matcher.group(2));
            break;
          default:
        }
      }
      if (text.getLiteral() != null)
        image.appendChild(text);
      if (!attrs.isEmpty())
        image.appendChild(new ImageAttributes(attrs));
      visit(image);
    } else
      visit(node, "HtmlBlock");
  }

  /**
   * Visit the {@link HtmlInline}.
   *
   * @param node
   */
  @Override
  public void visit(HtmlInline node) {
    visit(node, "HtmlInline");
  }

  /**
   * Visit the {@link CustomBlock}.
   *
   * @param node
   */
  @Override
  public void visit(CustomBlock node) {
    visit(node, "CustomBlock");
  }

  /**
   * Visit the {@link CustomNode}.
   *
   * @param node
   */
  @Override
  public void visit(CustomNode node) {
    visit(node, "CustomNode");
  }

  /**
   * Visit the child nodes.
   *
   * @param node
   */
  @Override
  public final void visitChildren(Node node) {
    super.visitChildren(node);
  }
}
