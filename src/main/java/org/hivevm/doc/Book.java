// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.util.List;

/**
 * The {@link Book} interface.
 */
public interface Book {

  String getTitle();

  List<Node> nodes();
}
