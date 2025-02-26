// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause
package org.hivevm.doc.fo;

import org.hivevm.doc.fo.writer.FoBlockContainer;
import org.hivevm.doc.fo.writer.FoExternalGraphic;
import org.hivevm.doc.fo.writer.FoStaticContent;
import org.hivevm.doc.template.PageBreak;
import org.hivevm.doc.template.PageColumn;
import org.hivevm.doc.template.PageImage;
import org.hivevm.doc.template.PageRow;
import org.hivevm.util.DataUri;

import java.util.Properties;

public class FoRendererFixed extends FoRenderer<FoStaticContent> {

    public FoRendererFixed() {
        super(new Properties());
    }

    @Override
    public final void visit(PageColumn item, FoStaticContent content) {
    }

    @Override
    public final void visit(PageImage item, FoStaticContent content) {
        FoBlockContainer container = content.blockContainer(FoBlockContainer.Position.Fixed);
        container.setPositionTop(item.getTop());
        container.setPositionLeft(item.getLeft());
        container.setPositionRight(item.getRight());
        container.setPositionBottom(item.getBottom());
        container.setWidth(item.getWidth());
        container.setHeight(item.getHeight());

        String base64 = DataUri.loadImage(item.uri);
        FoExternalGraphic image = new FoExternalGraphic(base64, container.addBlock());
        image.setSize(item.getWidth(), item.getHeight());
        image.setContentWidth("scale-to-fit");
        image.setContentHeight("scale-to-fit");
    }

    @Override
    public final void visit(PageRow item, FoStaticContent content) {
    }

    @Override
    public final void visit(PageBreak item, FoStaticContent content) {
    }
}