// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

/**
 * The {@link FoPageSequenceMaster} class.
 */
public class FoPageSequenceMaster extends FoNode {

  private final String name;
  private final FoNode master;

  public enum BlankOrNot {

    Blank("blank"),
    NotBlank("not-blank "),
    Any("any");

    public final String value;

    BlankOrNot(String value) {
      this.value = value;
    }
  }

  public enum OddOrEven {

    Odd("odd"),
    Even("even"),
    Any("any");

    public final String value;

    OddOrEven(String value) {
      this.value = value;
    }
  }

  public enum Position {

    First("first"),
    Last("last"),
    Rest("rest"),
    Any("any"),
    Only("only");

    public final String value;

    Position(String value) {
      this.value = value;
    }
  }

  /**
   * Constructs an instance of {@link FoPageSequenceMaster}.
   *
   * @param name
   */
  public FoPageSequenceMaster(String name) {
    super("fo:page-sequence-master");
    this.name = name;
    set("master-name", name);
    this.master = FoNode.create("fo:repeatable-page-master-alternatives");
    super.addNode(this.master);
  }

  public final String getPageName() {
    return this.name;
  }

  public FoNode addBlank(String reference, BlankOrNot blankOrNot) {
    FoNode builder = FoNode.create("fo:conditional-page-master-reference");
    builder.set("master-reference", reference);
    builder.set("blank-or-not-blank", blankOrNot.value);
    setNode(builder);
    return this;
  }

  public FoNode addPage(String name, Position position, OddOrEven oddOrEven) {
    return addPage(name, position.value, oddOrEven.value);
  }

  public FoNode addPage(String name, String position, String oddOrEven) {
    FoNode builder = FoNode.create("fo:conditional-page-master-reference");
    builder.set("master-reference", name);
    builder.set("page-position", position);
    builder.set("odd-or-even", oddOrEven);
    setNode(builder);
    return this;
  }

  /**
   * Add a new child {@link FoNode}.
   *
   * @param node
   */
  public FoPageSequenceMaster setNode(FoNode node) {
    this.master.addNode(node);
    return this;
  }
}
