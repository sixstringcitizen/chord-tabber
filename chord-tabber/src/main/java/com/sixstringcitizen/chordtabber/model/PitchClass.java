package com.sixstringcitizen.chordtabber.model;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Twelve pitch classes used internally for all chord math.
 */
public enum PitchClass {
    C(0, "C", "C"),
    C_SHARP(1, "C#", "Db"),
    D(2, "D", "D"),
    D_SHARP(3, "D#", "Eb"),
    E(4, "E", "E"),
    F(5, "F", "F"),
    F_SHARP(6, "F#", "Gb"),
    G(7, "G", "G"),
    G_SHARP(8, "G#", "Ab"),
    A(9, "A", "A"),
    A_SHARP(10, "A#", "Bb"),
    B(11, "B", "B");

    private static final Map<String, PitchClass> BY_NAME = Map.ofEntries(
            Map.entry("C", C),
            Map.entry("B#", C),
            Map.entry("C#", C_SHARP),
            Map.entry("DB", D_SHARP),
            Map.entry("D", D),
            Map.entry("D#", D_SHARP),
            Map.entry("EB", D_SHARP),
            Map.entry("E", E),
            Map.entry("FB", E),
            Map.entry("E#", F),
            Map.entry("F", F),
            Map.entry("F#", F_SHARP),
            Map.entry("GB", F_SHARP),
            Map.entry("G", G),
            Map.entry("G#", G_SHARP),
            Map.entry("AB", G_SHARP),
            Map.entry("A", A),
            Map.entry("A#", A_SHARP),
            Map.entry("BB", A_SHARP),
            Map.entry("B", B),
            Map.entry("CB", B)
    );

    private final int semitone;
    private final String sharpName;
    private final String flatName;

    PitchClass(int semitone, String sharpName, String flatName) {
        this.semitone = semitone;
        this.sharpName = sharpName;
        this.flatName = flatName;
    }

    public int semitone() {
        return semitone;
    }

    public PitchClass transpose(int semitones) {
        int value = Math.floorMod(semitone + semitones, 12);
        for (PitchClass pc : values()) {
            if (pc.semitone == value) {
                return pc;
            }
        }
        throw new IllegalStateException("Invalid semitone: " + value);
    }

    public static PitchClass fromSemitone(int semitone) {
        return values()[Math.floorMod(semitone, 12)];
    }

    public static PitchClass parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Note name is blank");
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        PitchClass pc = BY_NAME.get(normalized);
        if (pc == null) {
            throw new IllegalArgumentException("Unknown note name: " + raw);
        }
        return pc;
    }

    public String spell(PitchClass preferContext) {
        return spellFromInterval(preferContext, 0);
    }

    public String spellFromInterval(PitchClass root, int intervalFromRoot) {
        boolean preferFlat = switch (intervalFromRoot) {
            case 10, 13, 20 -> true;
            case 15, 18 -> false;
            default -> {
                int reduced = Math.floorMod(intervalFromRoot, 12);
                yield switch (reduced) {
                    case 1, 3, 6, 8, 10 -> true;
                    default -> root != null && prefersFlatKey(root);
                };
            }
        };
        if (preferFlat && !flatName.equals(sharpName)) {
            return flatName;
        }
        return sharpName;
    }

    private static boolean prefersFlatKey(PitchClass root) {
        return switch (root) {
            case F, D_SHARP, A_SHARP, G_SHARP, C_SHARP, F_SHARP -> true;
            default -> false;
        };
    }

    public String defaultName() {
        return sharpName;
    }
}
