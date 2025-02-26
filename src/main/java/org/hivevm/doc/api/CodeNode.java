// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link CodeNode} class.
 */
public interface CodeNode extends Node {

    boolean isInline();

    boolean isStyled();

    String getFontSize();

    String getBackground();

    String getTextColor();

    String getBorderColor();
}
