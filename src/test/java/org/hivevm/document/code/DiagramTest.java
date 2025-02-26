// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * The {@link DiagramTest} class.
 */
public abstract class DiagramTest {

    protected final String toSVG(InputStream istream) throws IOException {
        try (var ostream = new ByteArrayOutputStream()) {
            handleRequest(istream, ostream, null);
            var bytes = ostream.toByteArray();
            var base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/svg+xml;base64," + base64;
        }
    }

    protected abstract void handleRequest(InputStream istream, OutputStream ostream, String type) throws IOException;
}