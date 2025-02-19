// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.adoc;

import org.hivevm.util.Resolver;
import org.hivevm.util.lambda.RequestStreamHandler;

import java.io.*;


/**
 * The {@link AsciiDocRequestHandler} implements a reader based on MARKDOWN. The Reader starts from an
 * {@link InputStream} an includes referenced files to create a single huge MARKDOWN file.
 */
public class AsciiDocRequestHandler implements RequestStreamHandler {

    public void handleRequest(InputStream input, OutputStream output, File context) throws IOException {
        Resolver resolver = new Resolver.PathResolver(context);
        try (Reader reader = new InputStreamReader(input); Writer writer = new OutputStreamWriter(output)) {
            AsciiDocReader markdown = new AsciiDocReader(reader, resolver);
            writer.write(markdown.readAll());
        }
    }
}
