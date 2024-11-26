// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.builder;

import org.hivevm.doc.Link;

/**
 * The {@link LinkBuilder} class.
 */
public class LinkBuilder extends ContentBuilder implements Link {

  private String link;

  /**
   * Constructs an instance of {@link LinkBuilder}.
   */
  public LinkBuilder(String link) {
    this.link = link;
  }

  /**
   * Gets the link.
   */
  @Override
  public final String getLink() {
    return this.link;
  }

  final void setLink(String link) {
    this.link = link;
  }
}
