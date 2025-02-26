// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import org.hivevm.doc.adoc.AsciiDocRequestHandler;
import org.hivevm.doc.api.DocumentParser;
import org.hivevm.doc.fo.FoGenerator;
import org.hivevm.doc.fo.FoRequestHandler;
import org.hivevm.doc.fo.pdf.PdfRenderer;
import org.hivevm.doc.md.MarkdownRequestHandler;
import org.hivevm.doc.template.Template;
import org.hivevm.document.TestContext;
import org.hivevm.util.lambda.RequestStreamBuilder;
import org.hivevm.util.lambda.RequestStreamHandler;
import org.junit.jupiter.api.Test;

/**
 * The {@link PdfTest} class.
 */
public class PdfTest {

    @Test
    public void testPDF() throws Exception {
        var template = Template.getDefault();
        var target = TestContext.defaults().target();
        var markdown = new File("README.md").getAbsoluteFile();
        var foFile = new File(target, markdown.getName() + ".fo");
        var pdfFile = new File(target, markdown.getName() + ".pdf");

        try (var reader = new BufferedReader(new FileReader(markdown))) {
            var text = String.join("\n", reader.lines().toList());
            var document = DocumentParser.parse(text);
            var generator = new FoGenerator(template);
            try (var buffer = new ByteArrayOutputStream();
                var foStream = new FileOutputStream(foFile);
                var pdfStream = new FileOutputStream(pdfFile)) {
                generator.generate(document, buffer);
                var renderer = new PdfRenderer(template);
                foStream.write(buffer.toByteArray());
                renderer.handleRequest(new ByteArrayInputStream(buffer.toByteArray()), pdfStream,
                    new File("."));
            }
        }
    }

    @Test
    public void testFo() throws Exception {
        var template = Template.getDefault();
        var target = TestContext.defaults().target();
        var markdown = new File("README.md").getAbsoluteFile();
        var foFile = new File(target, markdown.getName() + ".fo");

        var builder = new RequestStreamBuilder();
        builder.append(new MarkdownRequestHandler());
        builder.append(new FoRequestHandler(template, true));
//        builder.append(new PdfRenderer(template));
        var handler = builder.build();

        try (var iStream = new FileInputStream(markdown);
            var foStream = new FileOutputStream(foFile)) {
            handler.handleRequest(iStream, foStream, markdown.getParentFile());
        }
    }

    @Test
    public void testPDFFromMarkdown() throws Exception {
        Template template = Template.getDefault();
        var context = TestContext.defaults();
        File md = new File(context.workingDir(), "sample/manual/developer-manual.md");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new MarkdownRequestHandler());
        builder.append(new FoRequestHandler(template, false));
        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        File output = new File(context.target(), md.getName() + ".pdf");
        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(md)) {
                handler.handleRequest(istream, ostream, md.getParentFile());
            }
        }
    }

    @Test
    public void testPDFFromAsciiDoc() throws Exception {
        Template template = Template.getDefault();
        var target = TestContext.defaults().target();
        File adoc = new File("adoc/HandbuchDerEntwicklungsabteilung.adoc");

        RequestStreamBuilder builder = new RequestStreamBuilder();
        builder.append(new AsciiDocRequestHandler());
//        builder.append(new ReplacerRequestHandler(Defaults.ENVIRONMENT));
        builder.append(new FoRequestHandler(template, false));
        builder.append(new PdfRenderer(template));
        RequestStreamHandler handler = builder.build();

        File output = new File(target, adoc.getName() + ".pdf");
        try (FileOutputStream ostream = new FileOutputStream(output)) {
            try (InputStream istream = new FileInputStream(adoc)) {
                handler.handleRequest(istream, ostream, adoc.getParentFile());
            }
        }
    }
}