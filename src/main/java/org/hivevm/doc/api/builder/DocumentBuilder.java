// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import org.hivevm.doc.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@link DocumentBuilder} class.
 */
public class DocumentBuilder extends PageBuilder implements Document {

    private String title;

    private final Map<String, String> identifiers = new HashMap<>();

    /**
     * Constructs an instance of {@link DocumentBuilder}.
     */
    public DocumentBuilder(Set<String> keywords) {
        BookConfig.init(keywords);
    }

    @Override
    public final String getTitle() {
        return this.title == null ? "" : this.title;
    }

    public final PageBuilder setTitle(String title) {
        this.title = (title == null) ? title : title.trim();
        return this;
    }

    public final HeaderBuilder createHeader(int level) {
        HeaderBuilder builder = new HeaderBuilder(this, level);
        add(builder);
        return builder;
    }

    private String getId(String key) {
        String idref = DocumentBuilder.getIdRef(key.substring(1));
        return this.identifiers.containsKey(idref) ? this.identifiers.get(idref) : key;
    }

    protected void addIndex(Header node) {
        if (!node.getTitle().isEmpty()) {
            String idref = DocumentBuilder.getIdRef(node.getTitle());
            this.identifiers.put(idref, node.getId());
        }
    }

    private void processNode(Node node) {
        if (node instanceof LinkBuilder link) {
            if (link.getLink().startsWith("#")) {
                link.setLink(getId(link.getLink()));
            }
        }
        node.stream().forEach(this::processNode);
    }

    /**
     * Gets the id of the link.
     *
     * @param title
     */
    private static String getIdRef(String title) {
        return title.toLowerCase().replace(" ", "-");
    }

    @Override
    public final <R> void accept(DocumentVisitor<R> visitor, R data) {
        visitor.visit(this, data);
    }

    public final Document build() {
        stream().filter(n -> n instanceof Header)
                .map(n -> (HeaderBuilder) n)
                .forEach(h -> {
                    h.setTitle(h.stream().filter(n -> n instanceof Text)
                            .map(n -> ((Text) n).text())
                            .collect(Collectors.joining(" ")));
                });

        setTitle(stream().filter(n -> n instanceof Header)
                .map(n -> (Header) n)
                .filter(h -> h.getLevel() == 1)
                .map(h -> h.getTitle())
                .findFirst().orElse(null));

        processNode(this);
        return this;
    }
}
