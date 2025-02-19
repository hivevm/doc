// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
import java.util.Map;

/**
 * The {@link Defaults} class.
 */
interface Defaults {

    Map<String, String> ENVIRONMENT = Map.of("VERSION", "25.04");
    File                WORKING_DIR = new File("/data/hivevm/hivevm");
    File                TARGET      = new File(System.getProperty("user.home"));
    //  File                TARGET      = new File(System.getProperty("java.io.tmpdir"));
}
