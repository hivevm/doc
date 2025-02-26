// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;

/**
 * The {@link CellFlowBuilder} class.
 */
public class CellFlowBuilder extends NodeBuilder implements FlowBuilder.FlowContainer {

    private final FlowBuilder builder = new FlowBuilder();

    public final CellFlowBuilder setBold() {
        return this;
    }

    public final CellFlowBuilder setBackground(String background) {
        return this;
    }

    public final CellFlowBuilder setPadding(String horizontal, String vertical) {
        return setPadding(horizontal, horizontal, vertical, vertical);
    }

    public final CellFlowBuilder setPadding(String left, String right, String top, String bottom) {
        return this;
    }

    @Override
    public FlowBuilder getFlowBuilder() {
        return builder;
    }

    public final Flow flow() {
        var flows = builder.build();
        return flows.size() != 1 ? new Flow.Text(null, flows) : flows.get(0);
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        throw new UnsupportedOperationException();
    }
}
