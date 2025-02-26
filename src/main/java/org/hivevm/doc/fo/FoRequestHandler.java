// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.doc.template.Template;
import org.hivevm.util.lambda.RequestStreamHandler;

import java.io.*;

public class FoRequestHandler implements RequestStreamHandler {

    private final Template template;
    private final boolean  isFormatted;

    public FoRequestHandler(Template template, boolean isFormatted) {
        this.template = template;
        this.isFormatted = isFormatted;
    }

    public void handleRequest(InputStream input, OutputStream output, File context) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String text = String.join("\n", reader.lines().toList());
            Document document = DocumentParser.parse(text, template.getKeywords());

            FoGenerator generator = new FoGenerator(template, isFormatted);
            try (InputStream istream = generator.generate(document)) {
                output.write(istream.readAllBytes());
            }
        }
    }
}
