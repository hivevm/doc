package org.hivevm.doc.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Unit(double value, String unit) {

    public String asString() {
        if (this == NIL)
            return null;
        if (unit == null)
            return Double.toString(value);
        return value + unit;
    }

    public static final Unit NIL = new Unit(0, null);

    public static Unit parse(String value) {
        Pattern pattern = Pattern.compile("([\\d\\-\\.]+)([a-zA-Z]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return new Unit(Double.valueOf(matcher.group(1)), matcher.group(2));
        }
        return NIL;
    }
}
