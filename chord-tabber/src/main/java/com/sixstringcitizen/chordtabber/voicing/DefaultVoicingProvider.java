package com.sixstringcitizen.chordtabber.voicing;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.model.SeventhType;
import com.sixstringcitizen.chordtabber.model.Triad;
import com.sixstringcitizen.chordtabber.theory.ChordTones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Supplies common open and CAGED default fingerings, validated against chord tones.
 */
public final class DefaultVoicingProvider {

    private static final int[] OPEN_MIDI = {40, 45, 50, 55, 59, 64};

    private static final Map<String, int[]> OPEN_SHAPES = Map.ofEntries(
            Map.entry("C", frets(-1, 3, 2, 0, 1, 0)),
            Map.entry("Cm", frets(-1, 3, 5, 5, 4, 3)),
            Map.entry("C7", frets(-1, 3, 2, 3, 1, 0)),
            Map.entry("Cmaj7", frets(-1, 3, 2, 0, 0, 0)),
            Map.entry("Cm7", frets(-1, 3, 5, 3, 4, 3)),
            Map.entry("D", frets(-1, -1, 0, 2, 3, 2)),
            Map.entry("Dm", frets(-1, -1, 0, 2, 3, 1)),
            Map.entry("D7", frets(-1, -1, 0, 2, 1, 2)),
            Map.entry("Dmaj7", frets(-1, -1, 0, 2, 2, 2)),
            Map.entry("Dm7", frets(-1, -1, 0, 2, 1, 1)),
            Map.entry("E", frets(0, 2, 2, 1, 0, 0)),
            Map.entry("Em", frets(0, 2, 2, 0, 0, 0)),
            Map.entry("E7", frets(0, 2, 0, 1, 0, 0)),
            Map.entry("Em7", frets(0, 2, 0, 0, 0, 0)),
            Map.entry("F", frets(1, 3, 3, 2, 1, 1)),
            Map.entry("Fm", frets(1, 3, 3, 1, 1, 1)),
            Map.entry("F7", frets(1, 3, 1, 2, 1, 1)),
            Map.entry("G", frets(3, 2, 0, 0, 0, 3)),
            Map.entry("G7", frets(3, 2, 0, 0, 0, 1)),
            Map.entry("Gm", frets(3, 5, 5, 3, 3, 3)),
            Map.entry("Gmaj7", frets(3, 2, 0, 0, 0, 2)),
            Map.entry("Gm7", frets(3, 5, 3, 3, 3, 3)),
            Map.entry("A", frets(-1, 0, 2, 2, 2, 0)),
            Map.entry("Am", frets(-1, 0, 2, 2, 1, 0)),
            Map.entry("A7", frets(-1, 0, 2, 0, 2, 0)),
            Map.entry("Am7", frets(-1, 0, 2, 0, 1, 0)),
            Map.entry("Amaj7", frets(-1, 0, 2, 1, 0, 0)),
            Map.entry("B", frets(-1, 2, 4, 4, 4, 2)),
            Map.entry("Bm", frets(-1, 2, 4, 4, 3, 2)),
            Map.entry("B7", frets(-1, 2, 1, 2, 0, 2)),
            Map.entry("Bm7", frets(-1, 2, 0, 2, 0, 2)),
            Map.entry("Bmaj7", frets(-1, 2, 1, 1, 0, 2)),
            Map.entry("C6", frets(-1, 3, 2, 2, 1, 0)),
            Map.entry("Am6", frets(-1, 0, 2, 2, 2, 0)),
            Map.entry("Dsus2", frets(-1, -1, 0, 2, 3, 0)),
            Map.entry("Dsus4", frets(-1, -1, 0, 2, 3, 3)),
            Map.entry("Asus2", frets(-1, 0, 2, 2, 0, 0)),
            Map.entry("Asus4", frets(-1, 0, 2, 2, 3, 0)),
            Map.entry("Esus4", frets(0, 2, 2, 2, 0, 0)),
            Map.entry("A5", frets(-1, 0, 2, -1, -1, -1)),
            Map.entry("E5", frets(0, 2, -1, -1, -1, -1)),
            Map.entry("G5", frets(3, 5, 5, -1, -1, -1))
    );

    public List<ChordVoicing> findDefaults(Chord chord, ChordTones chordTones) {
        Set<String> seen = new LinkedHashSet<>();
        List<ChordVoicing> defaults = new ArrayList<>();
        String chordName = chord.originalSymbol();

        addIfValid(defaults, seen, chordName, "Open voicing", OPEN_SHAPES.get(ChordSymbolFormatter.format(chord)), chordTones);

        for (int[] frets : movableShapes(chord)) {
            addIfValid(defaults, seen, chordName, movableLabel(chord, frets), frets, chordTones);
            if (defaults.size() >= 3) {
                break;
            }
        }

        return defaults;
    }

    private static void addIfValid(
            List<ChordVoicing> defaults,
            Set<String> seen,
            String chordName,
            String label,
            int[] frets,
            ChordTones chordTones) {
        if (frets == null || !isValidDefault(frets, chordTones)) {
            return;
        }
        String key = Arrays.toString(frets);
        if (!seen.add(key)) {
            return;
        }
        defaults.add(new ChordVoicing(label, chordName, frets));
    }

    private static boolean isValidDefault(int[] frets, ChordTones chordTones) {
        return VoicingGenerator.isPlayable(frets) && coversEssential(frets, chordTones.essential());
    }

    private static boolean coversEssential(int[] frets, Set<PitchClass> essential) {
        Set<PitchClass> found = new LinkedHashSet<>();
        for (int string = 0; string < 6; string++) {
            if (frets[string] >= 0) {
                found.add(pitchClassAt(string, frets[string]));
            }
        }
        return found.containsAll(essential);
    }

    private static List<int[]> movableShapes(Chord chord) {
        List<int[]> shapes = new ArrayList<>();
        if (!chord.alterations().isEmpty() || !chord.omit().isEmpty() || !chord.extensions().isEmpty()
                || !chord.addedNotes().isEmpty()) {
            return shapes;
        }

        int eRoot = fretForRootOnString(0, chord.root());
        int aRoot = fretForRootOnString(1, chord.root());

        if (chord.triad() == Triad.MAJOR && chord.seventh().isEmpty()) {
            addShape(shapes, eShapeMajor(eRoot));
            addShape(shapes, aShapeMajor(aRoot));
        } else if (chord.triad() == Triad.MINOR && chord.seventh().isEmpty()) {
            addShape(shapes, eShapeMinor(eRoot));
            addShape(shapes, aShapeMinor(aRoot));
        } else if (chord.triad() == Triad.MAJOR && chord.seventh().orElse(null) == SeventhType.DOMINANT) {
            addShape(shapes, eShapeDominant7(eRoot));
            addShape(shapes, aShapeDominant7(aRoot));
        } else if (chord.triad() == Triad.MAJOR && chord.seventh().orElse(null) == SeventhType.MAJOR) {
            addShape(shapes, eShapeMajor7(eRoot));
            addShape(shapes, aShapeMajor7(aRoot));
        } else if (chord.triad() == Triad.MINOR && chord.seventh().orElse(null) == SeventhType.MINOR) {
            addShape(shapes, eShapeMinor7(eRoot));
            addShape(shapes, aShapeMinor7(aRoot));
        } else if (chord.triad() == Triad.DIMINISHED) {
            addShape(shapes, eShapeDiminished(eRoot));
        } else if (chord.triad() == Triad.SUS4) {
            addShape(shapes, aShapeSus4(aRoot));
        } else if (chord.triad() == Triad.SUS2) {
            addShape(shapes, aShapeSus2(aRoot));
        }

        return shapes;
    }

    private static void addShape(List<int[]> shapes, int[] frets) {
        if (frets != null) {
            shapes.add(frets);
        }
    }

    private static int[] eShapeMajor(int rootFret) {
        return shape(rootFret, rootFret + 2, rootFret + 2, rootFret + 1, rootFret, rootFret);
    }

    private static int[] eShapeMinor(int rootFret) {
        return shape(rootFret, rootFret + 2, rootFret + 2, rootFret, rootFret, rootFret);
    }

    private static int[] eShapeDominant7(int rootFret) {
        return shape(rootFret, rootFret + 2, rootFret, rootFret + 1, rootFret, rootFret);
    }

    private static int[] eShapeMajor7(int rootFret) {
        return shape(rootFret, rootFret + 2, rootFret + 1, rootFret + 1, rootFret, rootFret);
    }

    private static int[] eShapeMinor7(int rootFret) {
        return shape(rootFret, rootFret + 2, rootFret, rootFret, rootFret, rootFret);
    }

    private static int[] eShapeDiminished(int rootFret) {
        return shape(rootFret, rootFret + 1, rootFret + 2, rootFret + 1, -1, -1);
    }

    private static int[] aShapeMajor(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret + 2, rootFret + 2, rootFret);
    }

    private static int[] aShapeMinor(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret + 2, rootFret + 1, rootFret);
    }

    private static int[] aShapeDominant7(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret, rootFret + 2, rootFret);
    }

    private static int[] aShapeMajor7(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret + 1, rootFret + 2, rootFret);
    }

    private static int[] aShapeMinor7(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret, rootFret + 1, rootFret);
    }

    private static int[] aShapeSus4(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret + 2, rootFret + 3, rootFret);
    }

    private static int[] aShapeSus2(int rootFret) {
        return shape(-1, rootFret, rootFret + 2, rootFret + 2, rootFret, rootFret);
    }

    private static int[] shape(int... frets) {
        for (int fret : frets) {
            if (fret > VoicingGenerator.MAX_FRET) {
                return null;
            }
        }
        return frets;
    }

    private static int fretForRootOnString(int string, PitchClass root) {
        int open = PitchClass.fromSemitone(OPEN_MIDI[string]).semitone();
        return Math.floorMod(root.semitone() - open, 12);
    }

    private static PitchClass pitchClassAt(int string, int fret) {
        return PitchClass.fromSemitone(OPEN_MIDI[string] + fret);
    }

    private static String movableLabel(Chord chord, int[] frets) {
        if (frets[0] >= 0) {
            return "E-shape voicing";
        }
        if (frets[1] >= 0) {
            return "A-shape voicing";
        }
        return "Barre voicing";
    }

    private static int[] frets(int e, int a, int d, int g, int b, int highE) {
        return new int[] {e, a, d, g, b, highE};
    }
}
