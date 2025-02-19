// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.template.PageMatch;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoPageSequenceMaster} class.
 */
public class FoPageSequenceMaster extends FoNode {

    private final String name;
    private final FoNode master;

    public enum BlankOrNot {

        Blank("blank"),
        NotBlank("not-blank "),
        Any("any");

        public final String value;

        BlankOrNot(String value) {
            this.value = value;
        }
    }

    public enum OddOrEven {

        Odd("odd"),
        Even("even"),
        Any("any");

        public final String value;

        OddOrEven(String value) {
            this.value = value;
        }
    }

    public enum Position {

        First("first"),
        Last("last"),
        Rest("rest"),
        Any("any"),
        Only("only");

        public final String value;

        Position(String value) {
            this.value = value;
        }
    }

    /**
     * Constructs an instance of {@link FoPageSequenceMaster}.
     *
     * @param name
     */
    public FoPageSequenceMaster(String name, XmlBuilder builder) {
        super("fo:page-sequence-master", builder);
        this.name = name;
        set("master-name", name);
        this.master = FoNode.create("fo:repeatable-page-master-alternatives", builder);
        super.addNode(this.master);
    }

    public final String getPageName() {
        return this.name;
    }

    public FoNode addPage(String name, PageMatch match) {
        FoNode builder = FoNode.create("fo:conditional-page-master-reference", getBuilder());
        builder.set("master-reference", name);
        switch (match) {
            case Odd -> builder.set("odd-or-even", OddOrEven.Odd.value);
            case Even -> builder.set("odd-or-even", OddOrEven.Even.value);
            case Rest -> builder.set("page-position", Position.Rest.value);
            case First -> builder.set("page-position", Position.First.value);
            case Last -> builder.set("page-position", Position.Last.value);
            case Only -> builder.set("page-position", Position.Only.value);
            case Blank -> builder.set("blank-or-not-blank", BlankOrNot.Blank.value);
            case NotBlank -> builder.set("blank-or-not-blank", BlankOrNot.NotBlank.value);
        }
        setNode(builder);
        return this;
    }

    /**
     * Add a new child {@link FoNode}.
     *
     * @param node
     */
    public FoPageSequenceMaster setNode(FoNode node) {
        this.master.addNode(node);
        return this;
    }
}
