// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.net.URL;
import java.util.*;

/**
 * The {@link TemplateBuilder} class.
 */
class TemplateBuilder {

    private String width;
    private String height;


    private final BookSymbols       symbols  = new BookSymbols();
    private final Set<TemplateFont> fonts    = new LinkedHashSet<>();
    private final Set<String>       keywords = new LinkedHashSet<>();

    private final List<PageStyle>   styles   = new ArrayList<>();
    private final Map<String, Page> pages    = new LinkedHashMap<>();
    private final Set<PageSet>      pageSets = new LinkedHashSet<>();

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
     * Gets the {@link BookSymbols}.
     */
    public final BookSymbols getSymbols() {
        return this.symbols;
    }

    public final TemplateBuilder addFontSymbols(String name, URL codepoints) {
        this.symbols.registerSymbols(name, codepoints);
        return this;
    }

    public final TemplateBuilder addStyle(PageStyle style) {
        this.styles.add(style);
        return this;
    }

    /**
     * Get {@link Page} by name.
     *
     * @param name
     */
    public final Page getTemplate(String name) {
        return this.pages.get(name);
    }

    /**
     * Sets the default size.
     *
     * @param width
     * @param height
     */
    public final TemplateBuilder setSize(String width, String height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Add a named {@link Page}.
     *
     * @param name
     */
    public final Page addPage(String name) {
        Page page = new Page(name);
        this.pages.put(name, page);
        return page;
    }

    /**
     * Add a named {@link Page}.
     *
     * @param name
     */
    public final PageSet addPageSet(String name) {
        PageSet pageSet = new PageSet(name);
        this.pageSets.add(pageSet);
        return pageSet;
    }

    /**
     * Adds a font by name.
     *
     * @param name
     * @param kind
     */
    public final TemplateFont addFontName(String name, TemplateFont.Kind kind) {
        TemplateFont font = new TemplateFont(name, kind);
        this.fonts.add(font);
        return font;
    }

    /**
     * Creates the {@link Template}.
     */
    public final Template build() {
        return new Template(width, height, symbols, fonts, styles, keywords, pages.values(), pageSets);
    }
}
