// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop;

import java.util.Properties;
import java.util.Random;

import org.hivevm.doc.Book;
import org.hivevm.doc.Chapter;
import org.hivevm.doc.NodeVisitor;
import org.hivevm.doc.Renderer;
import org.hivevm.doc.fop.config.FoContext;
import org.hivevm.doc.fop.nodes.FoBasicLink;
import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.fop.nodes.FoFlow;
import org.hivevm.doc.fop.nodes.FoLeader;
import org.hivevm.doc.fop.nodes.FoPageNumberCitation;
import org.hivevm.doc.fop.nodes.FoRoot;

/**
 * The {@link FoRendererToC} class.
 */
class FoRendererToC implements Renderer, NodeVisitor<FoBlock> {

  private final FoContext data;
  private final String    title;

  /**
   * Constructs an instance of {@link FoRenderer}.
   *
   * @param root
   * @param title
   */
  public FoRendererToC(FoContext data, String title) {
    this.data = data;
    this.title = title;
  }

  /**
   * Get the {@link FoRoot}.
   */
  protected final FoContext getData() {
    return this.data;
  }

  /**
   * Renders the {@link Book}.
   *
   * @param node
   */
  @Override
  public final void render(Book node) {
    Properties properties = new Properties();
    properties.put("TITLE", "Table of Content");

    FoFlow flow = getData().createFlow(this.title, Fo.PAGESET_STANDARD, true, properties);
    flow.setStartIndent("0pt").setEndIndent("0pt");
    flow.addBlock().setBreakAfter("page");

    String id = Long.toHexString(new Random().nextLong());
    getData().addBookmark(id).setTitle(this.title);

    FoBlock content = FoBlock.block();
    content.setId(id);
    content.setSpaceBefore("0.5em", "1.0em", "2.0em");
    content.setSpaceAfter("0.5em", "1.0em", "2.0em");
    content.setColor("#000000").setTextAlign("left");
    flow.addNode(content);

    FoBlock block = content.addBlock();
    block.setSpaceBefore("1.0em", "1.5em", "2.0em");
    block.setSpaceAfter("0.5em").setStartIndent("0pt");
    block.setFontWeight("bold").setFontSize("18pt");
    block.addText("Table of Contents");

    node.nodes().forEach(n -> n.accept(this, content));
  }

  @Override
  public final void visit(Chapter node, FoBlock data) {
    int intent = node.getLevel() - 1;
    String title = PageUtil.encode(node.getTitle());

    FoBlock content = data.addBlock();
    content.setMarginLeft(String.format("%sem", intent));
    FoRendererToC.createEntry(node.getId(), content).addText(title);

    node.forEach(n -> n.accept(this, data));
  }

  /**
   * Creates an entry for the Table of Content- {@link #createEntry}.
   *
   * @param id
   * @param container
   */
  public static FoBasicLink createEntry(String id, FoBlock container) {
    FoBlock block = FoBlock.block();
    block.setTextAlign("start").setTextAlignLast("justify");
    block.setEndIndent("1em").setEndIndentLastLine("-1em");
    container.addNode(block);

    FoBasicLink link = new FoBasicLink(id);
    block.addInline().setKeepWithNext("always").addNode(link);

    FoBlock inline = block.addInline().setKeepWithNext("always");
    FoLeader leader = new FoLeader();
    leader.setPaddingLeftRight("3pt");
    leader.setPattern("dots").setWidth("3pt").setAlign("reference-area");
    inline.addNode(leader);

    FoBasicLink link2 = new FoBasicLink(id);
    link2.addNode(new FoPageNumberCitation(id));
    inline.addNode(link2);
    return link;
  }
}
