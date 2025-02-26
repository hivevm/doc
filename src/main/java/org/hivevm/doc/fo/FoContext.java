// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import java.util.Stack;
import org.hivevm.doc.fo.writer.FoFlow;
import org.hivevm.doc.fo.writer.FoNode;
import org.hivevm.doc.fo.writer.FoRoot;
import org.hivevm.doc.template.Template;

/// The `FoContext` class provides a context for managing formatting object structures during the
/// document formatting process. It acts as a central manager for the root object, template
/// definition, active flow, and stack of nodes within the formatting object model.
///
/// This class facilitates operations such as setting up and retrieving the current flow, pushing
/// and popping nodes from the stack, and accessing the root, template, and the current active
/// node.
public class FoContext {

    private final FoRoot        root;
    private final Template      template;
    private final Stack<FoNode> nodes;

    private FoFlow flow;

    public FoContext(FoRoot root, Template template) {
        this.root = root;
        this.template = template;
        this.nodes = new Stack<>();
    }

    public final FoRoot root() {
        return this.root;
    }

    public final Template template() {
        return this.template;
    }

    public final FoFlow flow() {
        return this.flow;
    }

    public final void setFlow(FoFlow flow) {
        this.flow = flow;
        this.nodes.clear();
    }

    public final FoNode pop() {
        return this.nodes.pop();
    }

    public final void push(FoNode node) {
        this.nodes.push(node);
    }

    protected final FoNode top() {
        return this.nodes.isEmpty() ? this.flow : this.nodes.peek();
    }
}