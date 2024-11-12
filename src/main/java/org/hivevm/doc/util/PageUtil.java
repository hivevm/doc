// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.util;

import org.hivevm.doc.tree.Chapter;

/**
 * The {@link PageUtil} class.
 */
public abstract class PageUtil {

  private static final int    ARAB[]  = { 1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
  private static final String ROMAN[] =
      { "M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

  /**
   * Encode the text to avoid XML reserved characters.
   *
   * @param text
   */
  public static String encode(String text) {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  /**
   * Get the page index number.
   *
   * @param page
   */
  public static String getPageNumber(Chapter page) {
    String number = Integer.toString(page.getOffset() + 1);
    return (page.getParent().getOffset() < 0) ? number
        : String.format("%s.%s", PageUtil.getPageNumber(page.getParent()), number);
  }

  /**
   * Get the preface index number.
   *
   * @param page
   */
  public static String getRomanNumber(Chapter page) {
    StringBuilder result = new StringBuilder();
    int number = page.getOffset() + 1;
    int i = 0;
    while ((number > 0) || (PageUtil.ARAB.length == (i - 1))) {
      while ((number - PageUtil.ARAB[i]) >= 0) {
        number -= PageUtil.ARAB[i];
        result.append(PageUtil.ROMAN[i]);
      }
      i++;
    }
    return result.toString();
  }

  /**
   * Gets the id of the link.
   *
   * @param title
   */
  public static String getLinkId(String title) {
    return title.toLowerCase().replace(" ", "-");
  }
}
