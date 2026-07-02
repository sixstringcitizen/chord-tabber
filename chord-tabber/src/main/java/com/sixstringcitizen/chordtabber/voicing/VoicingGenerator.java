package com.sixstringcitizen.chordtabber.voicing;

import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.theory.ChordTones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Searches the fretboard for playable voicings that cover required chord tones.
 */
public final class VoicingGenerator {

    public static final int MAX_CANDIDATES = 30;
    public static final int MAX_FRET = 15;
    public static final int MAX_FRET_SPAN = 4;

    private static final int[] OPEN_MIDI = {40, 45, 50, 55, 59, 64};
    private static final String[] STRING_NAMES = {"E", "A", "D", "G", "B", "e"};

    public List<ChordVoicing> generate(ChordTones chordTones) {
        Set<PitchClass> allowed = chordTones.allPitchClasses();
        Set<PitchClass> essential = chordTones.essential();
        String chordName = chordTones.chord().originalSymbol();

        List<List<int[]>> perStringChoices = new ArrayList<>(6);
        for (int string = 0; string < 6; string++) {
            perStringChoices.add(choicesForString(string, allowed));
        }

        List<ChordVoicing> candidates = new ArrayList<>();
        int[] current = new int[] {-1, -1, -1, -1, -1, -1};
        search(0, current, perStringChoices, essential, chordName, candidates);
        return candidates;
    }

    private static List<int[]> choicesForString(int string, Set<PitchClass> allowed) {
        List<Integer> frets = new ArrayList<>();
        for (int fret = 0; fret <= MAX_FRET; fret++) {
            if (allowed.contains(pitchClassAt(string, fret))) {
                frets.add(fret);
            }
        }
        frets.add(-1);
        frets.sort((left, right) -> {
            if (left < 0) {
                return 1;
            }
            if (right < 0) {
                return -1;
            }
            return Integer.compare(left, right);
        });

        List<int[]> choices = new ArrayList<>(frets.size());
        for (int fret : frets) {
            choices.add(new int[] {fret});
        }
        return choices;
    }

    private static void search(
            int string,
            int[] current,
            List<List<int[]>> perStringChoices,
            Set<PitchClass> essential,
            String chordName,
            List<ChordVoicing> candidates) {
        if (candidates.size() >= MAX_CANDIDATES) {
            return;
        }
        if (string == 6) {
            if (!isPlayable(current) || !coversEssential(current, essential)) {
                return;
            }
            candidates.add(new ChordVoicing(labelVoicing(current), chordName, current.clone()));
            return;
        }

        for (int[] choice : perStringChoices.get(string)) {
            current[string] = choice[0];
            if (partiallyPlayable(current, string + 1)) {
                search(string + 1, current, perStringChoices, essential, chordName, candidates);
            }
        }
    }

    private static boolean coversEssential(int[] frets, Set<PitchClass> essential) {
        Set<PitchClass> found = new HashSet<>();
        for (int string = 0; string < 6; string++) {
            if (frets[string] >= 0) {
                found.add(pitchClassAt(string, frets[string]));
            }
        }
        return found.containsAll(essential);
    }

    private static boolean partiallyPlayable(int[] frets, int count) {
        int[] slice = Arrays.copyOf(frets, count);
        return isPlayable(slice);
    }

    static boolean isPlayable(int[] frets) {
        List<Integer> fretted = new ArrayList<>();
        int played = 0;
        for (int fret : frets) {
            if (fret >= 0) {
                played++;
                if (fret > 0) {
                    fretted.add(fret);
                }
            }
        }
        if (played == 0) {
            return false;
        }
        if (fretted.isEmpty()) {
            return true;
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int fret : fretted) {
            min = Math.min(min, fret);
            max = Math.max(max, fret);
        }
        if (max - min > MAX_FRET_SPAN) {
            return false;
        }
        Set<Integer> unique = new HashSet<>(fretted);
        return unique.size() <= 4;
    }

    private static PitchClass pitchClassAt(int string, int fret) {
        int midi = OPEN_MIDI[string] + fret;
        return PitchClass.fromSemitone(midi);
    }

    private static String labelVoicing(int[] frets) {
        boolean hasOpen = false;
        int lowestFret = Integer.MAX_VALUE;
        for (int fret : frets) {
            if (fret == 0) {
                hasOpen = true;
            }
            if (fret > 0) {
                lowestFret = Math.min(lowestFret, fret);
            }
        }
        if (hasOpen && lowestFret == Integer.MAX_VALUE) {
            return "Open voicing";
        }
        if (hasOpen) {
            return "Open-position voicing";
        }
        if (lowestFret <= 3) {
            return "Low-position voicing";
        }
        if (lowestFret <= 7) {
            return "Mid-neck voicing";
        }
        return "Upper-neck voicing";
    }

    public static PitchClass lowestPlayedPitchClass(int[] frets) {
        for (int string = 0; string < 6; string++) {
            if (frets[string] >= 0) {
                return pitchClassAt(string, frets[string]);
            }
        }
        return null;
    }
}
