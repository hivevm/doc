// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link TemplateFont} class.
 */
public class TemplateFont {

    private final String       name;
    private final Kind         kind;
    private final List<Metric> metrics = new ArrayList<>();


    /**
     * Constructs an instance of {@link TemplateFont}.
     *
     * @param name
     * @param kind
     */
    public TemplateFont(String name, Kind kind) {
        this.name = name;
        this.kind = kind;
    }

    /**
     * Gets the font name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the kind.
     */
    public final Kind getKind() {
        return this.kind;
    }

    /**
     * Gets the collection of metrics.
     */
    public final Iterable<Metric> getMetrics() {
        return this.metrics;
    }

    /**
     * Sets the {@link URI} to the font.
     */
    public final void addMetric(URI uri, boolean bold, boolean italic) {
        this.metrics.add(new Metric(uri, bold, italic));
    }

    public enum Kind {
        NORMAL,
        MONOSPACE,
        CODEPOINT
    }

    /**
     * The class defines the metric of a font
     *
     * @param uri
     * @param bold
     * @param italic
     */
    public record Metric(URI uri, boolean bold, boolean italic) {
    }
}
