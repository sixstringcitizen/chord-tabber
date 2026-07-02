package com.sixstringcitizen.chordtabber.theory;

import com.sixstringcitizen.chordtabber.model.AddedNote;
import com.sixstringcitizen.chordtabber.model.Alteration;
import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.Extension;
import com.sixstringcitizen.chordtabber.model.Omit;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.model.SeventhType;
import com.sixstringcitizen.chordtabber.model.Triad;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts parsed chords into ordered pitch-class spellings and interval sets.
 */
public final class IntervalBuilder {

    public ChordTones build(Chord chord) {
        Set<Integer> intervals = new LinkedHashSet<>();
        intervals.add(0);

        switch (chord.triad()) {
            case MAJOR -> {
                intervals.add(4);
                intervals.add(7);
            }
            case MINOR -> {
                intervals.add(3);
                intervals.add(7);
            }
            case DIMINISHED -> {
                intervals.add(3);
                intervals.add(6);
            }
            case AUGMENTED -> {
                intervals.add(4);
                intervals.add(8);
            }
            case SUS2 -> {
                intervals.add(2);
                intervals.add(7);
            }
            case SUS4 -> {
                intervals.add(5);
                intervals.add(7);
            }
            case POWER5 -> intervals.add(7);
        }

        chord.seventh().ifPresent(seventh -> intervals.add(seventhInterval(seventh)));

        for (Extension extension : chord.extensions()) {
            switch (extension) {
                case SIX -> intervals.add(9);
                case NINE -> intervals.add(14);
                case ELEVEN -> intervals.add(17);
                case THIRTEEN -> intervals.add(21);
            }
        }

        for (AddedNote addedNote : chord.addedNotes()) {
            switch (addedNote) {
                case ADD2 -> intervals.add(2);
                case ADD4 -> intervals.add(5);
                case ADD9 -> intervals.add(14);
                case ADD11 -> intervals.add(17);
                case ADD13 -> intervals.add(21);
            }
        }

        applyAlterations(intervals, chord.alterations());
        applyOmissions(intervals, chord);

        List<PitchClass> notes = new ArrayList<>();
        List<Integer> orderedIntervals = new ArrayList<>(intervals);
        orderedIntervals.sort(Integer::compareTo);
        for (int interval : orderedIntervals) {
            notes.add(chord.root().transpose(interval));
        }

        Set<PitchClass> essential = computeEssential(chord, orderedIntervals);
        return new ChordTones(chord, notes, orderedIntervals, essential);
    }

    private static int seventhInterval(SeventhType seventh) {
        return switch (seventh) {
            case DOMINANT, MINOR -> 10;
            case MAJOR -> 11;
            case DIMINISHED -> 9;
        };
    }

    private static void applyAlterations(Set<Integer> intervals, List<Alteration> alterations) {
        for (Alteration alteration : alterations) {
            switch (alteration) {
                case FLAT5 -> {
                    intervals.remove(7);
                    intervals.remove(6);
                    intervals.add(6);
                }
                case SHARP5 -> {
                    intervals.remove(7);
                    intervals.add(8);
                }
                case FLAT9 -> {
                    intervals.remove(14);
                    intervals.add(13);
                }
                case SHARP9 -> {
                    intervals.remove(14);
                    intervals.add(15);
                }
                case SHARP11 -> {
                    intervals.remove(17);
                    intervals.add(18);
                }
                case FLAT13 -> {
                    intervals.remove(21);
                    intervals.add(20);
                }
            }
        }
    }

    private static void applyOmissions(Set<Integer> intervals, Chord chord) {
        for (Omit omit : chord.omit()) {
            if (omit == Omit.NO3) {
                intervals.remove(3);
                intervals.remove(4);
            } else if (omit == Omit.NO5) {
                intervals.remove(7);
                intervals.remove(6);
                intervals.remove(8);
            }
        }
    }

    private static Set<PitchClass> computeEssential(Chord chord, List<Integer> orderedIntervals) {
        Set<PitchClass> essential = new LinkedHashSet<>();
        essential.add(chord.root());

        boolean hasThird = orderedIntervals.contains(3) || orderedIntervals.contains(4);
        boolean hasSeventh = orderedIntervals.contains(9)
                || orderedIntervals.contains(10)
                || orderedIntervals.contains(11);

        if (hasThird && chord.triad() != Triad.SUS2 && chord.triad() != Triad.SUS4) {
            for (int interval : orderedIntervals) {
                if (interval == 3 || interval == 4) {
                    essential.add(chord.root().transpose(interval));
                }
            }
        }

        if (hasSeventh) {
            for (int interval : orderedIntervals) {
                if (interval == 9 || interval == 10 || interval == 11) {
                    essential.add(chord.root().transpose(interval));
                }
            }
        }

        for (Alteration alteration : chord.alterations()) {
            int interval = switch (alteration) {
                case FLAT5, SHARP5 -> 0;
                case FLAT9 -> 13;
                case SHARP9 -> 15;
                case SHARP11 -> 18;
                case FLAT13 -> 20;
            };
            if (interval > 0) {
                essential.add(chord.root().transpose(interval));
            }
        }

        return essential;
    }
}
