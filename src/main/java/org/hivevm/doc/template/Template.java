// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import org.hivevm.util.DataUri;
import org.hivevm.util.xml.StAX;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Defines a Template definition for a collection of pages.
 */
public class Template {

    private final String width;
    private final String height;

    private final Set<TemplateFont> fonts;
    private final Set<String>       keywords;
    private final BookSymbols       symbols;
    private final List<PageStyle>   styles;

    private final Collection<Page>    pages;
    private final Collection<PageSet> pageSets;

    public Template(String width, String height, BookSymbols symbols, Set<TemplateFont> fonts,
                    List<PageStyle> styles, Set<String> keywords, Collection<Page> pages, Collection<PageSet> pageSets) {
        this.width = width;
        this.height = height;
        this.symbols = symbols;
        this.fonts = fonts;
        this.styles = styles;
        this.keywords = keywords;
        this.pages = pages;
        this.pageSets = pageSets;
    }

    /**
     * Gets the default width.
     */
    public final String getWidth() {
        return width;
    }

    /**
     * Gets the default height.
     */
    public final String getHeight() {
        return height;
    }

    /**
     * Gets the {@link TemplateFont}'s.
     */
    public final Set<TemplateFont> getFonts() {
        return fonts;
    }

    /**
     * Gets the keywords.
     */
    public final Set<String> getKeywords() {
        return keywords;
    }

    /**
     * Gets the {@link BookSymbols}.
     */
    public final BookSymbols getSymbols() {
        return symbols;
    }

    /**
     * Gets the {@link BookSymbols}.
     */
    public final PageStyle getStyle(String name) {
        return styles.stream().filter(s -> s.matches(name)).findFirst().orElse(null);
    }

    public final void forEachPage(Consumer<Page> action) {
        pages.forEach(action);
    }

    public final void forEachPageSet(Consumer<PageSet> action) {
        pageSets.forEach(action);
    }

    /**
     * Parses the configuration from the file.
     */
    public static Template parse(String config, File workingDir) throws IOException {
        TemplateBuilder template = new TemplateBuilder();
        try (InputStream stream = DataUri.toInputStream(workingDir, config)) {
            StAX.parse(stream, new TemplateParser(template, workingDir));
        }
        return template.build();
    }
}
