// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

import java.util.*;

/**
 * The {@link FoAbstract} is a builder implementation for Apache Formating Objects.
 */
public class FoAbstract implements FoNode {

    private final String              name;
    private final FoAbstract          parent;
    private final XmlBuilder          builder;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<FoNode>        children   = new ArrayList<>();

    /**
     * Constructs an instance of {@link FoAbstract}.
     *
     * @param name
     * @param builder
     */
    protected FoAbstract(String name, XmlBuilder builder) {
        this.name = name;
        this.parent = null;
        this.builder = builder;
    }

    /**
     * Constructs an instance of {@link FoAbstract}.
     *
     * @param name
     * @param parent
     */
    protected FoAbstract(String name, FoAbstract parent) {
        this.name = name;
        this.parent = parent;
        this.builder = parent.builder;
        this.parent.addNode(this);
    }

    public final FoNode getParent() {
        return parent;
    }

    public final XmlBuilder getBuilder() {
        return builder;
    }

    /**
     * Get the name.
     */
    protected final String getTagName() {
        return this.name;
    }

    /**
     * Get the attributes.
     */
    protected final Map<String, String> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    public final boolean hasChildren() {
        return !this.children.isEmpty();
    }

    /**
     * Set the ID.
     *
     * @param id
     */
    public final FoNode setId(String id) {
        return set("id", id);
    }

    /**
     * Set an attribute.
     *
     * @param name
     * @param value
     */
    protected final FoNode setAttribute(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    /**
     * Set an attribute.
     *
     * @param name
     * @param value
     */
    @Override
    public FoNode set(String name, String value) {
        return setAttribute(name, value);
    }

    /**
     * Add a new child {@link FoAbstract}.
     *
     * @param builder
     */
    @Override
    public final FoNode addNode(FoNode builder) {
        this.children.add(builder);
        return this;
    }

    /**
     * Add a new child text {@link FoAbstract}.
     *
     * @param text
     */
    @Override
    public final FoNode addText(String text) {
        return addNode(FoNode.text(text, getBuilder()));
    }

    /**
     * Returns an iterator over child {@link FoAbstract}.
     */
    @Override
    public final Iterator<FoNode> iterator() {
        return this.children.iterator();
    }

    /**
     * Returns an iterator over child {@link FoAbstract}.
     */
    @Override
    public final FoNode last() {
        return this.children.isEmpty() ? null : this.children.get(this.children.size() - 1);
    }

    /**
     * Build the String.
     */
    public void close() {
        builder.push(getTagName());
        getAttributes().keySet().stream()
                .filter(n -> getAttributes().get(n) != null)
                .forEach(n -> builder.set(n, getAttributes().get(n)));
        forEach(n -> n.close());
        builder.build();
    }
}
