// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoExternalGraphic} class.
 */
public class FoExternalGraphic extends FoNode {

  /**
   * Constructs an instance of {@link FoExternalGraphic}.
   *
   * @param url
   */
  public FoExternalGraphic(String url) {
    super("fo:external-graphic");
    set("src", "url(" + url + ")");
  }

  public FoExternalGraphic setWidth(String width) {
    set("width", (width == null) ? "auto" : width);
    return this;
  }

  public FoExternalGraphic setHeight(String height) {
    set("height", (height == null) ? "auto" : height);
    return this;
  }

  public FoExternalGraphic setSize(String width, String height) {
    return setWidth(width).setHeight(height);
  }
}
