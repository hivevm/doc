// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fluid;

import org.hivevm.doc.pdf.PdfFont;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The {@link FluidBuilder} class.
 */
public abstract class FluidBuilder {

  private final File workingDir;

  private String     width;
  private String     height;


  private final FluidSymbols         symbols  = new FluidSymbols();
  private final Map<String, PdfFont> fonts    = new LinkedHashMap<>();
  private final Set<String>          keywords = new LinkedHashSet<>();

  /**
   * Constructs an instance of {@link FluidBuilder}.
   *
   * @param workingDir
   */
  public FluidBuilder(File workingDir) {
    this.workingDir = workingDir;
  }

  /**
   * Gets the working directory.
   */
  public final File getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Gets the default width.
   */
  public final String getWidth() {
    return this.width;
  }

  /**
   * Gets the default height.
   */
  public final String getHeight() {
    return this.height;
  }

  /**
   * Gets the keywords.
   */
  public final Set<String> getKeywords() {
    return this.keywords;
  }

  /**
   * Gets the {@link FluidSymbols}.
   */
  public final FluidSymbols getSymbols() {
    return this.symbols;
  }

  /**
   * Gets the {@link #fonts}.
   */
  protected final Map<String, PdfFont> getFonts() {
    return this.fonts;
  }

  /**
   * Sets the default size.
   *
   * @param width
   * @param height
   */
  public final FluidBuilder setSize(String width, String height) {
    this.width = width;
    this.height = height;
    return this;
  }

  /**
   * Add a named {@link FluidTemplate}.
   *
   * @param name
   */
  public abstract FluidTemplate addTemplate(String name);

  /**
   * Adds a font by name.
   *
   * @param name
   */
  public final PdfFont addFontName(String name) {
    PdfFont font = new PdfFont(name);
    this.fonts.put(name, font);
    return font;
  }

  public final void forEachFont(Consumer<PdfFont> consumer) {
    this.fonts.values().forEach(f -> consumer.accept(f));
  }
}
