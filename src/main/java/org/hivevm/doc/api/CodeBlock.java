// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.List;

/**
 * The {@link CodeBlock} class.
 */
public interface CodeBlock extends ParagraphElement {

    /**
     * Get the flow lines.
     */
    List<Flow> lines();
}
