// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoFont;

import java.util.regex.Pattern;

/**
 * The {@link FoBasicLink} class.
 */
public class FoBasicLink extends FoNode implements FoFont<FoBasicLink> {

  private static final Pattern EXTERNAL = Pattern.compile("^[a-zA-Z]+:.+");

  /**
   * Constructs an instance of {@link FoBasicLink}.
   *
   * @param url
   */
  public FoBasicLink(String url) {
    super("fo:basic-link");
    if (FoBasicLink.EXTERNAL.matcher(url).find()) {
      set("external-destination", url);
    } else {
      set("internal-destination", url);
    }
  }
}
