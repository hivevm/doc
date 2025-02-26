// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.pdf;

import java.io.*;

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
