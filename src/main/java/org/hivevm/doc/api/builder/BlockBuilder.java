// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.Collection;
import org.hivevm.doc.api.Block;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.ParagraphElement;

/**
 * The {@link BlockBuilder} class.
 */
public class BlockBuilder extends ContainerBuilder implements Block {

    @Override
    public Kind kind() {
        return Kind.BLOCK;
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }

    public final Collection<ParagraphElement> elements() {
        return nodes();
    }
}
