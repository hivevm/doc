// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.builder;

import java.util.Collection;
import org.hivevm.doc.api.Document;
import org.hivevm.doc.api.List;
import org.hivevm.doc.api.ParagraphElement;

/**
 * The {@link ListItemBuilder} class.
 */
public class ListItemBuilder extends NodeBuilder implements List.Item {

    public enum State {
        NONE,
        CHECKED,
        UNCHECKED
    }

    public ListItemBuilder() {
        this.state = State.NONE;
    }

    private State state;

    public final State state() {
        return state;
    }

    public final void setState(State state) {
        this.state = state;
    }

    public final ParagraphBuilder addParagraph() {
        return add(new ParagraphBuilder());
    }

    public final ListBuilder addList(boolean ordered) {
        return add(new ListBuilder(ordered));
    }

    public final Collection<ParagraphElement> elements() {
        return nodes();
    }

    @Override
    public final <R> void accept(Document.Visitor<R> visitor, R data) {
        throw new UnsupportedOperationException();
    }
}
