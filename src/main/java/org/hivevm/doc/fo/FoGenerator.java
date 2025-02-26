// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.*;
import org.hivevm.util.xml.StAX;
import org.hivevm.util.xml.XmlBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * The {@link FoGenerator} class.
 */
public class FoGenerator {

    private final Template template;
    private final boolean  isFormatted;

    /**
     * Sets the filename
     */
    public FoGenerator(Template template, boolean isFormatted) {
        this.template = template;
        this.isFormatted = isFormatted;
    }

    private static void renderAbsolute(PageColumn panel, FoStaticContent node, Properties properties) {
        FoBlockContainer container = node.blockContainer(FoBlockContainer.Position.Absolute);
        container.setPosition(panel.getLeft(), panel.getRight(), panel.getTop(), panel.getBottom());

        container.setColor(panel.color);
        if (panel.background != null) {
            if (panel.background.isImage()) {
                container.setBackground(panel.background.get(), "no-repeat");
            } else {
                container.setBackgroundColor(panel.background.get());
            }
        }
        container.setFontSize(panel.fontSize);
        container.setFontStyle(panel.fontStyle);
        container.setFontWeight(panel.fontWeight);
        container.setFontFamily(panel.fontFamily);
        container.setTextAlign(panel.textAlign);
        container.setLineHeight(panel.lineHeight);

        FoRendererAbsolute renderer = new FoRendererAbsolute(properties);
        panel.items.forEach(c -> c.accept(renderer, container.addBlock()));
    }

    private static void renderWatermark(Page page, FoStaticContent content) {
        FoBlockContainer container = content.blockContainer(FoBlockContainer.Position.Fixed);
        container.setPositionTop("0");
        container.setPositionLeft("0");
        container.setPositionRight("0");
        container.setPositionBottom("0");

        if (page.background.isImage()) {
            FoExternalGraphic watermark = new FoExternalGraphic(page.background.get(), container.addBlock());
            watermark.setContentWidth(page.pageWidth());
            watermark.setContentHeight(page.pageHeight());
        } else {
            container.setBackgroundColor(page.background.get());
            container.addBlock();
        }
    }

    private static void renderRegion(Page page, PageRegion region, FoPageSequence node, Properties properties) {
        if (region.hasChildren() || (region.getRegion() == PageRegion.Region.TOP && page.background != null)) {
            FoStaticContent content = new FoStaticContent(region.getName(), node);

            // Render Page fixed layouts (Watermarks)
            if (region.getRegion() == PageRegion.Region.TOP) {
                if (page.background != null)
                    renderWatermark(page, content);

                FoRendererFixed renderer = new FoRendererFixed();
                page.items.forEach(i -> i.accept(renderer, content));
            }

            region.forEachItem(p -> renderAbsolute(p, content, properties));
        }
    }

    /**
     * Renders the regions of the page.
     */
    private static void renderPage(Page page, FoPageSequence sequence, Properties properties) {
        page.forEachRegion(r -> renderRegion(page, r, sequence, properties));
    }

    /**
     * Build the markdown based book.
     */
    public final Map<String, BiConsumer<FoPageSequence, Properties>> createPages(FoRoot root) throws IOException {
        FoNode layout = root.createLayout();
        template.forEachPage(p -> {
            FoSimplePageMaster simple = new FoSimplePageMaster(p.getName(), layout);
            simple.setMarginTop(p.marginTop());
            simple.setMarginLeft(p.marginLeft());
            simple.setMarginRight(p.marginRight());
            simple.setMarginBottom(p.marginBottom());
            simple.setPageSize(p.pageWidth(), p.pageHeight());

            FoRegion content = simple.createBodyRegion("region-body");
            content.setMarginTop(p.paddingTop());
            content.setMarginLeft(p.paddingLeft());
            content.setMarginRight(p.paddingRight());
            content.setMarginBottom(p.paddingBottom());
            content.setColumns(p.columnCount(), p.columnGap());

            p.forEachRegion(r -> (switch (r.getRegion()) {
                case TOP -> simple.createRegionBefore(r.getName());
                case LEFT -> simple.createRegionStart(r.getName());
                case RIGHT -> simple.createRegionEnd(r.getName());
                case BOTTOM -> simple.createRegionAfter(r.getName());
            }).setExtent(r.getExtent()).setColumns(r.columnCount(), r.columnGap()));
        });

        Map<String, BiConsumer<FoPageSequence, Properties>> pages = new HashMap<>();
        template.forEachPageSet(s -> {
            FoPageSequenceMaster master = new FoPageSequenceMaster(s.getName(), layout);
            s.forEachPage((p, m) -> master.addPage(p.getName(), m));
            pages.put(s.getName(), (sequence, properties) ->
                    s.forEachPage((p, m) -> renderPage(p, sequence, properties)));
        });

        return pages;
    }

    /**
     * Build the markdown based book.
     */
    public final InputStream generate(Document document) throws IOException {
        StringWriter writer = new StringWriter();

        try (XmlBuilder builder = new XmlBuilder(StAX.createWriter(writer, isFormatted ? 2 : 0))) {
            builder.addNamespace("fo", "http://www.w3.org/1999/XSL/Format");
            builder.addNamespace("fox", "http://xmlgraphics.apache.org/fop/extensions");
            builder.setNamespace("http://www.w3.org/1999/XSL/Format");

            String fontText = template.getFonts()
                    .stream()
                    .filter(f -> f.getKind() == TemplateFont.Kind.NORMAL)
                    .findFirst().get().getName();
            String fontCode = template.getFonts()
                    .stream()
                    .filter(f -> f.getKind() == TemplateFont.Kind.MONOSPACE)
                    .findFirst().get().getName();

            FoRoot root = new FoRoot(fontText, builder);
            String tocId = Long.toHexString(new Random().nextLong());
            Map<String, BiConsumer<FoPageSequence, Properties>> pageSet = createPages(root);
            FoContext context = new FoContext();

            // Renders the Bookmark
            document.accept(new FoBookmarkRenderer(FoDocumentRenderer.BOOK_ID, tocId), root.createBookmark());
            // Renders the Document
            document.accept(new FoDocumentRenderer(root, template, pageSet, fontCode), root);
            // Table of Content
            document.accept(new FoTocRenderer(template, tocId, pageSet), root);

            root.close();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        return new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
    }
}
