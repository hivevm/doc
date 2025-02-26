// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.DocumentVisitor;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.fo.writer.FoBookmark;
import org.hivevm.doc.fo.writer.FoNode;

import java.util.Stack;

public class FoBookmarkRenderer implements DocumentVisitor<FoNode> {

    private final String            docId;
    private final String            tocId;
    private final Stack<FoBookmark> bookmarks;

    /**
     * Constructs an instance of {@link FoBookmarkRenderer}.
     */
    public FoBookmarkRenderer(String docId, String tocId) {
        this.docId = docId;
        this.tocId = tocId;
        this.bookmarks = new Stack<>();
    }

    @Override
    public final void visit(Document doc, FoNode tree) {
        new FoBookmark(docId, tree).setTitle(doc.getTitle());
        doc.stream().forEach(n -> n.accept(this, tree));
        new FoBookmark(tocId, tree).setTitle("Table of Contents");
    }

    /**
     * Renders a {@link Header} node.
     */
    @Override
    public final void visit(Header node, FoNode tree) {
        while (bookmarks.size() >= node.getLevel())
            bookmarks.pop();

        FoBookmark bookmark = (node.getLevel() > 1)
                ? bookmarks.peek().addBookmark(node.getId())
                : new FoBookmark(node.getId(), tree);
        bookmark.setTitle(PageUtil.encode(node.getTitle().trim()));
        bookmarks.push(bookmark);
    }
}