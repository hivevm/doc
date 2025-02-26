// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.Collection;

///
public interface Block extends ParagraphElement {

    enum Kind {
        BLOCK,
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }

    Kind kind();

    Collection<ParagraphElement> elements();
}
