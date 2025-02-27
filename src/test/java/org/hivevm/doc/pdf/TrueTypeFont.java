// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

public class TrueTypeFont implements FilenameFilter {

  private static final File FONT_PATH = new File("src/main/resources/fonts");

  @Override
  public final boolean accept(File dir, String name) {
    return name.toLowerCase().endsWith(".ttf");
  }

  public static void main(String[] args) throws Exception {
    FilenameFilter filter = new TrueTypeFont();
    for (File font : TrueTypeFont.FONT_PATH.listFiles(filter)) {
      String filename = String.format("%s.xml", font.getName());
      File metric = new File(TrueTypeFont.FONT_PATH, filename);

      try (InputStream iStream = new FileInputStream(font.getAbsolutePath())) {
        try (OutputStream oStream = new FileOutputStream(metric.getAbsolutePath())) {
          FontResolver.createFontMetric(iStream, oStream);
        }
      }
    }
  }
}
