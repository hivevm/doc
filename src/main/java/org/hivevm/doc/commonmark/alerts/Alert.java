// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.alerts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Alert {

    NOTE,
    SUCCESS,
    WARNING,
    ERROR;

    private static final Pattern ALERT_LINE = Pattern.compile("^(!(!?)(\\w+)\\s+).*");

    static Matcher matcher(CharSequence text) {
        return Alert.ALERT_LINE.matcher(text);
    }

    static Alert of(String text) {
        if ((text == null) || (text.trim().length() == 0)) {
            return NOTE;
        }

        switch (text.toLowerCase().charAt(0)) {
            case 'e': // Red, Error
                return ERROR;
            case 'w': // Yellow, Warning
                return WARNING;
            case 's': // Green, Success
                return SUCCESS;
            case 'n': // Blue, Info
            default:
                return NOTE;
        }
    }
}
