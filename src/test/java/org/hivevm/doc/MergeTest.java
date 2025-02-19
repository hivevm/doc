// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.util.MergeReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@link MergeTest} class.
 */
public class MergeTest {

    @Test
    void testMergeMarkdown() throws IOException {
        File markdown = new File(Defaults.WORKING_DIR, "sample/manual/developer-manual.md");
        File target = new File(Defaults.TARGET, markdown.getName());

        try (MergeReader merge = MergeReader.create(markdown)) {
            try (FileWriter writer = new FileWriter(target)) {
                writer.write(merge.readAll());
            }
        }
    }

    @Test
    void testMergeAsciiDoc() throws IOException {
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");
        File target = new File(Defaults.TARGET, adoc.getName());

        try (MergeReader merge = MergeReader.create(adoc)) {
            try (FileWriter writer = new FileWriter(target)) {
                writer.write(merge.readAll());
            }
        }
    }
}
