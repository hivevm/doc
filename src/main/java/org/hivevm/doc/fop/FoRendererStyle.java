// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause
package org.hivevm.doc.fop;

import org.hivevm.doc.fop.nodes.FoBlock;
import org.hivevm.doc.fop.nodes.FoNode;
import org.hivevm.doc.template.PageBreak;
import org.hivevm.doc.template.PageColumn;
import org.hivevm.doc.template.PageImage;
import org.hivevm.doc.template.PageRow;

import java.util.Properties;

public class FoRendererStyle extends FoRenderer<FoNode> {

    public FoRendererStyle() {
        super(new Properties());
    }

    public FoRendererStyle(Properties properties) {
        super(properties);
    }

    @Override
    public final void visit(PageColumn item, FoNode node) {
        FoBlock block = FoBlock.block();
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
        block.setKeepWithNext(item.keepWithNext);

        if (item.spaceBefore != null) {
            String[] spaces = item.spaceBefore.split(",");
            if (spaces.length == 3)
                block.setSpaceBefore(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceBefore(item.spaceBefore);
        }

        if (item.spaceAfter != null) {
            String[] spaces = item.spaceAfter.split(",");
            if (spaces.length == 3)
                block.setSpaceAfter(spaces[0], spaces[1], spaces[2]);
            else
                block.setSpaceAfter(item.spaceAfter);
        }

        block.setBorderRadius(item.borderRadius);
        if (item.borderTop != null) {
            String[] border = item.borderTop.split(" ");
            block.setBorderTop(border[0], border[1], border[2]);
        }
        if (item.borderLeft != null) {
            String[] border = item.borderLeft.split(" ");
            block.setBorderLeft(border[0], border[1], border[2]);
        }
        if (item.borderRight != null) {
            String[] border = item.borderRight.split(" ");
            block.setBorderRight(border[0], border[1], border[2]);
        }
        if (item.borderBottom != null) {
            String[] border = item.borderBottom.split(" ");
            block.setBorderBottom(border[0], border[1], border[2]);
        }

        block.setMarginTop(item.marginTop);
        block.setMarginLeft(item.marginLeft);
        block.setMarginRight(item.marginRight);
        block.setMarginBottom(item.marginBottom);

        block.setPaddingTop(item.paddingTop);
        block.setPaddingLeft(item.paddingLeft);
        block.setPaddingRight(item.paddingRight);
        block.setPaddingBottom(item.paddingBottom);

        block.setBreakBefore(item.breakBefore);
        block.setBreakAfter(item.breakAfter);

        node.addNode(block);

        item.items.forEach(c -> c.accept(this, block));
    }

    @Override
    public final void visit(PageRow item, FoNode node) {
        FoBlock block = FoBlock.block();
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

        node.addNode(block);
    }

    @Override
    public final void visit(PageImage item, FoNode node) {
    }

    @Override
    public final void visit(PageBreak item, FoNode node) {
        FoBlock block = FoBlock.block();
        block.setBorderBottom(item.size, item.style, item.color);

        node.addNode(block);
    }
}