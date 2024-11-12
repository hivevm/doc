// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.pdf;


import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.Font;
import org.apache.fop.fonts.FontTriplet;
import org.apache.fop.fonts.FontUris;
import org.apache.xmlgraphics.util.MimeConstants;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

/**
 * The {@link PdfGenerator} class.
 */
public class PdfGenerator {

  private final String pageWidth;
  private final String pageHeight;
  private final File   workingDir;


  private final List<EmbedFontInfo> embedFonts = new ArrayList<>();

  /**
   * Constructs an instance of {@link PdfGenerator}.
   *
   * @param pageWidth
   * @param pageHeight
   * @param workingDir
   */
  public PdfGenerator(String pageWidth, String pageHeight, File workingDir) {
    this.pageWidth = pageWidth;
    this.pageHeight = pageHeight;
    this.workingDir = workingDir;
  }

  /**
   * Add a {@link PdfFont} to the renderer.
   *
   * @param font
   */
  public final void addFont(PdfFont font) {
    for (PdfFont.Metric metric : font.getMetrics()) {
      int weight = metric.bold ? Font.WEIGHT_BOLD : Font.WEIGHT_NORMAL;
      String style = metric.italic ? Font.STYLE_ITALIC : Font.STYLE_NORMAL;

      FontUris fontUri = new FontUris(metric.uri, URI.create(metric.uri.toString() + ".xml"));
      List<FontTriplet> triplets = Collections.singletonList(new FontTriplet(font.getName(), style, weight));
      this.embedFonts.add(new EmbedFontInfo(fontUri, true, false, triplets, null));
    }
  }

  /**
   * Writes the FO stream to PDF.
   *
   * @param source
   * @param target
   */
  public final void write(InputStream source, OutputStream target) throws Exception {
    FopFactoryBuilder builder = new FopFactoryBuilder(this.workingDir.toURI(), new FontResolver());
    builder.setStrictFOValidation(true).setStrictUserConfigValidation(true);
    builder.setSourceResolution(72).setTargetResolution(72);
    builder.setPageWidth(this.pageWidth).setPageHeight(this.pageHeight);
    builder.setPreferRenderer(true);

    FopFactory factory = builder.build();
    factory.getRendererFactory().addDocumentHandlerMaker(new PdfRenderer(this.embedFonts));


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
    Fop fop = factory.newFop(MimeConstants.MIME_PDF, foUserAgent, target);

    // Step 3: Setup JAXP using identity transformer
    Transformer transformer = TransformerFactory.newInstance().newTransformer();

    // Step 4: Setup input and output for XSLT transformation
    Result result = new SAXResult(fop.getDefaultHandler());

    // Step 5: Start XSLT transformation and FOP processing
    transformer.transform(new StreamSource(source), result);
  }
}