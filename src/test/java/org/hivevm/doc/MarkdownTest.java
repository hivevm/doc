// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.util.Replacer;

/**
 * The {@link MarkdownTest} class.
 */
class MarkdownTest {

  @Test
  void testMarkdownMerge() throws IOException {
    Replacer replacer = new Replacer(Defaults.ENVIRONMENT);
    File source = new File(Defaults.WORKING_DIR, "sample/manual/developer-manual.md");

    File target = new File(Defaults.TARGET, source.getName());
    try (MarkdownReader reader = new MarkdownReader(source)) {
      try (FileWriter writer = new FileWriter(target)) {
        writer.write(replacer.replaceAll(reader.readAll()));
      }
    }

    try (FileWriter writer = new FileWriter(target + ".md")) {
      try (BufferedReader reader = new BufferedReader(new MarkdownReader(source))) {
        String line;
        while ((line = reader.readLine()) != null) {
          writer.write(replacer.replaceAll(line
              ) + "\n");
        }
      }
    }

  }
}
