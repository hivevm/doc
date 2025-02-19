// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Header} class.
 */
public interface Header extends Node {

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
     *
     * @return
     */
    String getTitle();
}
