// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.List;
import org.hivevm.doc.api.CodeBlock;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Flow;
import org.hivevm.doc.api.FlowBuilder;

/**
 * The {@link CodeBuilder} class.
 */
public class CodeBuilder extends NodeBuilder implements CodeBlock {

    private final FlowBuilder builder = new FlowBuilder();

    public FlowBuilder getFlowBuilder() {
        return builder;
    }

    @Override
    public List<Flow> lines() {
        return builder.build();
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
