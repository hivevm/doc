// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.hivevm.doc.commonmark.Markdown;
import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.doc.fop.FoGenerator;
import org.hivevm.doc.fop.config.FoBuilder;
import org.hivevm.doc.fop.config.FoContext;
import org.hivevm.doc.markdown.MarkdownParser;
import org.hivevm.doc.pdf.PdfGenerator;
import org.hivevm.doc.tree.Book;
import org.hivevm.doc.tree.builder.BookBuilder;
import org.hivevm.doc.tree.builder.BookConfig;
import org.hivevm.doc.util.Replacer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * The {@link DocumentBuilder} class.
 */
public class DocumentBuilder {

  private final File          base;

  private File                target;
  private String              source;
  private String              config;
  private String              suffix;

  private Consumer<String>    onInfo;
  private Consumer<Throwable> onError;

  private final Properties    properties = new Properties();

  /**
   * Constructs an instance of {@link DocumentBuilder}.
   *
   * @param base
   */
  public DocumentBuilder(File base) {
    this.base = base;
    this.suffix = "";
    this.onInfo = m -> System.out.println(m);
    this.onError = Throwable::printStackTrace;
  }

  /**
   * Set the target directory.
   *
   * @param target
   */
  public DocumentBuilder setTarget(File target) {
    this.target = target;
    return this;
  }

  /**
   * Set the source directory.
   *
   * @param source
   */
  public DocumentBuilder setSource(String source) {
    this.source = source;
    return this;
  }

  /**
   * Set the config.
   *
   * @param config
   */
  public DocumentBuilder setConfig(String config) {
    this.config = config;
    return this;
  }

  /**
   * Set the suffix.
   *
   * @param suffix
   */
  public DocumentBuilder setSuffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  /**
   * Add an environment variable.
   *
   * @param name
   * @param value
   */
  public DocumentBuilder addProperty(String name, Object value) {
    if (value != null) {
      this.properties.put(name, value);
    }
    return this;
  }

  /**
   * Add enviroment variables.
   *
   * @param properties
   */
  public DocumentBuilder addProperties(Properties properties) {
    this.properties.putAll(properties);
    return this;
  }

  /**
   * Add an information handler.
   *
   * @param consumer
   */
  public DocumentBuilder onInfo(Consumer<String> consumer) {
    this.onInfo = consumer;
    return this;
  }

  /**
   * Add an error handler.
   *
   * @param consumer
   */
  public DocumentBuilder onError(Consumer<Throwable> consumer) {
    this.onError = consumer;
    return this;
  }

  /**
   * Get the properties.
   */
  public Properties getProperties() {
    return this.properties;
  }

  /**
   * Get the template config.
   */
  protected final String getConfig() {
    String config = this.config;
    if ((config == null) || config.isEmpty()) {
      config = ":default.ui.xml";
    }
    if (config.startsWith(":") && config.endsWith(":")) {
      config = String.format("%s.ui.xml", config.substring(0, config.length() - 1).toLowerCase());
    }
    return config;
  }

  /**
   * Get the markdown files.
   */
  protected final List<File> getFiles() {
    File source = (this.source == null) ? this.base : new File(this.source);
    if (!source.isAbsolute()) {
      source = new File(this.base, this.source);
    }

    List<File> files = new ArrayList<>();
    if (source.isDirectory()) {
      for (File file : source.listFiles()) {
        if (file.getName().toLowerCase().endsWith(".md")) {
          files.add(file);
        }
      }
    } else if (source.getName().toLowerCase().endsWith(".md")) {
      files.add(source);
    }
    return files;
  }

  /**
   * Parses a markdown file.
   *
   * @param file
   * @param properties
   */
  protected static Book parseMarkdown(File file, Properties properties) throws IOException {
    Replacer replacer = new Replacer(properties);
    MarkdownReader reader = new MarkdownReader(file);
    String text = replacer.replaceAll(reader.readAll());

    Parser parser = Markdown.newInstance();
    Node node = parser.parse(text);

    BookBuilder builder = new BookBuilder();
    node.accept(new MarkdownParser(builder));
    return builder.build();
  }

  /**
   * Executes the {@link DocumentBuilder}.
   */
  public final void build() {
    File target = this.target;
    if (target == null) {
      target = new File(".");
    }
    target.mkdirs();

    String config = getConfig();
    for (File file : getFiles()) {
      this.onInfo.accept(String.format("Processing file: %s", file.getName()));

      String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
      File workingDir = config.startsWith(":") ? new File(".") : new File(config).getParentFile();

      File output = new File(target, name + this.suffix + ".pdf");
      if (output.exists()) {
        output.delete();
      }

      try {
        FoBuilder template = FoContext.parse(config, workingDir);
        BookConfig.init(template.getKeywords());

        FoGenerator builder = new FoGenerator();
        builder.setName(name);
        builder.setTarget(target);
        builder.setContext(template.build());

        Book book = DocumentBuilder.parseMarkdown(file, this.properties);

        try (OutputStream ostream = new FileOutputStream(output)) {
          try (InputStream istream = builder.generate(book)) {

            PdfGenerator generator = new PdfGenerator(template.getWidth(), template.getHeight(), workingDir);
            template.forEachFont(f -> generator.addFont(f));
            generator.write(istream, ostream);
          }
        }
      } catch (Throwable e) {
        this.onError.accept(e);
      }
    }
  }
}
