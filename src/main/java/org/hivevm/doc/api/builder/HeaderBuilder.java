// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.Random;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.Header;

/**
 * The {@link HeaderBuilder} class.
 */
public class HeaderBuilder extends NodeBuilder
        implements Header, FlowBuilder.FlowContainer {

    private final String id;
    private final int    level;

    private String title;

    private final FlowBuilder builder = new FlowBuilder();

    /**
     * Constructs an instance of {@link HeaderBuilder}.
     */
    HeaderBuilder(DocumentBuilder root, int level) {
        this.level = level;
        this.id = Long.toHexString(new Random().nextLong());
        root.addIndex(this);
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
        if (title == null || title.isEmpty())
            title = builder.asText();
        return title == null ? "" : title;
    }

    @Override
    public FlowBuilder getFlowBuilder() {
        return builder;
    }

    /**
     * Get all {@link Flow} elements of the header.
     */
    @Override
    public final Flow flow() {
        var flows = builder.build();
        return flows.size() != 1 ? new Flow.Text(null, flows) : flows.get(0);
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
