// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.pdf;

import org.apache.fop.apps.*;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.Font;
import org.apache.fop.fonts.FontTriplet;
import org.apache.fop.fonts.FontUris;
import org.apache.xmlgraphics.util.MimeConstants;
import org.hivevm.doc.template.Template;
import org.hivevm.doc.template.TemplateFont;
import org.hivevm.util.lambda.RequestStreamHandler;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfRenderer implements RequestStreamHandler {

    private final Template template;

    public PdfRenderer(Template template) {
        this.template = template;
    }

    protected final Template getTemplate() {
        return template;
    }

    public void handleRequest(InputStream input, OutputStream output, File context) throws IOException {
        List<EmbedFontInfo> embedded = new ArrayList<>();
        template.getFonts().forEach(f -> {
            for (TemplateFont.Metric metric : f.getMetrics()) {
                int weight = metric.bold() ? Font.WEIGHT_BOLD : Font.WEIGHT_NORMAL;
                String style = metric.italic() ? Font.STYLE_ITALIC : Font.STYLE_NORMAL;

                FontUris fontUri = new FontUris(metric.uri(), URI.create(metric.uri().toString() + ".xml"));
                List<FontTriplet> triplets = Collections.singletonList(
                        new FontTriplet(f.getName(), style, weight));
                embedded.add(new EmbedFontInfo(fontUri, true, false, triplets, null));
            }
        });

        FopFactoryBuilder builder = new FopFactoryBuilder(context.toURI(), new FontResolver());
        builder.setStrictFOValidation(true).setStrictUserConfigValidation(true);
        builder.setSourceResolution(72).setTargetResolution(72);
        builder.setPageWidth(getTemplate().getWidth()).setPageHeight(getTemplate().getHeight());
        builder.setPreferRenderer(true);

        FopFactory factory = builder.build();
        factory.getRendererFactory().addDocumentHandlerMaker(new PdfHandler(embedded));

        // a user agent is needed for transformation
        FOUserAgent foUserAgent = factory.newFOUserAgent();
        // foUserAgent.getRendererOptions().put("encryption-length", 128);
        // foUserAgent.getRendererOptions().put("user-password", "test");
        // foUserAgent.getRendererOptions().put("owner-password", "test");
        // foUserAgent.getRendererOptions().put("noprint", "true");
        // foUserAgent.getRendererOptions().put("nocopy", "true");
        // foUserAgent.getRendererOptions().put("noedit", "true");
        // foUserAgent.getRendererOptions().put("noannotations", "true");
        // foUserAgent.getRendererOptions().put("nofillinforms", "true");
        // foUserAgent.getRendererOptions().put("noaccesscontent", "true");
        // foUserAgent.getRendererOptions().put("noassembledoc", "true");
        // foUserAgent.getRendererOptions().put("noprinthq", "true");

        // Step 1: Construct a FopFactory by specifying a reference to the
        // configuration file (reuse if you plan to render multiple documents!)

        // Step 2: Construct fop with desired output format
        // MIME_PDF = "application/pdf";
        try {
            Fop fop = factory.newFop(MimeConstants.MIME_PDF, foUserAgent, output);

            // Step 3: Setup JAXP using identity transformer
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            // Step 4: Setup input and output for XSLT transformation
            Result result = new SAXResult(fop.getDefaultHandler());

            // Step 5: Start XSLT transformation and FOP processing
            transformer.transform(new StreamSource(input), result);
        } catch (FOPException | TransformerException e) {
            throw new IOException(e);
        }
    }
}
