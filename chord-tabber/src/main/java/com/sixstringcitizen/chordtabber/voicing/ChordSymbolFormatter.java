package com.sixstringcitizen.chordtabber.voicing;

import com.sixstringcitizen.chordtabber.model.AddedNote;
import com.sixstringcitizen.chordtabber.model.Alteration;
import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.Extension;
import com.sixstringcitizen.chordtabber.model.Omit;
import com.sixstringcitizen.chordtabber.model.SeventhType;
import com.sixstringcitizen.chordtabber.model.Triad;

/**
 * Builds a normalized chord symbol for default voicing lookup.
 */
final class ChordSymbolFormatter {

    private ChordSymbolFormatter() {
    }

    static String format(Chord chord) {
        StringBuilder builder = new StringBuilder(chord.root().defaultName());

        if (chord.triad() == Triad.DIMINISHED && chord.seventh().orElse(null) == SeventhType.MINOR) {
            builder.append("m7b5");
            appendSlash(builder, chord);
            return builder.toString();
        }

        appendQuality(builder, chord);
        for (Omit omit : chord.omit()) {
            builder.append(omit == Omit.NO3 ? "no3" : "no5");
        }
        for (Alteration alteration : chord.alterations()) {
            builder.append(switch (alteration) {
                case FLAT5 -> "b5";
                case SHARP5 -> "#5";
                case FLAT9 -> "b9";
                case SHARP9 -> "#9";
                case SHARP11 -> "#11";
                case FLAT13 -> "b13";
            });
        }
        chord.bass().ifPresent(bass -> builder.append('/').append(bass.defaultName()));
        return builder.toString();
    }

    private static void appendSlash(StringBuilder builder, Chord chord) {
        chord.bass().ifPresent(bass -> builder.append('/').append(bass.defaultName()));
    }

    private static void appendQuality(StringBuilder builder, Chord chord) {
        if (chord.triad() == Triad.DIMINISHED && chord.seventh().orElse(null) == SeventhType.DIMINISHED) {
            builder.append("dim7");
            return;
        }

        switch (chord.triad()) {
            case MINOR -> builder.append('m');
            case DIMINISHED -> builder.append("dim");
            case AUGMENTED -> builder.append("aug");
            case SUS2 -> builder.append("sus2");
            case SUS4 -> builder.append("sus4");
            case POWER5 -> builder.append('5');
            case MAJOR -> { }
        }

        chord.seventh().ifPresent(seventh -> {
            switch (seventh) {
                case DOMINANT -> builder.append('7');
                case MAJOR -> builder.append("maj7");
                case MINOR -> builder.append("m7");
                case DIMINISHED -> builder.append("dim7");
            }
        });

        for (Extension extension : chord.extensions()) {
            switch (extension) {
                case SIX -> builder.append('6');
                case NINE -> builder.append('9');
                case ELEVEN -> builder.append("11");
                case THIRTEEN -> builder.append("13");
            }
        }

        for (AddedNote addedNote : chord.addedNotes()) {
            switch (addedNote) {
                case ADD2 -> builder.append("add2");
                case ADD4 -> builder.append("add4");
                case ADD9 -> builder.append("add9");
                case ADD11 -> builder.append("add11");
                case ADD13 -> builder.append("add13");
            }
        }
    }
}
