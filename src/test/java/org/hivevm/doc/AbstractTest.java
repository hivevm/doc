// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.doc.util.Replacer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * The {@link AbstractTest} class.
 */
abstract class AbstractTest {

  protected static final Properties PROPERTIES  = new Properties();
  protected static final File       WORKING_DIR = new File("/data/smartIO/develop");

  protected static final File       TARGET      = new File(AbstractTest.WORKING_DIR, "target");


  static {
    AbstractTest.PROPERTIES.setProperty("VERSION", "25.04");
    AbstractTest.TARGET.mkdirs();
  }

  protected abstract File getSource();

  @Test
  void testPDF() {
    DocumentBuilder builder = new DocumentBuilder(AbstractTest.WORKING_DIR);
    builder.setConfig(":TOL:");
    builder.setTarget(AbstractTest.TARGET);
    builder.setSource(getSource().getAbsolutePath());
    builder.addProperties(AbstractTest.PROPERTIES);
    builder.build();
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
