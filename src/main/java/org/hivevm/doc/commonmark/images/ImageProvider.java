// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.images;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;

import java.util.Map;

class ImageProvider implements AttributeProvider {

    private ImageProvider() {
    }

    public static ImageProvider create() {
        return new ImageProvider();
    }

    @Override
    public void setAttributes(Node node, String tagName, final Map<String, String> attributes) {
        if (node instanceof Image) {
            node.accept(new AbstractVisitor() {

                @Override
                public void visit(CustomNode node) {
                    if (node instanceof ImageAttributes imageAttributes) {
                        for (Map.Entry<String, String> entry : imageAttributes.getAttributes().entrySet()) {
                            attributes.put(entry.getKey(), entry.getValue());
                        }
                        // Now that we have used the image attributes we remove the node.
                        imageAttributes.unlink();
                    }
                }
            });
        }
    }
}
