// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark;

import java.io.IOException;
import java.util.Arrays;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;

import org.hivevm.doc.commonmark.alerts.AlertExtension;
import org.hivevm.doc.commonmark.images.ImageExtension;
import org.hivevm.doc.commonmark.markers.MarkerExtension;
import org.hivevm.doc.commonmark.tables.TableExtension;

/**
 * The {@link Markdown} implements a reader based on Markdown. The Reader starts from a file an
 * includes referenced files to create a single huge Markdown file.
 */
public interface Markdown {

  /**
   * Creates an instance of {@link Parser}.
   */
  static Parser newInstance() throws IOException {
    return Markdown.newInstance(ImageExtension.create(), TableExtension.create(), AlertExtension.create(),
        MarkerExtension.create());
  }

  /**
   * Creates an instance of {@link Parser}.
   */
  static Parser newInstance(Extension... extensions) throws IOException {
    return Parser.builder().extensions(Arrays.asList(extensions)).build();
  }
}
