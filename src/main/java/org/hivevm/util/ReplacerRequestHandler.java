// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.util;

import org.hivevm.util.lambda.RequestStreamHandler;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link ReplacerRequestHandler} class.
 */
public class ReplacerRequestHandler implements RequestStreamHandler {

    private final Replacer replacer;

    public ReplacerRequestHandler(Map<String, String> properties) {
        this.replacer = new Replacer(properties);
    }

    public void handleRequest(InputStream input, OutputStream output, File context) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            try (Writer writer = new OutputStreamWriter(output)) {
                writer.write(replacer.replaceAll(reader.lines().collect(Collectors.joining("\n"))));
            }
        }
    }
}