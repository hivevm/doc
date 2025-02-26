// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.Set;

/**
 * The {@link Flow} defines the structure of a single line in a document. A flow contains Text, Images, Links and
 * Styling.
 */
public interface Flow {

    <D> void accept(Visitor<D> visitor, D data);

    /**
     * The {@link Text} defines a text element in the flow.
     */
    record Text(String text, Iterable<Flow> flows) implements Flow {

        @Override
        public void accept(Flow.Visitor visitor, Object data) {
            visitor.visit(this, data);
        }
    }

    /**
     * The {@link Link} defines a link to an internal or external location.
     */
    record Link(String link, String title, Iterable<Flow> flows) implements Flow {

        public void accept(Flow.Visitor visitor, Object data) {
            visitor.visit(this, data);
        }
    }

    /**
     * The {@link Style} defines a classification of a flow.
     */
    record Style(Set<Flow.Kind> kinds, String color, Iterable<Flow> flows) implements Flow {

        @Override
        public void accept(Flow.Visitor visitor, Object data) {
            visitor.visit(this, data);
        }
    }

    /**
     * The {@link Image} defines an Image in the current flow.
     */
    record Image(String url, String text, String align, String width, String height) implements Flow {

        @Override
        public void accept(Flow.Visitor visitor, Object data) {
            visitor.visit(this, data);
        }
    }

    enum Kind {
        CODE,
        BOLD,
        ITALIC,
        OVERLINE,
        UNDERLINE,
        HIGHLIGHT,
        STRIKETHROUGH
    }

    interface Visitor<D> {

        void visit(Text node, D data);

        void visit(Link node, D data);

        void visit(Image node, D data);

        void visit(Style node, D data);
    }
}
