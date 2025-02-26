// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

/**
 * The {@link ContainerBuilder} class.
 */
public abstract class ContainerBuilder extends NodeBuilder {

    public final ParagraphBuilder addParagraph() {
        return add(new ParagraphBuilder());
    }

    public final BlockBuilder addBlock() {
        return add(new BlockBuilder());
    }

    public final CodeBuilder addCode() {
        return add(new CodeBuilder());
    }

    public final ListBuilder addList(boolean ordered) {
        return add(new ListBuilder(ordered));
    }
}
