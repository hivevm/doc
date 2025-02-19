// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoExternalGraphic} class.
 */
public class FoExternalGraphic extends FoNode {

    /**
     * Constructs an instance of {@link FoExternalGraphic}.
     *
     * @param url
     */
    public FoExternalGraphic(String url, XmlBuilder builder) {
        super("fo:external-graphic", builder);
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

    public FoExternalGraphic setContentWidth(String width) {
        set("content-width", (width == null) ? "auto" : width);
        return this;
    }

    public FoExternalGraphic setContentHeight(String height) {
        set("content-height", (height == null) ? "auto" : height);
        return this;
    }

    public FoExternalGraphic setSize(String width, String height) {
        return setWidth(width).setHeight(height);
    }
}
