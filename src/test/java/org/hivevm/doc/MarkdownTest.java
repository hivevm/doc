// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.document.TestContext;
import org.hivevm.util.MergeReader;
import org.junit.jupiter.api.Test;

/**
 * The {@link MarkdownTest} class.
 */
public class MarkdownTest {

    @Test
    public void testMarkdown() throws IOException {
        File markdown = new File("README.md");
        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            String text = String.join("\n", reader.lines().toList());
            DocumentParser.parse(text);
        }
    }

    @Test
    public void testMarkdownMerge() throws IOException {
        var workingDir = TestContext.defaults().workingDir();
        File markdown = new File(workingDir, "sample/manual/developer-manual.md");
        try (MergeReader merge = MergeReader.create(markdown)) {
            DocumentParser.parse(merge.readAll());
        }
    }

    @Test
    public void testAsciiDocMerge() throws Exception {
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");
        try (MergeReader merge = MergeReader.create(adoc)) {
            DocumentParser.parse(merge.readAll());
        }
    }
}