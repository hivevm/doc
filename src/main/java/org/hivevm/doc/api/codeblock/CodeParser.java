// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import java.util.Properties;
import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.ContainerBuilder;

/**
 * The {@link CodeParser} implements a tokenizer for a specific language.
 */
public interface CodeParser {

    default void generate(String text, ContainerBuilder builder, Properties properties) {
        generate(text, builder.addCode());
    }

    default void generate(String text, CodeBuilder builder) {
        builder.getFlowBuilder().addText(text);
    }
}
