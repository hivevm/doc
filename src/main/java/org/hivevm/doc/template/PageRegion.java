// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The {@link PageRegion} class.
 */
public class PageRegion implements PageContainer {

    public enum Region {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    private final String name;
    private final Region region;

    private String extent;

    private int  columns   = 1;
    private Unit columnGap = Unit.NIL;

    private final List<PageColumn> children = new ArrayList<>();

    /**
     * Constructs an instance of {@link PageRegion}.
     *
     * @param region
     */
    public PageRegion(String name, Region region) {
        this.name = String.format("region-%s-%s", name, region.name());
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    public String getExtent() {
        return extent;
    }

    public String columnCount() {
        return String.valueOf(this.columns);
    }

    public String columnGap() {
        return this.columnGap.asString();
    }

    public final void addItem(PageRenderer.PageRenderable item) {
        this.children.add((PageColumn) item);
    }

    public final void setExtent(String extent) {
        this.extent = extent;
    }

    /**
     * Set the columns of the template.
     *
     * @param count
     * @param gap
     */
    public void setColumns(String count, String gap) {
        this.columns = Integer.parseInt(count);
        this.columnGap = Unit.parse(gap);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public final void forEachItem(Consumer<PageColumn> action) {
        children.forEach(action);
    }
}
