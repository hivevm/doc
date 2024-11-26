// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.util.Replacer;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@link MarkdownTest} class.
 */
class MarkdownTest {

  @Test
  void testMarkdownMerge() throws IOException {
    File source = new File(Defaults.WORKING_DIR, "sample/manual/developer-manual.md");

    MarkdownReader reader = new MarkdownReader(source);
    File target = new File(Defaults.TARGET, source.getName());

    Replacer replacer = new Replacer(Defaults.ENVIRONMENT);
    try (FileWriter writer = new FileWriter(target)) {
      writer.write(replacer.replaceAll(reader.readAll()));
    }
  }
}
