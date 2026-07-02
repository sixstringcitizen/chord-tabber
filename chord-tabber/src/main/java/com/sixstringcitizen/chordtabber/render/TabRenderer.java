package com.sixstringcitizen.chordtabber.render;

import com.sixstringcitizen.chordtabber.voicing.ChordVoicing;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders voicings as ASCII guitar tablature.
 */
public final class TabRenderer {

    private static final String[] DISPLAY_ORDER = {"e", "B", "G", "D", "A", "E"};
    private static final int[] DISPLAY_INDEX = {5, 4, 3, 2, 1, 0};

    public String render(ChordVoicing voicing) {
        StringBuilder builder = new StringBuilder();
        int[] frets = voicing.frets();
        for (int i = 0; i < DISPLAY_ORDER.length; i++) {
            int stringIndex = DISPLAY_INDEX[i];
            int fret = frets[stringIndex];
            builder.append(DISPLAY_ORDER[i])
                    .append("|--")
                    .append(formatFret(fret))
                    .append("--\n");
        }
        return builder.toString().stripTrailing();
    }

    public List<String> renderAll(List<ChordVoicing> voicings) {
        List<String> tabs = new ArrayList<>();
        for (ChordVoicing voicing : voicings) {
            tabs.add(render(voicing));
        }
        return tabs;
    }

    private static String formatFret(int fret) {
        if (fret < 0) {
            return "x";
        }
        return Integer.toString(fret);
    }
}
