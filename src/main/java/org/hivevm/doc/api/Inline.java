// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Inline} class.
 */
public interface Inline extends Node {

    String getColor();

    String getBackground();

    String getPaddingTop();

    String getPaddingLeft();

    String getPaddingRight();

    String getPaddingBottom();

    boolean isBold();

    boolean isItalic();

    boolean isUnderline();

    boolean isOverline();

    boolean isStrikethrough();

    boolean isFootnote();
}
