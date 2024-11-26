// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Stack;

import org.hivevm.doc.pdf.PdfFont;
import org.hivevm.util.DataUri;
import org.hivevm.util.StAX;
import org.hivevm.util.StAX.Attributes;


/**
 * The {@link FluidParser} class.
 */
public class FluidParser implements StAX.Handler {

  private final FluidBuilder builder;
  private final Stack<Fluid> items = new Stack<>();

  /**
   * Constructs an instance of {@link FluidParser}.
   *
   * @param builder
   */
  public FluidParser(FluidBuilder builder) {
    this.builder = builder;
  }

  /**
   * Get {@link FluidBuilder}.
   */
  protected final FluidBuilder getBuilder() {
    return this.builder;
  }

  /**
   * Get top {@link Fluid}.
   *
   * @param clazz
   */
  @SuppressWarnings("unchecked")
  protected final <I extends Fluid> I getItem(Class<I> clazz) {
    return (I) this.items.peek();
  }

  /**
   * Adds an {@link Fluid} item.
   *
   * @param item
   */
  protected final <I extends Fluid> I addItem(I item) {
    this.items.push(item);
    return item;
  }

  /**
   * Release the current item.
   */
  protected final void releaseItem() {
    this.items.pop();
  }

  /**
   * Adds a font metric to the current font.
   *
   * @param file
   * @param bold
   * @param italic
   */
  protected final void addFontMetric(String file, boolean bold, boolean italic) {
    URI uri = DataUri.toURI(getBuilder().getWorkingDir(), file);
    this.font.addMetric(uri, bold, italic);
  }

  private PdfFont font;


  @Override
  public void handleEvent(String name, Attributes attrs) {
    switch (name) {
      case "template":
        getBuilder().setSize(attrs.get("width"), attrs.get("height"));
        break;

      case "formatter":
        getBuilder().getKeywords().add(String.format("/%s/%s/", attrs.get("pattern"), attrs.get("value")));
        break;

      case "font":
        this.font = getBuilder().addFontName(attrs.get("name"));
        if(attrs.isSet("codepoint")) {
          try {
            URL url = DataUri.toURI(null, attrs.get("codepoint")).toURL();
            getBuilder().getSymbols().registerSymbols(Fluid.FONT_SYMBOLS, url);
          } catch (MalformedURLException e) {}
        }
        break;

      case "font-metric":
        addFontMetric(attrs.get("uri"), attrs.getBool("bold"), attrs.getBool("italic"));
        break;

      case "page":
        FluidTemplate template = getBuilder().addTemplate(attrs.get("name"));
        template.setPageSize(attrs.get("width", getBuilder().getWidth()),
            attrs.get("height", getBuilder().getHeight()));
        addItem(template);

        if (attrs.isSet("column-count")) {
          template.setColumns(attrs.get("column-count"), attrs.get("column-gap"));
        }

        attrs.onAttribute("padding-top", p -> template.getPadding().setTop(p));
        attrs.onAttribute("padding-left", p -> template.getPadding().setLeft(p));
        attrs.onAttribute("padding-right", p -> template.getPadding().setRight(p));
        attrs.onAttribute("padding-bottom", p -> template.getPadding().setBottom(p));
        attrs.onAttribute("padding", p -> {
          String[] padding = p.split(",");
          if (padding.length == 1) {
            padding = new String[] { padding[0], padding[0], padding[0], padding[0] };
          } else if (padding.length == 2) {
            padding = new String[] { padding[0], padding[1], padding[0], padding[1] };
          }

          template.getPadding().setTop(padding[0]);
          template.getPadding().setLeft(padding[3]);
          template.getPadding().setRight(padding[1]);
          template.getPadding().setBottom(padding[2]);
        });
        break;

      case "region":
        String id = attrs.get("name");
        String position = attrs.get("position");

        template = getItem(FluidTemplate.class);
        FluidRegion region = template.setRegion(id, position);
        region.setExtent(attrs.get("extent"));
        if (attrs.isSet("background")) {
          String background = attrs.get("background");
          if (background.startsWith("#")) {
            region.setBackground(background);
          } else {
            region.setBackground(DataUri.toURI(getBuilder().getWorkingDir(), background));
          }
        }

        addItem(region);
    }
  }

  @Override
  public void handleEvent(String name, String content) {
    switch (name) {
      case "text":
      case "template":
      case "region":
      case "panel":
      case "image":
        releaseItem();
        break;
    }
  }
}
