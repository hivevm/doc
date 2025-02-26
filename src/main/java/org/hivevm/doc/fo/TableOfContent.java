// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Stack;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.Header;
import org.hivevm.doc.fo.writer.FoBasicLink;
import org.hivevm.doc.fo.writer.FoBlock;
import org.hivevm.doc.fo.writer.FoBookmark;
import org.hivevm.doc.fo.writer.FoLeader;
import org.hivevm.doc.fo.writer.FoNode;
import org.hivevm.doc.fo.writer.FoPageNumberCitation;
import org.hivevm.doc.fo.writer.FoRoot;
import org.jspecify.annotations.NonNull;

/**
 * Represents a Table of Contents (ToC) for a document. This class manages hierarchical
 * relationships between headers in a document and creates bookmarks for easy navigation.
 */
public class TableOfContent {

    private static final int[]    ARAB  = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5,
        4, 1};
    private static final String[] ROMAN = {"M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L", "XL",
        "X", "IX", "V", "IV", "I"};


    private final String              tocId;
    private final Map<Header, String> chapters;

    private TableOfContent(Map<Header, String> chapters) {
        this.chapters = chapters;
        this.tocId = Long.toHexString(new Random().nextLong());
    }

    public final String getChapter(Header header) {
        return chapters.get(header);
    }

    public final String getTitle(Header header) {
        return Fo.encode(header.getTitle().trim());
    }

    public final String getChapterTitle(Header header) {
        return chapters.get(header) + getTitle(header);
    }

    public final void renderBookmark(@NonNull Document doc, FoRoot root) {
        try (var tree = new FoNode("bookmark-tree", root)) {
            var bookmarks = new Stack<FoBookmark>();
            bookmarks.push(new FoBookmark(Fo.BOOK_ID, tree).setTitle(doc.getTitle()));
            doc.elements().stream()
                .filter(n -> n instanceof Header)
                .map(n -> (Header) n)
                .filter(h -> h.getLevel() > 1)
                .forEach(h -> renderBookmark(h, tree, bookmarks));
            new FoBookmark(tocId, tree).setTitle("I. - Table of content");
        }
    }

    private void renderBookmark(@NonNull Header node, @NonNull FoNode tree,
        Stack<FoBookmark> bookmarks) {
        while (bookmarks.size() >= node.getLevel()) {
            bookmarks.pop();
        }

        var bookmark = bookmarks.isEmpty()
            ? new FoBookmark(node.getId(), tree)
            : bookmarks.peek().addBookmark(node.getId());
        bookmark.setTitle(getChapterTitle(node));
        bookmarks.push(bookmark);
    }

    public final void renderToc(Document doc, FoContext context) {
        var title = "Table of Contents";
        var template = context.template();
        var properties = new Properties();
        properties.put("TITLE", title);

        var page = context.root()
            .addPageSequence(tocId)
            .setReference(Fo.PAGESET_STANDARD)
            .setInitialPageNumber("1")
            .setFormat("I")
            .setLanguage("en");

        var flow = FoDocumentRenderer.createFlow(page, Fo.PAGESET_STANDARD, template, properties);
        var content = FoBlock.block(flow);
        content.setBreakBefore("page")
            .setSpaceBefore("0.5em", "1.0em", "2.0em")
            .setSpaceAfter("0.5em", "1.0em", "2.0em")
            .setColor("#000000")
            .setTextAlign("left");
        content.setId(tocId);

        var block = content.addBlock();
        block.setSpaceBefore("1.0em", "1.5em", "2.0em");
        block.setSpaceAfter("0.5em").setStartIndent("0pt");
        block.setFontWeight("bold").setFontSize("18pt");
        block.addText(title);

        doc.elements().stream()
            .filter(n -> n instanceof Header)
            .map(n -> (Header) n)
            .filter(h -> h.getLevel() > 1)
            .forEach(header -> {
                int intent = header.getLevel() - 1;

                var block0 = content.addBlock();
                block0.setMarginLeft(String.format("%sem", intent));
                block0.setTextAlignLast("justify");

                var link = new FoBasicLink(block0);
                link.setDestination(header.getId());

                var inline = FoBlock.inline(link);
                inline.setKeepWithNext("always")
                    .addText(Fo.encode(getChapterTitle(header)));

                var leader = new FoLeader(inline);
                leader.setPattern("dots")
                    .setWidth("3pt")
                    .setAlign("reference-area")
                    .setPaddingLeftRight("3pt");
                new FoPageNumberCitation(header.getId(), inline);
            });
    }

    private static String asRomanNumber(int number) {
        var result = new StringBuilder();
        int i = 0;
        while ((number > 0) || (TableOfContent.ARAB.length == (i - 1))) {
            while ((number - TableOfContent.ARAB[i]) >= 0) {
                number -= TableOfContent.ARAB[i];
                result.append(TableOfContent.ROMAN[i]);
            }
            i++;
        }
        return result.toString();
    }

    public static TableOfContent create(Document doc) {
        var stack = new Stack<Integer>();
        var chapters = new HashMap<Header, String>();
        for (var header : doc.elements().stream()
            .filter(n -> n instanceof Header)
            .map(n -> (Header) n).toList()) {

            while (stack.size() > header.getLevel()) {
                stack.pop();
            }

            if (stack.size() < header.getLevel()) {
                while (stack.size() < header.getLevel() - 1) {
                    stack.push(0);
                }
                stack.push(1);
            }
            else {
                int offset = stack.pop();
                stack.push(offset + 1);
            }

            if (header.getLevel() == 1)
                chapters.put(header, "");
            else {
                var value = "";
                var isRoman = false;
                for (var i = 1; i < stack.size(); i++) {
                    int level = stack.get(i);
                    if (level == 0) {
                        value = "";
                        isRoman = true;
                    }
                    else
                        value += (isRoman ? TableOfContent.asRomanNumber(level) : level) + ".";
                }
                chapters.put(header, value.isEmpty() ? "" : value + " ");
            }
        }
        return new TableOfContent(chapters);
    }
}
