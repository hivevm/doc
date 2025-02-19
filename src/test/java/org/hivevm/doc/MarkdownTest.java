// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.doc.template.Template;
import org.hivevm.util.MergeReader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The {@link MarkdownTest} class.
 */
public class MarkdownTest {

    @Test
    public void testMarkdown() throws IOException {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File markdown = new File("README.md");
        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            String text = String.join("\n", reader.lines().toList());
            Document document = DocumentParser.parse(text, template.getKeywords());

            System.out.println(">>>>>>>>");
        }
    }

    @Test
    public void testMarkdownMerge() throws IOException {
        Template template = Template.parse(":default.ui.xml", new File("."));
        File markdown = new File(Defaults.WORKING_DIR, "sample/manual/developer-manual.md");

        try (MergeReader merge = MergeReader.create(markdown)) {
            Document document = DocumentParser.parse(merge.readAll(), template.getKeywords());

            System.out.println(">>>>>>>>");
        }
    }

    @Test
    public void testAsciiDocMerge() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");

        try (MergeReader merge = MergeReader.create(adoc)) {
            Document document = DocumentParser.parse(merge.readAll(), template.getKeywords());

            System.out.println(">>>>>>>>");
        }
    }
}