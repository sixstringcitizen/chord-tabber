package com.sixstringcitizen.chordtabber;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChordTabberTest {

    private final ChordTabber tabber = new ChordTabber();

    @Test
    void describesMajorSeventh() {
        ChordResult result = tabber.describe("Cmaj7");
        assertEquals("Cmaj7", result.chordName());
        assertEquals(List.of("C", "E", "G", "B"), result.notes());
        assertEquals(3, result.voicings().size());
        assertTrue(containsVoicing(result, -1, 3, 2, 0, 0, 0));
        assertTrue(result.tabs().get(0).contains("e|--"));
    }

    @Test
    void describesMinorChordWithDefaultFirst() {
        ChordResult result = tabber.describe("Am");
        assertEquals(List.of("A", "C", "E"), result.notes());
        assertEquals(3, result.voicings().size());
        assertTrue(containsVoicing(result, -1, 0, 2, 2, 1, 0));
        assertEquals("Open voicing", result.voicings().get(0).name());
    }

    @Test
    void describesDominantSeventhWithDefaultFirst() {
        ChordResult result = tabber.describe("G7");
        assertEquals(List.of("G", "B", "D", "F"), result.notes());
        assertEquals(3, result.voicings().size());
        assertTrue(containsVoicing(result, 3, 2, 0, 0, 0, 1));
        assertEquals("Open voicing", result.voicings().get(0).name());
    }

    private static boolean containsVoicing(ChordResult result, int... frets) {
        for (var voicing : result.voicings()) {
            if (Arrays.equals(voicing.frets(), frets)) {
                return true;
            }
        }
        return false;
    }
}
