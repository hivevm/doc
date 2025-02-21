// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.Template;
import org.hivevm.util.xml.XmlBuilder;

import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * The {@link FoContext} class.
 */
public class FoContext {

    private final FoRoot        root;
    private       FoFlow        flow;
    private final Template      template;
    private final String        fontCode;
    private final Stack<FoNode> nodes = new Stack<>();

    private final Map<String, BiConsumer<FoPageSequence, Properties>> pageSet;


    /**
     * Constructs an instance of {@link FoContext}.
     *
     * @param root
     * @param template
     * @param pageSet
     */
    public FoContext(FoRoot root, Template template,
                     Map<String, BiConsumer<FoPageSequence, Properties>> pageSet,
                     String fontCode) {
        this.root = root;
        this.template = template;
        this.pageSet = pageSet;
        this.fontCode = fontCode;
    }

    public final FoRoot getRoot() {
        return root;
    }

    public final FoFlow getFlow() {
        return flow;
    }

    public final Template getTemplate() {
        return template;
    }

    /**
     * Get the top {@link FoNode}.
     */
    public final FoNode top() {
        return this.nodes.isEmpty() ? this.flow : this.nodes.peek();
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
    public final void push(FoNode node) {
        this.nodes.push(node);
    }

    /**
     * Get the code font name.
     */
    public final String getCodeFont() {
        return this.fontCode;
    }

    /**
     * Set the supplier that creates a {@link FoFlow}.
     *
     * @param id
     * @param name
     */
    public final FoFlow createFlow(String id, String name, boolean initial, Properties properties, XmlBuilder builder) {
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
        FoStaticContent content = new FoStaticContent("xsl-footnote-separator", builder);

        FoBlock block = content.addBlock()
                .setTextAlignLast("justify")
                .setPadding("0.5em");

        FoLeader leader = new FoLeader(builder)
                .setPattern("rule")
                .setLength("50%")
                .setRuleThickness("0.5pt")
                .setColor("#777777");
        block.addNode(leader);

        page.addNode(content);


        if (this.pageSet.containsKey(name)) {
            this.pageSet.get(name).accept(page, properties);
        }

        this.flow = page.flow("region-body");
        this.flow.setStartIndent("0pt").setEndIndent("0pt");
        return this.flow;
    }

    /**
     * Iterates over each symbol.
     *
     * @param text
     * @param function
     */
    public final String forEachSymbol(String text, BiFunction<String, String, String> function) {
        return this.template.getSymbols().forEach(text, function);
    }
}