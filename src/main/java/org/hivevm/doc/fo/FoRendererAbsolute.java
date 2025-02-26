// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause
package org.hivevm.doc.fo;

import org.hivevm.doc.fo.writer.FoBlock;
import org.hivevm.doc.fo.writer.FoExternalGraphic;
import org.hivevm.doc.template.PageBreak;
import org.hivevm.doc.template.PageColumn;
import org.hivevm.doc.template.PageImage;
import org.hivevm.doc.template.PageRow;
import org.hivevm.util.DataUri;

import java.util.Properties;

public class FoRendererAbsolute extends FoRenderer<FoBlock> {

    public FoRendererAbsolute(Properties properties) {
        super(properties);
        properties.put("PAGE_NUMBER", "<fo:page-number/>");
    }

    @Override
    public final void visit(PageColumn item, FoBlock block) {
        block.setSpan(item.span);
        block.setMargin(item.getLeft(), item.getRight(), item.getTop(), item.getBottom());
        block.setSpan(item.span);

        block.setColor(item.color);
        if (item.background != null) {
            if (item.background.isImage()) {
                block.setBackground(item.background.get(), "no-repeat");
            } else {
                block.setBackgroundColor(item.background.get());
            }
        }
        block.setFontSize(item.fontSize);
        block.setFontStyle(item.fontStyle);
        block.setFontWeight(item.fontWeight);
        block.setFontFamily(item.fontFamily);

        block.setTextAlign(item.textAlign);
        block.setLineHeight(item.lineHeight);

        item.items.forEach(c -> c.accept(this, block.addBlock()));
    }

    @Override
    public final void visit(PageRow item, FoBlock block) {
        block.setColor(item.color);
        block.setFontSize(item.fontSize);
        block.setFontStyle(item.fontStyle);
        block.setFontWeight(item.fontWeight);
        block.setFontFamily(item.fontFamily);

        block.setTextAlign(item.textAlign);
        block.setLineHeight(item.lineHeight);
        block.setPadding(item.getLeft(), item.getRight(), item.getTop(), item.getBottom());

        item.forEachInline(i -> {
            FoBlock inline = block.addInline();
            inline.setColor(i.color);
            inline.setFontSize(i.fontSize);
            inline.setFontStyle(i.fontStyle);
            inline.setFontWeight(i.fontWeight);
            inline.setFontFamily(i.fontFamily);
            inline.setTextAlign(i.textAlign);
            inline.setLineHeight(i.lineHeight);

            addAndReplaceText(i.text, inline);
        });
    }

    @Override
    public final void visit(PageImage item, FoBlock block) {
        String base64 = DataUri.loadImage(item.uri);
        new FoExternalGraphic(base64, block);
    }

    @Override
    public final void visit(PageBreak item, FoBlock block) {
        block.setBorderBottom(item.size, item.style, item.color);
    }
}