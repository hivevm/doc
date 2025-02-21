// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoPageNumberCitation} class.
 */
public class FoPageNumberCitation extends FoAbstract {

    public FoPageNumberCitation(String id, XmlBuilder builder) {
        super("page-number-citation", builder);
        set("ref-id", id);
    }
}
