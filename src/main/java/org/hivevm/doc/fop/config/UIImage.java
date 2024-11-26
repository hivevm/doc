// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.config;

import java.net.URI;
import java.util.Properties;

import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.fop.nodes.FoExternalGraphic;
import org.hivevm.util.DataUri;

/**
 * The {@link UIImage} class.
 */
class UIImage implements UIRenderable {

  private final URI uri;

  /**
   * Constructs an instance of {@link UIImage}.
   *
   * @param uri
   */
  public UIImage(URI uri) {
    this.uri = uri;
  }

  @Override
  public void render(FoBlock container, Properties properties) {
    String base64 = DataUri.loadImage(this.uri);
    container.addNode(new FoExternalGraphic(base64));
  }
}