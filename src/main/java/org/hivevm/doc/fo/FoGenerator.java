// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.*;
import org.hivevm.util.xml.StAX;
import org.hivevm.util.xml.XmlBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * The {@link FoGenerator} class.
 */
public class FoGenerator {

    private final Template template;

    /**
     * Sets the filename
     */
    public FoGenerator(Template template) {
        this.template = template;
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

        FoBlock block = container.addBlock();
        if (page.background.isImage()) {
            FoExternalGraphic watermark = new FoExternalGraphic(page.background.get(), content.getBuilder());
            watermark.setContentWidth(page.pageWidth());
            watermark.setContentHeight(page.pageHeight());
            block.addNode(watermark);
        } else
            container.setBackgroundColor(page.background.get());
    }

    private static void renderRegion(Page page, PageRegion region, FoPageSequence node, Properties properties) {
        FoStaticContent content = new FoStaticContent(region.getName(), node.getBuilder());

        // Render Page fixed layouts (Watermarks)
        if (region.getRegion() == PageRegion.Region.TOP) {
            if (page.background != null)
                renderWatermark(page, content);

            FoRendererFixed renderer = new FoRendererFixed();
            page.items.forEach(i -> i.accept(renderer, content));
        }

        region.forEachItem(p -> renderAbsolute(p, content, properties));

        if (content.hasChildren()) {
            node.addNode(content);
        }
    }

    /**
     * Renders the regions of the page.
     *
     * @param page
     * @param sequence
     * @param properties
     */
    private static void renderPage(Page page, FoPageSequence sequence, Properties properties) {
        page.forEachRegion(r -> renderRegion(page, r, sequence, properties));
    }

    /**
     * Build the markdown based book.
     */
    public final FoContext createContext(XmlBuilder builder) throws IOException {
        String fontText = template.getFonts()
                .stream()
                .filter(f -> f.getKind() == TemplateFont.Kind.NORMAL)
                .findFirst().get().getName();
        String fontCode = template.getFonts()
                .stream()
                .filter(f -> f.getKind() == TemplateFont.Kind.MONOSPACE)
                .findFirst().get().getName();


//        builder.addNamespace("fo", "http://www.w3.org/1999/XSL/Format");
//        builder.addNamespace("fox", "http://xmlgraphics.apache.org/fop/extensions");

        FoRoot root = new FoRoot(fontText, builder);
        root.set("xmlns:fo", "http://www.w3.org/1999/XSL/Format");
        root.set("xmlns:fox", "http://xmlgraphics.apache.org/fop/extensions");

        template.forEachPage(p -> {
            FoSimplePageMaster simple = new FoSimplePageMaster(p.getName(), builder);
            simple.setMarginTop(p.marginTop());
            simple.setMarginLeft(p.marginLeft());
            simple.setMarginRight(p.marginRight());
            simple.setMarginBottom(p.marginBottom());
            simple.setPageSize(p.pageWidth(), p.pageHeight());

            FoRegion content = simple.setBodyRegion("region-body");
            content.setMarginTop(p.paddingTop());
            content.setMarginLeft(p.paddingLeft());
            content.setMarginRight(p.paddingRight());
            content.setMarginBottom(p.paddingBottom());
            content.setColumns(p.columnCount(), p.columnGap());

            p.forEachRegion(r -> (switch (r.getRegion()) {
                case TOP -> simple.addRegionBefore(r.getName());
                case LEFT -> simple.addRegionStart(r.getName());
                case RIGHT -> simple.addRegionEnd(r.getName());
                case BOTTOM -> simple.addRegionAfter(r.getName());
            }).setExtent(r.getExtent()).setColumns(r.columnCount(), r.columnGap()));

            root.getLayouts().addNode(simple);
        });

        Map<String, BiConsumer<FoPageSequence, Properties>> pages = new HashMap<>();
        template.forEachPageSet(s -> {
            FoPageSequenceMaster master = new FoPageSequenceMaster(s.getName(), builder);
            s.forEachPage((p, m) -> master.addPage(p.getName(), m));
            pages.put(s.getName(),
                    (sequence, properties) ->
                            s.forEachPage((p, m) -> renderPage(p, sequence, properties)));
            root.getLayouts().addNode(master);
        });

        return new FoContext(root, template, pages, fontCode);
    }

    /**
     * Build the markdown based book.
     */
    public final InputStream generate(Document document) throws IOException {
        StringWriter writer = new StringWriter();
//        try (XmlBuilder builder = new XmlBuilder(StAX.createWriter(writer, 2))) {
//            FoContext context = createContext(builder);
//            document.accept(new FoDocumentRenderer(template), context);
//            printer.write(context.getRoot().build());
//        }
//        catch (XMLStreamException e) {
//            throw new IOException(e);
//        }
        try (PrintWriter printer = new PrintWriter(writer)) {
            FoContext context = createContext(null);
            document.accept(new FoDocumentRenderer(template), context);
            printer.write(context.getRoot().build());
        }
        return new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
    }
}
