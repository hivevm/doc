// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Link;

/**
 * The {@link LinkBuilder} class.
 */
public class LinkBuilder extends ContentBuilder implements Link {

    private String link;

    /**
     * Constructs an instance of {@link LinkBuilder}.
     */
    public LinkBuilder(String link) {
        this.link = link;
    }

    /**
     * Gets the link.
     */
    @Override
    public final String getLink() {
        return this.link;
    }

    final void setLink(String link) {
        this.link = link;
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
