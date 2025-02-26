// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.Collection;

/**
 * The {@link List} class.
 */
public interface List extends ParagraphElement {

    boolean isOrdered();

    Iterable<Item> items();

    interface Item extends DocumentNode {

        Collection<ParagraphElement> elements();
    }
}
