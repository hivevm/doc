// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Paragraph} class.
 */
public interface Paragraph extends Node {

    boolean isBlock();

    boolean isSoftBreak();

    boolean isLineBreak();

    String getBackground();

    String getPaddingTop();

    String getPaddingLeft();

    String getPaddingRight();

    String getPaddingBottom();
}
