// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.Fo;
import org.hivevm.util.xml.XmlBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The {@link FoNode} is a builder implementation for Apache Formating Objects.
 */
public class FoNode implements Fo, Iterable<FoNode> {

    private final String     name;
    private final FoNode     parent;
    private final XmlBuilder builder;

    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<FoNode>        children   = new ArrayList<>();

    /**
     * Constructs an instance of {@link FoNode}.
     */
    protected FoNode(String name, XmlBuilder builder) {
        this.name = name;
        this.parent = null;
        this.builder = builder;
        builder.push(name);
    }

    /**
     * Constructs an instance of {@link FoNode}.
     */
    protected FoNode(String name, FoNode parent) {
        this.name = name;
        this.parent = parent;
        this.builder = parent.builder;
        this.parent.children.add(this);
    }

    /**
     * Set an attribute.
     */
    public final FoNode set(String name, String value) {
        if (!this.children.isEmpty())
            throw new IllegalArgumentException();
        if (value != null)
            this.attributes.put(name, value);

        if (value != null && parent == null)
            this.builder.set(name, value);
        return this;
    }

    /**
     * Set the ID.
     */
    public final FoNode setId(String id) {
        return set("id", id);
    }

    /**
     * Add a new child text {@link FoNode}.
     */
    public final FoNode addText(String text) {
        new FoNode(text, this) {

            @Override
            public void close() {
                builder.addContentText(text);
            }
        };
        return this;
    }

    /**
     * Returns an iterator over child {@link FoNode}.
     */
    @Override
    public final @NotNull Iterator<FoNode> iterator() {
        return this.children.iterator();
    }

    /**
     * Creates a new instance of a {@link FoNode}.
     */
    public final FoNode create(String name) {
        return new FoNode(name, this);
    }

    /**
     * Build the String.
     */
    public void close() {
        if (parent != null) {
            builder.push(this.name);
            this.attributes.keySet().forEach(n -> builder.set(n, attributes.get(n)));
        }

        forEach(FoNode::close);
        builder.build();
    }
}
