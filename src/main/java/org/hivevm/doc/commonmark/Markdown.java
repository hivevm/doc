// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.hivevm.doc.commonmark.alerts.AlertExtension;
import org.hivevm.doc.commonmark.images.ImageExtension;
import org.hivevm.doc.commonmark.markers.MarkerExtension;
import org.hivevm.doc.commonmark.tables.TableExtension;

import java.io.IOException;
import java.util.Arrays;

/**
 * The {@link Markdown} creates an instance of Common Mark {@link Parser}, with some extensions for common mark.
 */
public interface Markdown {

    /**
     * Creates an instance of {@link Parser}.
     */
    static Parser newInstance() throws IOException {
        return Markdown.newInstance(
                ImageExtension.create(),
                TableExtension.create(),
                AlertExtension.create(),
                MarkerExtension.create()
        );
    }

    /**
     * Creates an instance of {@link Parser}.
     */
    static Parser newInstance(Extension... extensions) throws IOException {
        return Parser.builder().extensions(Arrays.asList(extensions)).build();
    }
}
