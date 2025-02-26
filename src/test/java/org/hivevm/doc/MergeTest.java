// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.hivevm.document.TestContext;
import org.hivevm.util.MergeReader;
import org.junit.jupiter.api.Test;

/**
 * The {@link MergeTest} class.
 */
public class MergeTest {

    @Test
    void testMergeMarkdown() throws IOException {
        var context = TestContext.defaults();
        var markdown = new File(context.workingDir(), "sample/manual/developer-manual.md");
        var target = new File(context.target(), markdown.getName());

        try (MergeReader merge = MergeReader.create(markdown)) {
            try (FileWriter writer = new FileWriter(target)) {
                writer.write(merge.readAll());
            }
        }
    }

    @Test
    void testMergeAsciiDoc() throws IOException {
        var context = TestContext.defaults();
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");
        File target = new File(context.target(), adoc.getName());

        try (MergeReader merge = MergeReader.create(adoc)) {
            try (FileWriter writer = new FileWriter(target)) {
                writer.write(merge.readAll());
            }
        }
    }
}
