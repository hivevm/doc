// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.function.BiFunction;

import org.hivevm.doc.fop.Fo;
import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.fop.nodes.FoBookmark;
import org.hivevm.doc.fop.nodes.FoFlow;
import org.hivevm.doc.fop.nodes.FoLeader;
import org.hivevm.doc.fop.nodes.FoNode;
import org.hivevm.doc.fop.nodes.FoPageSequence;
import org.hivevm.doc.fop.nodes.FoRoot;
import org.hivevm.doc.fop.nodes.FoStaticContent;
import org.hivevm.doc.template.FluidSymbols;
import org.hivevm.util.DataUri;
import org.hivevm.util.StAX;

/**
 * The {@link FoContext} class.
 */
public class FoContext {

  private final FoRoot                      root;
  private final FluidSymbols                symbols;
  private final Map<String, UIPageSequence> pageSet;
  private final Stack<FoNode>               nodes = new Stack<>();


  /**
   * Constructs an instance of {@link FoContext}.
   *
   * @param root
   * @param symbols
   * @param pageSet
   */
  public FoContext(FoRoot root, FluidSymbols symbols, Map<String, UIPageSequence> pageSet) {
    this.root = root;
    this.symbols = symbols;
    this.pageSet = pageSet;
  }

  /**
   * Get the top {@link FoNode}.
   */
  public final FoNode top() {
    return this.nodes.peek();
  }

  /**
   * Pop the top {@link FoNode}.
   */
  public final FoNode pop() {
    return this.nodes.pop();
  }

  /**
   * Push the a {@link FoNode}.
   *
   * @param node
   */
  public final FoNode push(FoNode node) {
    return this.nodes.push(node);
  }

  /**
   * Add a bookmark by id.
   *
   * @param id
   */
  public final FoBookmark addBookmark(String id) {
    return this.root.addBookmark(id);
  }

  /**
   * Set the supplier that creates a {@link FoFlow}.
   *
   * @param id
   * @param name
   */
  public final FoFlow createFlow(String id, String name, boolean initial, Properties properties) {
    FoPageSequence page = this.root.addPageSequence(name);
    page.setLanguage("en").setInitialPageNumber(initial ? "1" : "auto");
    page.setId(id);

    switch (name) {
      case Fo.PAGESET_CHAPTER:
        page.setFormat("1");
        break;

      default:
        page.setFormat("I");
    }

    // Foot separator
    FoStaticContent content = new FoStaticContent("xsl-footnote-separator");
    FoBlock block = content.addBlock().setTextAlignLast("justify").setPadding("0.5em");
    page.addNode(content);

    FoLeader leader = new FoLeader();
    leader.setPattern("rule").setLength("50%");
    leader.setRuleThickness("0.5pt").setColor("#777777");
    block.addNode(leader);

    if (this.pageSet.containsKey(name)) {
      this.pageSet.get(name).render(page, properties);
    }

    FoFlow flow = page.flow(Fo.PAGE_CONTENT);
    flow.setStartIndent("0pt").setEndIndent("0pt");
    return flow;
  }

  /**
   * Builds the the {@link FoRoot}.
   */
  @Override
  public final String toString() {
    return this.root.build();
  }

  /**
   * Iterates over each symbol.
   *
   * @param text
   * @param function
   */
  public final String forEachSymbol(String text, BiFunction<String, String, String> function) {
    return this.symbols.forEach(text, function);
  }

  /**
   * Parses the configuration from the file.
   *
   * @param config
   * @param workingDir
   */
  public static FoBuilder parse(String config, File workingDir) throws IOException {
    FoBuilder template = new FoBuilder(workingDir);
    try (InputStream stream = DataUri.toInputStream(workingDir, config)) {
      StAX.parse(stream, new FoContextHandler(template));
    }
    return template;
  }
}