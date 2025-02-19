// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Header;

import java.util.Random;

/**
 * The {@link HeaderBuilder} class.
 */
public class HeaderBuilder extends PageBuilder implements Header {

    private final String          id;
    private final DocumentBuilder root;
    private final int             level;

    private String title;

    /**
     * Constructs an instance of {@link HeaderBuilder}.
     */
    HeaderBuilder(DocumentBuilder root, int level) {
        this.level = level;
        this.root = root;
        this.id = Long.toHexString(new Random().nextLong());
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final int getLevel() {
        return this.level;
    }

    @Override
    public final String getTitle() {
        return title == null ? "" : title;
    }

    public final HeaderBuilder setTitle(String title) {
        this.title = (title == null) ? title : title.trim();
        this.root.addIndex(this);
        return this;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
