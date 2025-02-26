// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Header} class.
 */
public interface Header extends StructuralElement {

    /**
     * Gets the identifier
     */
    String getId();

    /**
     * Gets the level
     */
    int getLevel();

    /**
     * Gets the title
     */
    String getTitle();

    /**
     * Gets the text flow in the header
     */
    Flow flow();
}
