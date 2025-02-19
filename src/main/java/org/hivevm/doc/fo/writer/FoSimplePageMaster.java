// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fo.writer;

import org.hivevm.doc.fo.writer.set.FoMargin;
import org.hivevm.util.xml.XmlBuilder;

/**
 * The {@link FoSimplePageMaster} class.
 */
public class FoSimplePageMaster extends FoNode implements FoMargin<FoSimplePageMaster> {

    private final String   name;
    private final FoRegion body;

    /**
     * Constructs an instance of {@link FoSimplePageMaster}.
     *
     * @param name
     */
    public FoSimplePageMaster(String name, XmlBuilder builder) {
        super("fo:simple-page-master", builder);
        this.name = name;
        set("master-name", name);
        setPageSize("210mm", "297mm");
        this.body = new FoRegion(builder);
        addNode(this.body);
    }

    public final String getPageName() {
        return this.name;
    }

    public void setPageSize(String width, String height) {
        setPageWidth(width);
        setPageHeight(height);
    }

    public void setPageWidth(String width) {
        set("page-width", width);
    }

    public void setPageHeight(String height) {
        set("page-height", height);
    }

    public FoRegion setBodyRegion(String name) {
        return this.body.setRegionName(name);
    }

    public FoRegion addRegionBefore(String name) {
        FoRegion region = new FoRegion(name, "before", getBuilder());
        addNode(region);
        return region;
    }

    public FoRegion addRegionAfter(String name) {
        FoRegion region = new FoRegion(name, "after", getBuilder());
        addNode(region);
        return region;
    }

    public FoRegion addRegionStart(String name) {
        FoRegion region = new FoRegion(name, "start", getBuilder());
        addNode(region);
        return region;
    }

    public FoRegion addRegionEnd(String name) {
        FoRegion region = new FoRegion(name, "end", getBuilder());
        addNode(region);
        return region;
    }
}
