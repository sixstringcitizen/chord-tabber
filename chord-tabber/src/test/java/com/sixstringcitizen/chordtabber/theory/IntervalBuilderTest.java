package com.sixstringcitizen.chordtabber.theory;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.parse.ChordParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalBuilderTest {

    private final ChordParser parser = new ChordParser();
    private final IntervalBuilder intervalBuilder = new IntervalBuilder();

    @Test
    void buildsMajorSeventhTones() {
        Chord chord = parser.parse("Cmaj7");
        ChordTones tones = intervalBuilder.build(chord);
        assertEquals(List.of("C", "E", "G", "B"), spell(tones));
    }

    @Test
    void buildsMinorTones() {
        Chord chord = parser.parse("Am");
        ChordTones tones = intervalBuilder.build(chord);
        assertEquals(List.of("A", "C", "E"), spell(tones));
    }

    @Test
    void buildsDominantSeventhTones() {
        Chord chord = parser.parse("G7");
        ChordTones tones = intervalBuilder.build(chord);
        assertEquals(List.of("G", "B", "D", "F"), spell(tones));
    }

    @Test
    void buildsAlteredDominant() {
        Chord chord = parser.parse("C7#9");
        ChordTones tones = intervalBuilder.build(chord);
        assertEquals(List.of("C", "E", "G", "Bb", "D#"), spell(tones));
    }

    private static List<String> spell(ChordTones tones) {
        List<String> names = new ArrayList<>();
        List<Integer> intervals = tones.intervals();
        for (int i = 0; i < tones.notes().size(); i++) {
            names.add(tones.notes().get(i).spellFromInterval(tones.chord().root(), intervals.get(i)));
        }
        return names;
    }
}
