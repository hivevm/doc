// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.alerts;

import org.commonmark.node.CustomBlock;

public class AlertBlock extends CustomBlock {

    private final Alert type;

    public AlertBlock(Alert type) {
        this.type = type;
    }

    public Alert getType() {
        return this.type;
    }
}