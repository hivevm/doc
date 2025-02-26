// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.doc.fo.FoGenerator;
import org.hivevm.doc.fo.FoRequestHandler;
import org.hivevm.doc.fo.pdf.PdfRenderer;
import org.hivevm.doc.md.MarkdownRequestHandler;
import org.hivevm.doc.template.Template;
import org.hivevm.util.ReplacerRequestHandler;
import org.hivevm.util.lambda.RequestStreamBuilder;
import org.hivevm.util.lambda.RequestStreamHandler;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * The {@link Pdf2Test} class.
 */
public class Pdf2Test {

    @Test
    public void testPDF() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File markdown = new File("README.md");
        File output = new File(Defaults.TARGET, markdown.getName() + ".pdf");

        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            String text = String.join("\n", reader.lines().toList());
            Document document = DocumentParser.parse(text, template.getKeywords());

            FoGenerator generator = new FoGenerator(template, false);
            try (InputStream istream = generator.generate(document);
                 OutputStream ostream = new FileOutputStream(output)) {
                PdfRenderer renderer = new PdfRenderer(template);
                renderer.handleRequest(istream, ostream, new File("."));
            }
        }
    }

    @Test
    public void testPDF2() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File markdown = new File("README2.md");
        File output = new File(Defaults.TARGET, markdown.getName() + ".pdf");

        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            String text = String.join("\n", reader.lines().toList());
            Document document = DocumentParser.parse(text, template.getKeywords());

            FoGenerator generator = new FoGenerator(template, false);
            try (InputStream istream = generator.generate(document);
                 OutputStream ostream = new FileOutputStream(output)) {
                PdfRenderer renderer = new PdfRenderer(template);
                renderer.handleRequest(istream, ostream, new File("."));
            }
        }
    }

    @Test
    public void testFo() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File markdown = new File("README.md").getAbsoluteFile();
        File output = new File(Defaults.TARGET, markdown.getName() + ".fo");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new MarkdownRequestHandler());
        builder.append(new ReplacerRequestHandler(Defaults.ENVIRONMENT));
        builder.append(new FoRequestHandler(template, true));
//        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(markdown)) {
                handler.handleRequest(istream, ostream, markdown.getParentFile());
            }
        }
    }
}