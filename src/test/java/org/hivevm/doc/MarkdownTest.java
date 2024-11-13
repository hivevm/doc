// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.doc.util.Replacer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@link MarkdownTest} class.
 */
class MarkdownTest extends AbstractTest {

  @Override
  protected final File getSource() {
    return new File(AbstractTest.WORKING_DIR, "sample/manual/developer-manual.md");
  }

  @Test
  void testMarkdownMerge() throws IOException {
    MarkdownReader reader = new MarkdownReader(getSource());
    File target = new File(AbstractTest.TARGET, getSource().getName());

    Replacer replacer = new Replacer(AbstractTest.PROPERTIES);
    try (FileWriter writer = new FileWriter(target)) {
      writer.write(replacer.replaceAll(reader.readAll()));
    }
  }
}
