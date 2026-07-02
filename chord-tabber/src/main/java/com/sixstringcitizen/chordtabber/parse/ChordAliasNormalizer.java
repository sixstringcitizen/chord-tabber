package com.sixstringcitizen.chordtabber.parse;

final class ChordAliasNormalizer {

    private ChordAliasNormalizer() {
    }

    static String normalize(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Chord name is blank");
        }
        String s = input.trim()
                .replace("Δ", "maj")
                .replace("°", "dim")
                .replace("ø", "m7b5")
                .replace("−", "-");
        s = s.replaceAll("\\s+", "");
        s = s.replace("(no3)", "no3")
                .replace("(no5)", "no5")
                .replace("(", "")
                .replace(")", "");
        return s;
    }
}
