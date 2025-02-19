// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

/**
 * The {@link CodeParser} implements a tokenizer for a specific language.
 */
@FunctionalInterface
public interface CodeParser {

    void generate(String text, CodeBuilder builder);
}
