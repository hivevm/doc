// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hivevm.doc.api.Block;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.api.StructuralElement;

/**
 * The {@link DocumentBuilder} class.
 */
public class DocumentBuilder extends ContainerBuilder implements Document {

    private String title;

    private final Map<String, String> identifiers = new HashMap<>();

    @Override
    public final String getTitle() {
        if (title == null) {
            title = elements().stream()
                    .filter(n -> n instanceof Header)
                    .map(n -> (Header) n)
                    .filter(h -> h.getLevel() == 1)
                    .map(Header::getTitle)
                    .findFirst().orElse("");
        }
        return this.title;
    }

    public final HeaderBuilder addHeader(int level) {
        return add(new HeaderBuilder(this, level));
    }

    @Override
    public final String getId(String key) {
        String idref = DocumentBuilder.getIdRef(key.substring(1));
        return this.identifiers.getOrDefault(idref, key);
    }

    protected void addIndex(Header node) {
        if (!node.getTitle().isEmpty()) {
            String idref = DocumentBuilder.getIdRef(node.getTitle());
            this.identifiers.put(idref, node.getId());
        }
    }

    private void processNode(NodeBuilder node) {
        node.forEach(this::processNode);
    }

    /**
     * Gets the id of the link.
     */
    private static String getIdRef(String title) {
        return title.toLowerCase().replace(" ", "-");
    }

    public final BreakBuilder addBreak() {
        return add(new BreakBuilder());
    }

    public final MessageBuilder addBlockMessage(Block.Kind style) {
        return add(new MessageBuilder(style));
    }

    public final TableBuilder addTable() {
        return add(new TableBuilder(false));
    }

    public final TableBuilder addVirtualTable() {
        return add(new TableBuilder(true));
    }

    public final Collection<StructuralElement> elements() {
        return nodes();
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        visitor.visit(this, data);
    }

    public final Document build() {
        processNode(this);
        return this;
    }
}
