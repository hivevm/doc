// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

/**
 * The {@link PageUtil} class.
 */
abstract class PageUtil {

    private static final int[] ARAB = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4,
            1};

    /**
     * Encode the text to avoid XML reserved characters.
     *
     * @param text
     */
    public static String encode(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
