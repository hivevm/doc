// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import org.hivevm.document.CodeBlock;

import java.util.List;

@FunctionalInterface
public interface CodeGenerator {

    void generate(String[] lines, List<CodeBlock.Line> rows);
}
