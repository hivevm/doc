// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

/**
 * The {@link PageMargin} class.
 */
public interface PageMargin {

    /**
     * Set the template top padding.
     *
     * @param value
     */
    void setTop(String value);

    /**
     * Set the template left padding.
     *
     * @param value
     */
    void setLeft(String value);

    /**
     * Set the template right padding.
     *
     * @param value
     */
    void setRight(String value);

    /**
     * Set the template bottom padding.
     *
     * @param value
     */
    void setBottom(String value);
}
