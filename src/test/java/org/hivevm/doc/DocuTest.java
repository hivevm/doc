// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * The {@link DocuTest} class.
 */
class DocuTest extends AbstractTest {

  @Override
  protected final File getSource() {
    return new File(AbstractTest.WORKING_DIR, "README.md");
  }

  @Test
  void testPDF() {
    DocumentBuilder builder = new DocumentBuilder(AbstractTest.WORKING_DIR);
    builder.setConfig(":TOL:");
    builder.setTarget(AbstractTest.TARGET);
    builder.setSource(getSource().getAbsolutePath());
    builder.addProperties(AbstractTest.PROPERTIES);
    builder.build();
  }
}
