// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.Break;
import org.hivevm.doc.api.Document;

/**
 * The {@link BreakBuilder} class.
 */
public class BreakBuilder extends NodeBuilder implements Break {

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }
}
