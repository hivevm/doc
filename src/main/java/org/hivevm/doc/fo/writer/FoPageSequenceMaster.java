// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.template.PageMatch;

/**
 * The {@link FoPageSequenceMaster} class.
 */
public class FoPageSequenceMaster extends FoNode {

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
     * @param layout
     */
    public FoPageSequenceMaster(String name, FoNode layout) {
        super("page-sequence-master", layout);
        set("master-name", name);
        this.master = new FoNode("repeatable-page-master-alternatives", this);
    }

    public FoNode addPage(String name, PageMatch match) {
        FoNode builder = new FoNode("conditional-page-master-reference", this.master);
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
        return this;
    }
}
