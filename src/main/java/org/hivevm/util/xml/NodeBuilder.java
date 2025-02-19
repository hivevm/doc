// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.util.xml;

import java.io.Closeable;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Stack;

/**
 * The {@link NodeBuilder} provides an abstract interface for building structured documents.
 */
abstract class NodeBuilder<B extends NodeBuilder<B>>
        implements Closeable {

    private final Stack<String> nodes = new Stack<>();

    /**
     * Get the number of nodes on the {@link NodeBuilder}.
     */
    public final int count() {
        return this.nodes.size();
    }

    /**
     * Return <code>true</code> if the {@link NodeBuilder} hasn't any node.
     */
    public final String top() {
        return this.nodes.isEmpty() ? null : this.nodes.peek();
    }

    /**
     * Append a node on the top of the {@link NodeBuilder}.
     *
     * @param node - the node to push
     */
    @SuppressWarnings("unchecked")
    public B push(String node) {
        this.nodes.push(node);
        return (B) this;
    }

    /**
     * Build the top most node from {@link NodeBuilder}.
     *
     * @return pop the top node
     */
    public final String pop() {
        return this.nodes.pop();
    }

    /**
     * Build the top most node from {@link NodeBuilder}.
     *
     * @return pop the top node
     */
    public String build() {
        return pop();
    }

    /**
     * Writes a string value for the named attribute.
     *
     * @param name
     * @param value
     */
    public final NodeBuilder<B> set(String name, String value) {
        return set(name, value, false);
    }

    /**
     * Writes a string value for the named attribute. If empty is <code>true</code>, empty values will
     * be rendered.
     *
     * @param name
     * @param value
     * @param empty
     */
    public abstract NodeBuilder<B> set(String name, String value, boolean empty);

    /**
     * Writes a boolean value for the named attribute.
     *
     * @param name
     * @param value
     */
    public final NodeBuilder<B> set(String name, boolean value) {
        return set(name, value ? Boolean.TRUE.toString().toLowerCase() : null);
    }

    /**
     * Writes a numeric value for the named attribute.
     *
     * @param name
     * @param value
     */
    public final NodeBuilder<B> set(String name, double value) {
        boolean isDefault = Double.isNaN(value) || (value == 0.0);
        return set(name, isDefault ? null : NodeBuilder.getBigDecimalValue(BigDecimal.valueOf(value)));
    }

    /**
     * Writes an integer value for the named attribute.
     *
     * @param name
     * @param value
     */
    public NodeBuilder<B> set(String name, int value) {
        return set(name, value == 0 ? null : Integer.toString(value));
    }

    /**
     * Writes an enumeration value for the named attribute.
     *
     * @param name
     * @param value
     */
    public final NodeBuilder<B> set(String name, Enum<?> value) {
        return set(name,
                ((value == null) || (value.ordinal() == 0)) ? null : value.name().toUpperCase());
    }

    /**
     * Writes a comment for the next element.
     *
     * @param comment
     */
    public NodeBuilder<B> addComment(String comment) {
        return this;
    }

    /**
     * Writes a text value.
     *
     * @param text
     */
    public final NodeBuilder<B> addContentText(String text) {
        addContent(text, false);
        return this;
    }

    /**
     * Writes a text value in CData.
     *
     * @param data
     */
    public final NodeBuilder<B> addContentData(String data) {
        addContent(data, true);
        return this;
    }

    /**
     * Writes a text value in CData.
     *
     * @param content
     * @param isCData
     */
    protected abstract NodeBuilder<B> addContent(String content, boolean isCData);

    /**
     * Create a new instance of {@link NodeBuilder}.
     *
     * @param writer
     */
    public NodeBuilder<B> newInstance(Writer writer) throws Exception {
        throw new UnsupportedOperationException();
    }

    // Helper formatter for BigDecimal, otherwise the digits will not be printed on integers
    private static String getBigDecimalValue(BigDecimal input) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(1);
        return numberFormat.format(input);
    }
}
