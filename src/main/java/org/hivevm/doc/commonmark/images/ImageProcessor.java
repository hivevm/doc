// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.images;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

class ImageProcessor implements DelimiterProcessor {

  // Only allow a defined set of attributes to be used.
  private static final Set<String> SUPPORTED_ATTRIBUTES =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("width", "height", "align")));

  @Override
  public int getMinLength() {
    return 1;
  }

  @Override
  public char getOpeningCharacter() {
    return '{';
  }

  @Override
  public char getClosingCharacter() {
    return '}';
  }

  /*
   * @see org.commonmark.parser.delimiter.DelimiterProcessor#process(org.commonmark.
   * parser.delimiter. DelimiterRun, org.commonmark.parser.delimiter.DelimiterRun)
   */
  @Override
  public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
    Text opener = openingRun.getOpener();
    Text closer = closingRun.getCloser();

    // Check if the attributes can be applied - if the previous node is an Image,
    // and if all the
    // attributes are in
    // the set of SUPPORTED_ATTRIBUTES
    if (opener.getPrevious() instanceof Image) {
      boolean canApply = true;
      List<Node> toUnlink = new ArrayList<>();

      Map<String, String> attributesMap = new LinkedHashMap<>();
      Node tmp = opener.getNext();
      while ((tmp != null) && (tmp != closer)) {
        Node next = tmp.getNext();
        // Only Text nodes can be used for attributes
        if (tmp instanceof Text) {
          String attributes = ((Text) tmp).getLiteral();
          for (String s : attributes.split("\\s+")) {
            String[] attribute = s.split("=");
            if ((attribute.length > 1) && ImageProcessor.SUPPORTED_ATTRIBUTES.contains(attribute[0].toLowerCase())) {
              attributesMap.put(attribute[0], attribute[1]);
              // The tmp node can be unlinked, as we have retrieved its value.
              toUnlink.add(tmp);
            } else {
              // This attribute is not supported, so break here (no need to check any further
              // ones).
              canApply = false;
              break;
            }
          }
        } else {
          // This node type is not supported, so break here (no need to check any further
          // ones).
          canApply = false;
          break;
        }
        tmp = next;
      }

      // Only if all of the above checks pass can the attributes be applied.
      if (canApply) {
        // Unlink the tmp nodes
        for (Node node : toUnlink) {
          node.unlink();
        }

        if (attributesMap.size() > 0) {
          ImageAttributes imageAttributes = new ImageAttributes(attributesMap);

          // The new node is added as a child of the image node to which the attributes
          // apply.
          Node nodeToStyle = opener.getPrevious();
          nodeToStyle.appendChild(imageAttributes);
        }
        return 1;
      }
    }

    // If we got here then the attributes cannot be applied, so fallback to leaving
    // the text
    // unchanged.
    // Need to add back the opening and closing characters (which are removed
    // elsewhere).
    if (opener.getPrevious() == null) {
      opener.getParent().prependChild(new Text("" + getOpeningCharacter()));
    } else {
      opener.getPrevious().insertAfter(new Text("" + getOpeningCharacter()));
    }
    closer.insertAfter(new Text("" + getClosingCharacter()));
    return 1;
  }
}
