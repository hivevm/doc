// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.fo.writer.FoLayoutMasterSet;
import org.hivevm.doc.fo.writer.FoPageSequenceMaster;
import org.hivevm.doc.fo.writer.FoRegion;
import org.hivevm.doc.fo.writer.FoRoot;
import org.hivevm.doc.fo.writer.FoSimplePageMaster;
import org.hivevm.doc.template.Page;
import org.hivevm.doc.template.PageMatch;
import org.hivevm.doc.template.PageSet;
import org.hivevm.doc.template.Template;
import org.hivevm.util.xml.StAX;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoGenerator} class.
 */
public class FoGenerator {

    private final Template template;
    private final boolean  isFormatted;

    /**
     * Sets the filename
     */
    public FoGenerator(Template template) {
        this(template, false);
    }

    /**
     * Sets the filename
     */
    public FoGenerator(Template template, boolean isFormatted) {
        this.template = template;
        this.isFormatted = isFormatted;
    }

    /**
     * Generates the layout.
     */
    private void generateLayout(FoRoot root) {
        try (FoLayoutMasterSet layout = root.createLayoutMasteSet()) {
            for (Page page : template.pages()) {
                FoSimplePageMaster simple = layout.createPageMaster(page.getName());
                simple.setMarginTop(page.marginTop())
                    .setMarginLeft(page.marginLeft())
                    .setMarginRight(page.marginRight())
                    .setMarginBottom(page.marginBottom())
                    .setPageSize(page.pageWidth(), page.pageHeight());

                try (FoRegion content = simple.createBodyRegion("region-body")) {
                    content.setMarginTop(
                            Stream.of(page.paddingTop(), page.getRegion("top").getExtent())
                                .filter(v -> v != null).collect(Collectors.joining(" + ")))
                        .setMarginLeft(
                            Stream.of(page.paddingLeft(), page.getRegion("left").getExtent())
                                .filter(v -> v != null).collect(Collectors.joining(" + ")))
                        .setMarginRight(
                            Stream.of(page.paddingRight(), page.getRegion("right").getExtent())
                                .filter(v -> v != null).collect(Collectors.joining(" + ")))
                        .setMarginBottom(
                            Stream.of(page.paddingBottom(), page.getRegion("bottom").getExtent())
                                .filter(v -> v != null).collect(Collectors.joining(" + ")))
                        .setColumns(page.columnCount(), page.columnGap());
                }

                page.forEachRegion(r -> (switch (r.getRegion()) {
                    case BEFORE -> simple.createRegionBefore(r.getName());
                    case START -> simple.createRegionStart(r.getName());
                    case END -> simple.createRegionEnd(r.getName());
                    case AFTER -> simple.createRegionAfter(r.getName());
                }).setExtent(r.getExtent()));
            }

            for (PageSet s : template.pageSet()) {
                FoPageSequenceMaster master = layout.createPageSequence(s.getName());
                for (Map.Entry<Page, PageMatch> e : s.pages()) {
                    master.addPage(e.getKey().getName(), e.getValue());
                }
            }
        }
    }

    /**
     * Build the markdown based book.
     */
    public final void generate(Document document, OutputStream ostream) throws IOException {
        try (var builder = new XmlBuilder(
            StAX.formatted(StAX.createWriter(ostream), isFormatted ? 2 : 0))) {
            builder.addNamespace("fo", "http://www.w3.org/1999/XSL/Format");
            builder.addNamespace("fox", "http://xmlgraphics.apache.org/fop/extensions");
            builder.setNamespace("http://www.w3.org/1999/XSL/Format");

            try (var root = new FoRoot(builder)) {
                root.setFontFamily(template.getFont());
                root.setFontSize("10pt");
                generateLayout(root);
                document.accept(new FoDocumentRenderer(), new FoContext(root, template));
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
