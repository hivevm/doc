// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.pdf;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.fonts.apps.TTFReader;
import org.apache.fop.fonts.truetype.FontFileReader;
import org.apache.fop.fonts.truetype.TTFFile;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;

/**
 * The {@link FontResolver} implements a {@link ResourceResolver} which supports dynamic generation
 * of font-metrics.
 */
class FontResolver implements ResourceResolver {

  /**
   * Resolves the resource from an {@link URI} and supports dynamic font-metric generation.
   *
   * @param uri
   */
  @Override
  public final Resource getResource(URI uri) throws IOException {
    if (uri.toString().startsWith("/")) {
      return new Resource(new FileInputStream(uri.toString()));
    } else if (uri.toString().endsWith("ttf.xml")) {
      String ttf = uri.toString().substring(0, uri.toString().length() - 4);
      return new Resource(FontResolver.createFontMetric(URI.create(ttf).toURL()));
    }
    return new Resource(uri.toURL().openStream());
  }

  @Override
  public final OutputStream getOutputStream(URI uri) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * Load the font metric as {@link InputStream}.
   *
   * @param url
   */
  private static InputStream createFontMetric(URL url) throws IOException {
    try (InputStream iStream = url.openStream()) {
      try (ByteArrayOutputStream oStream = new ByteArrayOutputStream()) {
        FontResolver.createFontMetric(iStream, oStream);
        return new Resource(new ByteArrayInputStream(oStream.toByteArray()));
      }
    }
  }

  /**
   * Create a Font metric from a font definition (like TTF).
   *
   * @param inputStream
   * @param outputStream
   */
  public static void createFontMetric(InputStream inputStream, OutputStream outputStream) throws IOException {
    TTFFile ttfFile = new TTFFile(true, true);
    ttfFile.readFont(new FontFileReader(inputStream), null);
    if (ttfFile.isCFF()) {
      throw new UnsupportedOperationException("OpenType fonts with CFF data are not supported, yet");
    }

    TTFReader reader = new TTFReader();
    DOMSource source = new DOMSource(reader.constructFontXML(ttfFile, null, null, null, null, true, null));
    try (OutputStream bufferedStream = new BufferedOutputStream(outputStream)) {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(source, new StreamResult(bufferedStream));
    } catch (TransformerException e) {
      throw new IOException(e);
    }
  }
}
