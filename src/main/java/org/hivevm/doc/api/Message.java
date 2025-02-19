// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

/**
 * The {@link Message} class.
 */
public interface Message extends Node {

    enum Style {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }

    Style getStyle();
}
