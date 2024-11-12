// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes.set;

/**
 * The {@link Fo} is an interface for Apache Formating Objects.
 */
public interface Fo {

  /**
   * Set an attribute.
   *
   * @param name
   * @param value
   */
  Fo set(String name, String value);
}
