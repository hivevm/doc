// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import org.hivevm.doc.fop.FoGenerator;
import org.hivevm.doc.fop.config.FoBuilder;
import org.hivevm.doc.fop.config.FoContext;
import org.hivevm.doc.pdf.PdfGenerator;

/**
 * The {@link PdfTest} class.
 */
class PdfTest {

  @Test
  void testPDF() throws Exception {
    File workingDir = new File(".");
    File file = new File(workingDir, "README.md");
    String config = ":default.ui.xml";

    FoBuilder template = FoContext.parse(config, workingDir);

    FoGenerator builder = new FoGenerator();
    builder.setName("Name");
    builder.setTarget(Defaults.TARGET);
    builder.setContext(template.build());

    File output = new File(Defaults.TARGET, file.getName() + ".pdf");
    Book book = DocumentBuilder.parseMarkdown(file, Defaults.ENVIRONMENT, template.getKeywords());
    try (OutputStream ostream = new FileOutputStream(output)) {
      try (InputStream istream = builder.generate(book)) {

        PdfGenerator generator = new PdfGenerator(template.getWidth(), template.getHeight(), workingDir);
        template.forEachFont(f -> generator.addFont(f));
        generator.write(istream, ostream);
      }
    }
  }

  @Test
  void testPDF2() throws Exception {
    File workingDir = new File(".");
    File file = new File("/data/smartIO/develop/sample/manual", "user-manual.md");
    String config = ":tol.ui.xml";

    FoBuilder template = FoContext.parse(config, workingDir);

    FoGenerator builder = new FoGenerator();
    builder.setName("Name");
    builder.setTarget(Defaults.TARGET);
    builder.setContext(template.build());

    File output = new File(Defaults.TARGET, file.getName() + ".pdf");
    Book book = DocumentBuilder.parseMarkdown(file, Defaults.ENVIRONMENT, template.getKeywords());
    try (OutputStream ostream = new FileOutputStream(output)) {
      try (InputStream istream = builder.generate(book)) {

        PdfGenerator generator = new PdfGenerator(template.getWidth(), template.getHeight(), workingDir);
        template.forEachFont(f -> generator.addFont(f));
        generator.write(istream, ostream);
      }
    }
  }
}
