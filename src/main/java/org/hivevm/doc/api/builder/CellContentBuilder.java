// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.Paragraph;

/**
 * The {@link CellContentBuilder} class.
 */
public class CellContentBuilder extends NodeBuilder
        implements Paragraph, FlowBuilder.FlowContainer {

    private final FlowBuilder builder = new FlowBuilder();

    public final CellContentBuilder setPadding(String horizontal, String vertical) {
        return setPadding(horizontal, horizontal, vertical, vertical);
    }

    public final CellContentBuilder setPadding(String left, String right, String top, String bottom) {
        return this;
    }

    public final CodeBuilder addCode() {
        return add(new CodeBuilder());
    }

    public final CellFlowBuilder addFenced() {
        return add(new CellFlowBuilder());
    }

    @Override
    public FlowBuilder getFlowBuilder() {
        return builder;
    }

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
