package com.sixstringcitizen.chordtabber.voicing;

import java.util.Arrays;
import java.util.Objects;

/**
 * One guitar fingering. Frets are ordered low E to high e; -1 means muted.
 */
public final class ChordVoicing {

    private final String name;
    private final String chordName;
    private final int[] frets;

    public ChordVoicing(String name, String chordName, int[] frets) {
        this.name = Objects.requireNonNull(name, "name");
        this.chordName = Objects.requireNonNull(chordName, "chordName");
        if (frets.length != 6) {
            throw new IllegalArgumentException("Expected 6 string frets");
        }
        this.frets = frets.clone();
    }

    public String name() {
        return name;
    }

    public String chordName() {
        return chordName;
    }

    public int[] frets() {
        return frets.clone();
    }

    public int fretForString(int stringIndex) {
        return frets[stringIndex];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ChordVoicing other)) {
            return false;
        }
        return name.equals(other.name)
                && chordName.equals(other.chordName)
                && Arrays.equals(frets, other.frets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, chordName);
        result = 31 * result + Arrays.hashCode(frets);
        return result;
    }
}
