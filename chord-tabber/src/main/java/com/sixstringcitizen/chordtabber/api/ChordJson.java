package com.sixstringcitizen.chordtabber.api;

import com.sixstringcitizen.chordtabber.ChordResult;
import com.sixstringcitizen.chordtabber.ChordTabber;
import com.sixstringcitizen.chordtabber.voicing.ChordVoicing;

import java.util.List;

public final class ChordJson {

    private ChordJson() {
    }

    public static String toJson(ChordResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"chordName\":").append(string(result.chordName()));
        builder.append(",\"notes\":").append(stringArray(result.notes()));
        builder.append(",\"voicings\":[");
        for (int i = 0; i < result.voicings().size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            appendVoicing(builder, result.voicings().get(i), result.tabs().get(i));
        }
        builder.append("]}");
        return builder.toString();
    }

    public static String describe(String chordName) {
        ChordTabber tabber = new ChordTabber();
        return toJson(tabber.describe(chordName));
    }

    public static String error(String message) {
        return "{\"error\":" + string(message) + "}";
    }

    private static void appendVoicing(StringBuilder builder, ChordVoicing voicing, String tab) {
        builder.append("{\"name\":").append(string(voicing.name()));
        builder.append(",\"frets\":").append(intArray(voicing.frets()));
        builder.append(",\"tab\":").append(string(tab));
        builder.append('}');
    }

    private static String stringArray(List<String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(string(values.get(i)));
        }
        builder.append(']');
        return builder.toString();
    }

    private static String intArray(int[] values) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(values[i]);
        }
        builder.append(']');
        return builder.toString();
    }

    private static String string(String value) {
        StringBuilder builder = new StringBuilder(value.length() + 2);
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\\' -> builder.append("\\\\");
                case '"' -> builder.append("\\\"");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append(ch);
            }
        }
        builder.append('"');
        return builder.toString();
    }
}
