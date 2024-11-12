// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.pdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link PdfFont} class.
 */
public class PdfFont {

  private final String       name;
  private final List<Metric> metrics = new ArrayList<>();

  /**
   * Constructs an instance of {@link PdfFont}.
   *
   * @param name
   */
  public PdfFont(String name) {
    this.name = name;
  }

  /**
   * Gets the font name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Gets the collection of metrics.
   */
  final Iterable<Metric> getMetrics() {
    return this.metrics;
  }

  /**
   * Sets the {@link URI} to the font.
   */
  public final void addMetric(URI uri, boolean bold, boolean italic) {
    this.metrics.add(new Metric(uri, bold, italic));
  }

  /**
   * The {@link Metric} class.
   */
  class Metric {

    public final URI     uri;
    public final boolean bold;
    public final boolean italic;

    /**
     * Constructs an instance of {@link Metric}.
     *
     * @param uri
     * @param bold
     * @param italic
     */
    private Metric(URI uri, boolean bold, boolean italic) {
      this.uri = uri;
      this.bold = bold;
      this.italic = italic;
    }
  }
}
