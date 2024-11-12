// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.fop.nodes;

import org.hivevm.doc.fop.nodes.set.FoPadding;

/**
 * The {@link FoLeader} class.
 */
public class FoLeader extends FoNode implements FoPadding<FoLeader> {

  public FoLeader() {
    super("fo:leader");
  }

  public FoLeader setPattern(String value) {
    set("leader-pattern", value);
    return this;
  }

  public FoLeader setWidth(String value) {
    set("leader-pattern-width", value);
    return this;
  }

  public FoLeader setAlign(String value) {
    set("leader-alignment", value);
    return this;
  }

  public FoLeader setLength(String value) {
    set("leader-length", value);
    return this;
  }

  public FoLeader setColor(String value) {
    set("color", value);
    return this;
  }

  public FoLeader setRuleThickness(String value) {
    set("rule-thickness", value);
    return this;
  }
}
