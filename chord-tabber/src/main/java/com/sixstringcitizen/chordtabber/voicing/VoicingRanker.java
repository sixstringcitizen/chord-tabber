package com.sixstringcitizen.chordtabber.voicing;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.PitchClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ranks generated voicings by playability heuristics, with defaults pinned first.
 */
public final class VoicingRanker {

    public static final int TOP_COUNT = 3;

    public List<ChordVoicing> rank(Chord chord, List<ChordVoicing> candidates, List<ChordVoicing> defaults) {
        Set<String> seen = new HashSet<>();
        List<ChordVoicing> result = new ArrayList<>();

        for (ChordVoicing voicing : defaults) {
            if (result.size() >= TOP_COUNT) {
                break;
            }
            if (seen.add(fretKey(voicing.frets()))) {
                result.add(voicing);
            }
        }

        List<ScoredVoicing> scored = new ArrayList<>();
        for (ChordVoicing voicing : candidates) {
            if (seen.contains(fretKey(voicing.frets()))) {
                continue;
            }
            scored.add(new ScoredVoicing(voicing, score(chord, voicing)));
        }
        scored.sort(Comparator.comparingInt(ScoredVoicing::score).reversed());

        for (ScoredVoicing entry : scored) {
            if (result.size() >= TOP_COUNT) {
                break;
            }
            int[] frets = entry.voicing().frets();
            if (!seen.add(fretKey(frets))) {
                continue;
            }
            if (isNearDuplicateOfAny(frets, result)) {
                continue;
            }
            result.add(entry.voicing());
        }

        return result;
    }

    private static String fretKey(int[] frets) {
        return Arrays.toString(frets);
    }

    /**
     * Treats a candidate as redundant if it differs from an already-picked voicing by only
     * a single string's fret (same muting pattern otherwise) - e.g. a barre shape with one
     * inner string left "open", which isn't actually playable alongside a full barre.
     */
    private static boolean isNearDuplicateOfAny(int[] frets, List<ChordVoicing> existing) {
        for (ChordVoicing voicing : existing) {
            if (isNearDuplicate(frets, voicing.frets())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNearDuplicate(int[] a, int[] b) {
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            boolean aMuted = a[i] < 0;
            boolean bMuted = b[i] < 0;
            if (aMuted != bMuted) {
                return false;
            }
            if (!aMuted && a[i] != b[i]) {
                diff++;
            }
        }
        return diff <= 1;
    }

    private static int score(Chord chord, ChordVoicing voicing) {
        int[] frets = voicing.frets();
        int score = 0;

        int played = 0;
        int muted = 0;
        int fretSum = 0;
        for (int fret : frets) {
            if (fret < 0) {
                muted++;
            } else {
                played++;
                if (fret == 0) {
                    score += 10;
                } else {
                    fretSum += fret;
                }
            }
        }

        score -= muted * 4;
        score += Math.max(0, 60 - fretSum);

        if (played > 0) {
            score += played * 2;
        }

        PitchClass lowest = VoicingGenerator.lowestPlayedPitchClass(frets);
        if (chord.bass().isPresent()) {
            if (lowest == chord.bass().get()) {
                score += 25;
            } else if (lowest != null) {
                score -= 8;
            }
        } else if (lowest == chord.root()) {
            score += 20;
        }

        if (frets[0] == 0 && frets[1] >= 0) {
            score -= 12;
        }

        if (VoicingGenerator.isPlayable(frets)) {
            score += 5;
        }

        return score;
    }

    private record ScoredVoicing(ChordVoicing voicing, int score) {
    }
}
