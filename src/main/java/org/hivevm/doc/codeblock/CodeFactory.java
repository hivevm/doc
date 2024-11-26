// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.codeblock;

import java.util.HashMap;
import java.util.Map;

import org.hivevm.doc.builder.CodeBuilder;
import org.hivevm.doc.builder.CodeParser;
import org.hivevm.doc.builder.SectionBuilder;

/**
 * The {@link CodeFactory} implements a tokenizer for a specific language.
 */
public class CodeFactory implements CodeParser {

  private final Map<String, CodeParser> parsers = new HashMap<>();

  /**
   * Constructs an instance of {@link CodeFactory}.
   */
  public CodeFactory() {
    this.parsers.put("ini", new CodeParserIni());
    this.parsers.put("conf", new CodeParserConf());
    this.parsers.put("yaml", new CodeParserYaml());
    this.parsers.put("shell", new CodeParserShell());

    this.parsers.put("meta", new CodeParserMeta());
    this.parsers.put("java", new CodeParserJava());
    this.parsers.put("c", new CodeParserCpp());
    this.parsers.put("cpp", new CodeParserCpp());
    this.parsers.put("c++", new CodeParserCpp());
    this.parsers.put("sql", new CodeParserSql());
    this.parsers.put("oql", new CodeParserSql());

    this.parsers.put("xml", new CodeParserXml());
    this.parsers.put("json", new CodeParserJson());
    this.parsers.put("api", new CodeParserApi());
  }

  /**
   * Generates the code text
   *
   * @param node
   */
  @Override
  public final void generate(String text, CodeBuilder builder) {
    builder.addText(text);
  }

  /**
   * Creates a {@link CodeFactory} for a specific language.
   *
   * @param name
   * @param section
   */
  public final void generate(String name, String text, SectionBuilder builder) {
    String key = name.toLowerCase();
    CodeParser parser = this.parsers.containsKey(key) ? this.parsers.get(key) : this;

    CodeBuilder styled = builder.addCode();
    styled.setStyled(true);
    parser.generate(text, styled);
  }
}
