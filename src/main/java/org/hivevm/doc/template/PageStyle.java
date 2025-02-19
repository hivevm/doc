// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The {@link PageStyle} defines a generic interface for all template items.
 */
public class PageStyle implements PageContainer {

    public Pattern pattern;

    public final List<PageRenderer.PageRenderable> items = new ArrayList<>();

    /**
     * Constructs an instance of {@link PageStyle}.
     */
    public PageStyle(Pattern pattern) {
        this.pattern = pattern;
    }

    public final boolean matches(String text) {
        return pattern.matcher(text).find();
    }

    @Override
    public final void addItem(PageRenderer.PageRenderable child) {
        this.items.add(child);
    }
}
