package com.wl.orders;

import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// Small display helpers shared by the screens.
public class Format {

    private static final DecimalFormat MONEY =
            new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    // "14100.00" -> "Rs. 14,100"
    public static String money(String raw) {
        try {
            return "Rs. " + MONEY.format(Double.parseDouble(raw));
        } catch (NumberFormatException e) {
            return "Rs. " + raw;
        }
    }

    // "2026-10-05 17:42:10" -> "2026-10-05 17:42"
    public static String dateTime(String raw) {
        if (raw != null && raw.length() >= 16) {
            return raw.substring(0, 16);
        }
        return raw == null ? "" : raw;
    }

    public static String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1).trim() + "…";
    }

    // JSONObject.optString() returns the text "null" for JSON nulls - this returns "" instead.
    public static String str(JSONObject o, String key) {
        return o.isNull(key) ? "" : o.optString(key, "");
    }
}
