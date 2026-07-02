package com.sixstringcitizen.chordtabber.parse;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.Extension;
import com.sixstringcitizen.chordtabber.model.Omit;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.model.SeventhType;
import com.sixstringcitizen.chordtabber.model.Triad;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChordParserTest {

    private final ChordParser parser = new ChordParser();

    @Test
    void parsesMajorSeventh() {
        Chord chord = parser.parse("Cmaj7");
        assertEquals(PitchClass.C, chord.root());
        assertEquals(Triad.MAJOR, chord.triad());
        assertEquals(SeventhType.MAJOR, chord.seventh().orElseThrow());
    }

    @Test
    void parsesMinor() {
        Chord chord = parser.parse("Am");
        assertEquals(PitchClass.A, chord.root());
        assertEquals(Triad.MINOR, chord.triad());
        assertTrue(chord.seventh().isEmpty());
    }

    @Test
    void parsesDominantSeventhWithAlteration() {
        Chord chord = parser.parse("C7#9");
        assertEquals(SeventhType.DOMINANT, chord.seventh().orElseThrow());
        assertTrue(chord.alterations().contains(com.sixstringcitizen.chordtabber.model.Alteration.SHARP9));
    }

    @Test
    void parsesSlashChord() {
        Chord chord = parser.parse("C/E");
        assertEquals(PitchClass.C, chord.root());
        assertEquals(PitchClass.E, chord.bass().orElseThrow());
    }

    @Test
    void parsesHalfDiminishedAlias() {
        Chord chord = parser.parse("Cm7b5");
        assertEquals(Triad.DIMINISHED, chord.triad());
        assertEquals(SeventhType.MINOR, chord.seventh().orElseThrow());
    }

    @Test
    void parsesOmissions() {
        Chord chord = parser.parse("Cmaj7no3");
        assertTrue(chord.omit().contains(Omit.NO3));
    }

    @Test
    void parsesExtensions() {
        Chord chord = parser.parse("Cmaj9");
        assertTrue(chord.extensions().contains(Extension.NINE));
        assertEquals(SeventhType.MAJOR, chord.seventh().orElseThrow());
    }
}
