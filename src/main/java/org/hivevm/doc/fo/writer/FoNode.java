// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.Fo;
import org.hivevm.util.xml.XmlBuilder;

import java.util.Iterator;

/**
 * The {@link FoNode} is a builder implementation for Apache Formating Objects.
 */
public interface FoNode extends Fo, Iterable<FoNode> {

    XmlBuilder getBuilder();

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    boolean hasChildren();

    /**
     * Set the ID.
     *
     * @param id
     */
    FoNode setId(String id);

    /**
     * Set an attribute.
     *
     * @param name
     * @param value
     */
    @Override
    FoNode set(String name, String value);

    /**
     * Add a new child {@link FoNode}.
     *
     * @param builder
     */
    FoNode addNode(FoNode builder);

    /**
     * Add a new child text {@link FoNode}.
     *
     * @param text
     */
    FoNode addText(String text);

    /**
     * Returns an iterator over child {@link FoNode}.
     */
    @Override
    Iterator<FoNode> iterator();

    /**
     * Returns an iterator over child {@link FoNode}.
     */
    FoNode last();

    /**
     * Build the String.
     */
    void close();

    /**
     * Constructs a text {@link FoNode}.
     *
     * @param text
     * @param builder
     */
    static FoNode text(String text, XmlBuilder builder) {
        return new FoAbstract(text, builder) {

            @Override
            public void close() {
                builder.addContentText(getTagName());
            }
        };
    }

    /**
     * Creates a new instance of a {@link FoNode}.
     *
     * @param name
     * @param builder
     */
    static FoNode create(String name, XmlBuilder builder) {
        return new FoAbstract(name, builder);
    }
}
