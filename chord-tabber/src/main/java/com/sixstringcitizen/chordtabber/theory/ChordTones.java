package com.sixstringcitizen.chordtabber.theory;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.PitchClass;

import java.util.List;
import java.util.Set;

public final class ChordTones {

    private final Chord chord;
    private final List<PitchClass> notes;
    private final List<Integer> intervals;
    private final Set<PitchClass> essential;

    public ChordTones(Chord chord, List<PitchClass> notes, List<Integer> intervals, Set<PitchClass> essential) {
        this.chord = chord;
        this.notes = List.copyOf(notes);
        this.intervals = List.copyOf(intervals);
        this.essential = Set.copyOf(essential);
    }

    public Chord chord() {
        return chord;
    }

    public List<PitchClass> notes() {
        return notes;
    }

    public List<Integer> intervals() {
        return intervals;
    }

    public Set<PitchClass> essential() {
        return essential;
    }

    public Set<PitchClass> allPitchClasses() {
        return Set.copyOf(notes);
    }
}
