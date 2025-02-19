// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

/**
 * The {@link CodeToken} class.
 */
enum CodeToken {

    TEXT("#000000"),
    COMMENT("#808080"),

    DEFINES("#469BA5"),
    KEYWORD("#9d1a92"),
    SECTION("#ff0008"),
    PARAMETER("#000082"),

    NAME("#a9142b"),
    VALUE("#007000"),

    JSON_NAME("#a9142b"),
    JSON_VALUE("#078967"),
    JSON_TEXT("#0758a0"),

    YAML_ATTR("#f92370"),
    YAML_VALUE("#e3d774"),

    YAML_COLOR("#f8f8ee"),
    YAML_COMMENT("#90918b"),
    YAML_BACKGROUND("#282923");

    public final String COLOR;

    /**
     * Create a literal with a color
     *
     * @param color
     */
    CodeToken(String color) {
        this.COLOR = color;
    }
}
