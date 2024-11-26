// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages {@link FluidSymbols}'s
 */
public class FluidSymbols {

  private static Pattern PATTERN     = Pattern.compile("^([^\\s]+)\\s([^\\s]+)$");
  private static Pattern PLACEHOLDER = Pattern.compile("(@([^@]+)@)");


  private final Map<String, String>    FONTS   = new HashMap<>();
  private final Map<String, Character> SYMBOLS = new HashMap<>();

  /**
   * Avoid to create an instance of {@link FluidSymbols}
   */
  protected FluidSymbols() {}

  /**
   * Register the code-points for the font.
   *
   * @param name
   * @param codepoints
   */
  final void registerSymbols(String name, URL codepoints) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(codepoints.openStream()))) {
      reader.lines().forEach(l -> {
        Matcher matcher = FluidSymbols.PATTERN.matcher(l);
        if (matcher.find()) {
          String symbol = matcher.group(1);
          String value = matcher.group(2);
          this.FONTS.put(symbol, name);
          this.SYMBOLS.put(symbol, (char) Integer.parseInt(value, 16));
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Register an alias for a symbol.
   *
   * @param name
   * @param alias
   * @param symbol
   */
  final void registerSymbol(String name, String alias, String symbol) {
    this.FONTS.put(alias, name);
    this.SYMBOLS.put(alias, this.SYMBOLS.get(symbol));
  }

  /**
   * Renders each material symbol of the text using the provided {@link Function}.
   *
   * @param text
   * @param function
   */
  public final String forEach(String text, BiFunction<String, String, String> function) {
    int offset = 0;
    StringBuffer buffer = new StringBuffer();

    Matcher matcher = FluidSymbols.PLACEHOLDER.matcher(text);
    while (matcher.find()) {
      buffer.append(text.substring(offset, matcher.start(1)));
      offset = matcher.end(1);

      String symbolName = matcher.group(2);
      if (this.SYMBOLS.get(symbolName) != null) {
        String font = this.FONTS.get(symbolName);
        Character symbol = this.SYMBOLS.get(symbolName);
        buffer.append(function.apply(symbol.toString(), font));
      } else {
        buffer.append("@" + symbolName + "@");
      }
    }

    buffer.append(text.substring(offset));
    return buffer.toString();
  }
}
