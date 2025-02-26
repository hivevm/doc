// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.alerts;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;

public class AlertExtension implements Parser.ParserExtension {

    private AlertExtension() {
    }

    public static Extension create() {
        return new AlertExtension();
    }

    @Override
    public void extend(org.commonmark.parser.Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new AlertBlockParser.Factory());
    }
}
