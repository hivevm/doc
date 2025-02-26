// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The {@link Node} defines a common interface for al concrete {@link Document} elements.
 */
public interface Node extends Iterable<Node> {

    default Iterator<Node> iterator() {
        return Collections.emptyIterator();
    }

    default Stream<Node> stream() {
        return Collections.<Node>emptySet().stream();
    }

    <R> void accept(DocumentVisitor<R> visitor, R data);
}
