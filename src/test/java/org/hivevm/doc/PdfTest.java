// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.hivevm.doc.adoc.AsciiDocRequestHandler;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.doc.fo.FoGenerator;
import org.hivevm.doc.fo.FoRequestHandler;
import org.hivevm.doc.fo.pdf.PdfRenderer;
import org.hivevm.doc.md.MarkdownRequestHandler;
import org.hivevm.doc.template.Template;
import org.hivevm.util.DataUri;
import org.hivevm.util.ReplacerRequestHandler;
import org.hivevm.util.lambda.RequestStreamBuilder;
import org.hivevm.util.lambda.RequestStreamHandler;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * The {@link PdfTest} class.
 */
public class PdfTest {

    @Test
    public void testPDF() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File markdown = new File("README.md");
        File output = new File(Defaults.TARGET, markdown.getName() + ".pdf");

        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            String text = String.join("\n", reader.lines().toList());
            Document document = DocumentParser.parse(text, template.getKeywords());

            FoGenerator generator = new FoGenerator(template, true);
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

        File markdown = new File("README.md").getAbsoluteFile();
        File output = new File(Defaults.TARGET, markdown.getName() + ".fo");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new MarkdownRequestHandler());
        builder.append(new ReplacerRequestHandler(Defaults.ENVIRONMENT));
        builder.append(new FoRequestHandler(template, false));
//        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(markdown)) {
                handler.handleRequest(istream, ostream, markdown.getParentFile());
            }
        }
    }

    @Test
    public void testPDFFromMarkdown() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));
        File md = new File(Defaults.WORKING_DIR, "sample/manual/user-manual.md");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new MarkdownRequestHandler());
        builder.append(new ReplacerRequestHandler(Defaults.ENVIRONMENT));
        builder.append(new FoRequestHandler(template, false));
        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        File output = new File(Defaults.TARGET, md.getName() + ".pdf");
        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(md)) {
                handler.handleRequest(istream, ostream, md.getParentFile());
            }
        }
    }

    @Test
    public void testPDFFromAsciiDoc() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new AsciiDocRequestHandler());
//        builder.append(new ReplacerRequestHandler(Defaults.ENVIRONMENT));
        builder.append(new FoRequestHandler(template, false));
        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        File output = new File(Defaults.TARGET, adoc.getName() + ".pdf");
        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(adoc)) {
                handler.handleRequest(istream, ostream, adoc.getParentFile());
            }
        }
    }

    @Test
    public void testPdfFromFO() throws Exception {
        Template template = Template.parse(":default.ui.xml", new File("."));

        File output = new File(Defaults.TARGET, "test" + ".pdf");
        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = DataUri.toInputStream(Defaults.TARGET, ":test.fo.xml")) {
                RequestStreamHandler handler = new PdfRenderer(template);
                handler.handleRequest(istream, ostream, Defaults.TARGET);
            }
        }
    }
}