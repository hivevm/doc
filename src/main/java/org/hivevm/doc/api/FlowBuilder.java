// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause
package org.hivevm.doc.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * ElementBuilder is used to create Text Flow structure during the creation process.
 */
public class FlowBuilder {

    public interface FlowContainer {
        FlowBuilder getFlowBuilder();
    }

    /**
     * Internal Node to support the build process.
     */
    private record Node(Flow elem, List<Flow> list) {
    }

    private final Node        root;
    private final Stack<Node> stack;

    public FlowBuilder() {
        this.root = new Node(null, new ArrayList<>());
        this.stack = new Stack<>();
        this.stack.push(this.root);
    }

    public final FlowBuilder addLine() {
        List<Flow> list = new ArrayList<>();
        Flow elem = new Flow.Text("", list);
        stack.peek().list().add(elem);
        stack.push(new Node(elem, list));
        return this;
    }

    public final FlowBuilder addText(String text) {
        Flow elem = new Flow.Text(text, Collections.emptyList());
        stack.peek().list().add(elem);
        return this;
    }

    public final FlowBuilder addStyle(Flow.Kind style) {
        List<Flow> list = new ArrayList<>();
        Flow elem = new Flow.Style(Collections.singleton(style), null, list);
        stack.peek().list().add(elem);
        stack.push(new Node(elem, list));
        return this;
    }

    public final FlowBuilder addColor(String color) {
        List<Flow> list = new ArrayList<>();
        Flow elem = new Flow.Style(Collections.emptySet(), color, list);
        stack.peek().list().add(elem);
        stack.push(new Node(elem, list));
        return this;
    }

    public final FlowBuilder addLink(String destination, String title) {
        List<Flow> list = new ArrayList<>();
        Flow elem = new Flow.Link(destination, title, list);
        stack.peek().list().add(elem);
        stack.push(new Node(elem, list));
        return this;
    }

    public final FlowBuilder addImage(String destination, String title, String align, String width, String height) {
        Flow elem = new Flow.Image(destination, title, align, width, height);
        stack.peek().list().add(elem);
        return this;
    }

    public final FlowBuilder restore() {
        stack.pop();
        return this;
    }

    public final String asText() {
        return root.list().stream()
                .filter(n -> n instanceof Flow.Text)
                .map(n -> ((Flow.Text) n).text())
                .collect(Collectors.joining(" "));
    }

    /**
     * Creates the Element
     */
    public List<Flow> build() {
        return stack.pop().list();
    }
}
