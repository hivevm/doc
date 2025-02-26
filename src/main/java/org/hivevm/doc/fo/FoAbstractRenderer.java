// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.fo.writer.*;
import org.hivevm.doc.template.PageStyle;
import org.hivevm.doc.template.Template;

import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * The {@link FoAbstractRenderer} class.
 */
abstract class FoAbstractRenderer<C> implements DocumentVisitor<C> {

    private final Template template;

    private final Map<String, BiConsumer<FoPageSequence, Properties>> pageSet;


    /**
     * Creates an instance of {@link FoAbstractRenderer} for the provided template.
     */
    public FoAbstractRenderer(Template template,
                              Map<String, BiConsumer<FoPageSequence, Properties>> pageSet) {
        this.template = template;
        this.pageSet = pageSet;
    }

    /**
     * Iterates over each symbol.
     */
    protected final String forEachSymbol(String text, BiFunction<String, String, String> function) {
        return this.template.getSymbols().forEach(text, function);
    }

    /**
     * Set the supplier that creates a {@link FoFlow}.
     */
    protected final FoFlow createPageSequence(String id, String name, FoRoot root, boolean initial, Properties properties) {
        FoPageSequence page = root.addPageSequence(name);
        page.setLanguage("en").setInitialPageNumber(initial ? "1" : "auto");
        page.setId(id);

        switch (name) {
            case Fo.PAGESET_CHAPTER:
                page.setFormat("1");
                break;

            default:
                page.setFormat("I");
        }

        // Foot separator
        FoStaticContent content = new FoStaticContent("xsl-footnote-separator", page);

        FoBlock block = content.addBlock()
                .setTextAlignLast("justify")
                .setPadding("0.5em");

        new FoLeader(block)
                .setPattern("rule")
                .setLength("50%")
                .setRuleThickness("0.5pt")
                .setColor("#777777");

        if (this.pageSet.containsKey(name))
            this.pageSet.get(name).accept(page, properties);

        FoFlow flow = page.flow("region-body");
        flow.setStartIndent("0pt").setEndIndent("0pt");
        return flow;
    }

    protected final void applyStyle(FoBlock block, String name) {
        PageStyle style = template.getStyle(name);

         block.setSpan(style.span);
        block.setColor(style.color);

        if (style.background != null) {
            if (style.background.isImage()) {
                block.setBackground(style.background.get(), "no-repeat");
            } else {
                block.setBackgroundColor(style.background.get());
            }
        }
        block.setFontSize(style.fontSize);
        block.setFontStyle(style.fontStyle);
        block.setFontWeight(style.fontWeight);
        block.setFontFamily(style.fontFamily);

        block.setTextAlign(style.textAlign);
        block.setLineHeight(style.lineHeight);
        block.setKeepWithNext(style.keepWithNext);

        if (style.spaceBefore != null) {
            String[] spaces = style.spaceBefore.split(",");
            if (spaces.length == 3)
                block.setSpaceBefore(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceBefore(style.spaceBefore);
        }

        if (style.spaceAfter != null) {
            String[] spaces = style.spaceAfter.split(",");
            if (spaces.length == 3)
                block.setSpaceAfter(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceAfter(style.spaceAfter);
        }

        block.setBorderRadius(style.borderRadius);
        if (style.borderTop != null) {
            String[] border = style.borderTop.split(" ");
            block.setBorderTop(border[0], border[1], border[2]);
        }
        if (style.borderLeft != null) {
            String[] border = style.borderLeft.split(" ");
            block.setBorderLeft(border[0], border[1], border[2]);
        }
        if (style.borderRight != null) {
            String[] border = style.borderRight.split(" ");
            block.setBorderRight(border[0], border[1], border[2]);
        }
        if (style.borderBottom != null) {
            String[] border = style.borderBottom.split(" ");
            block.setBorderBottom(border[0], border[1], border[2]);
        }

        block.setMarginTop(style.marginTop);
        block.setMarginLeft(style.marginLeft);
        block.setMarginRight(style.marginRight);
        block.setMarginBottom(style.marginBottom);

        block.setPaddingTop(style.paddingTop);
        block.setPaddingLeft(style.paddingLeft);
        block.setPaddingRight(style.paddingRight);
        block.setPaddingBottom(style.paddingBottom);

        block.setBreakBefore(style.breakBefore);
        block.setBreakAfter(style.breakAfter);
    }
}
