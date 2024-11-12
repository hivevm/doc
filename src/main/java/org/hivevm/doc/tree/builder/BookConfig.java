// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.tree.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The {@link BookConfig} class.
 */
public class BookConfig {

  private static BookConfig INSTANCE;

  public static BookConfig instance() {
    if (BookConfig.INSTANCE == null) {
      BookConfig.init(Collections.emptyList());
    }
    return BookConfig.INSTANCE;
  }

  private final List<BookKeyword> keywords;
  private final Pattern           pattern;

  private BookConfig(List<BookKeyword> keywords) {
    this.keywords = keywords;
    this.pattern = keywords.isEmpty() ? null
        : Pattern.compile(keywords.stream().map(k -> "(" + k.getKeyword() + ")").collect(Collectors.joining("|")),
            Pattern.CASE_INSENSITIVE);
  }

  /**
   * . Process the content string for the {@link ContentBuilder}.
   *
   * @param content
   * @param builder
   */
  public final void processKeywords(String content, Consumer<String> consumer, Supplier<InlineBuilder> supplier) {
    int offset = 0;

    if (!this.keywords.isEmpty() && (this.pattern != null)) {
      Matcher matcher = this.pattern.matcher(content);
      while (matcher.find()) {
        if (matcher.start() > offset) {
          consumer.accept(content.substring(offset, matcher.start()));
        }

        for (int i = 0; i < matcher.groupCount(); i++) {
          String input = matcher.group(i + 1);
          if (input != null) {
            this.keywords.get(i).handle(input, supplier.get());
          }
        }
        offset = matcher.end();
      }
    }

    consumer.accept(content.substring(offset));
  }

  public static void init(Collection<String> mappings) {
    List<BookKeyword> keywords = new ArrayList<>();
    mappings.forEach(m -> keywords.add(BookKeyword.of(m)));
    BookConfig.INSTANCE = new BookConfig(keywords);
  }
}
