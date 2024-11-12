// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;

/**
 * The {@link DocuTest} class.
 */
class DocuTest extends AbstractTest {

  @Override
  protected final File getSource() {
    return new File(AbstractTest.WORKING_DIR, "README.md");
  }
}
