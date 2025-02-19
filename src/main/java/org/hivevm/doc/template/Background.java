// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import org.hivevm.util.DataUri;

import java.io.File;
import java.net.URI;

/**
 * The {@link Background} class.
 */
public interface Background {

    String get();

    boolean isImage();

    static Background of(String value, File workingDir) {
        return value.startsWith("#") ? new Color(value) : new Image(DataUri.toURI(workingDir, value));
    }

    class Color implements Background {

        private final String color;

        public Color(String color) {
            this.color = color;
        }

        public boolean isImage() {
            return false;
        }

        public String get() {
            return color;
        }
    }

    class Image implements Background {

        private final URI image;

        public Image(URI image) {
            this.image = image;
        }

        public boolean isImage() {
            return true;
        }

        public String get() {
            return DataUri.loadImage(image);
        }
    }
}
