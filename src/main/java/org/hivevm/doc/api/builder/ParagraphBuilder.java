// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;
import org.hivevm.doc.api.Paragraph;

/**
 * The {@link ParagraphBuilder} class.
 */
public class ParagraphBuilder extends NodeBuilder
        implements Paragraph, FlowBuilder.FlowContainer {

    private final FlowBuilder builder = new FlowBuilder();

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
