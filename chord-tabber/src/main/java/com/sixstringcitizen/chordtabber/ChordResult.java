package com.sixstringcitizen.chordtabber;

import com.sixstringcitizen.chordtabber.voicing.ChordVoicing;

import java.util.List;

public final class ChordResult {

    private final String chordName;
    private final List<String> notes;
    private final List<ChordVoicing> voicings;
    private final List<String> tabs;

    public ChordResult(String chordName, List<String> notes, List<ChordVoicing> voicings, List<String> tabs) {
        this.chordName = chordName;
        this.notes = List.copyOf(notes);
        this.voicings = List.copyOf(voicings);
        this.tabs = List.copyOf(tabs);
    }

    public String chordName() {
        return chordName;
    }

    public List<String> notes() {
        return notes;
    }

    public List<ChordVoicing> voicings() {
        return voicings;
    }

    public List<String> tabs() {
        return tabs;
    }
}
