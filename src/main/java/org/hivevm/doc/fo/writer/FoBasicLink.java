// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoFont;

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
    public FoBasicLink(String url, FoNode parent) {
        super("basic-link", parent);
        if (FoBasicLink.EXTERNAL.matcher(url).find()) {
            set("external-destination", url);
        } else {
            set("internal-destination", url);
        }
    }
}
