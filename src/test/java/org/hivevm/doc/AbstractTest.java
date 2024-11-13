// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
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
}
