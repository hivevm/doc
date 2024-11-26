// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.pdf;

import java.util.List;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.io.InternalResourceResolver;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontCollection;
import org.apache.fop.render.intermediate.IFContext;
import org.apache.fop.render.intermediate.IFDocumentHandler;
import org.apache.fop.render.intermediate.IFDocumentHandlerConfigurator;
import org.apache.fop.render.pdf.PDFDocumentHandler;
import org.apache.fop.render.pdf.PDFDocumentHandlerMaker;
import org.apache.fop.render.pdf.PDFRendererConfig.PDFRendererConfigParser;
import org.apache.fop.render.pdf.PDFRendererConfigurator;

/**
 * The {@link PdfRenderer} implements an {@link PDFDocumentHandlerMaker} with custom
 * {@link EmbedFontInfo}.
 */
class PdfRenderer extends PDFDocumentHandlerMaker {

  private final List<EmbedFontInfo> embedFonts;

  /**
   * Constructs an instance of {@link PdfRenderer}.
   *
   * @param embedFonts
   */
  public PdfRenderer(List<EmbedFontInfo> embedFonts) {
    this.embedFonts = embedFonts;
  }

  /**
   * Creates an {@link IFDocumentHandler}.
   *
   * @param context
   */
  @Override
  public IFDocumentHandler makeIFDocumentHandler(IFContext context) {
    PDFDocumentHandler handler = new PDFDocumentHandler(context) {

      @Override
      public IFDocumentHandlerConfigurator getConfigurator() {
        return new CustomPDFRendererConfigurator(getUserAgent());
      }
    };

    FOUserAgent userAgent = context.getUserAgent();
    if (userAgent.isAccessibilityEnabled()) {
      userAgent.setStructureTreeEventHandler(handler.getStructureTreeEventHandler());
    }
    return handler;
  }

  /**
   * The {@link CustomPDFRendererConfigurator} class.
   */
  private class CustomPDFRendererConfigurator extends PDFRendererConfigurator {

    /**
     * Constructs an instance of {@link CustomPDFRendererConfigurator}.
     *
     * @param userAgent
     */
    public CustomPDFRendererConfigurator(FOUserAgent userAgent) {
      super(userAgent, new PDFRendererConfigParser());
    }

    @Override
    protected final FontCollection getCustomFontCollection(InternalResourceResolver resolver, String mimeType)
        throws FOPException {
      return createCollectionFromFontList(resolver, PdfRenderer.this.embedFonts);
    }
  }
}
