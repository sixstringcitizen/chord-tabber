package com.sixstringcitizen.chordtabber.render;

import com.sixstringcitizen.chordtabber.voicing.ChordVoicing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TabRendererTest {

    private final TabRenderer renderer = new TabRenderer();

    @Test
    void rendersMutedStringsAsX() {
        ChordVoicing voicing = new ChordVoicing("Open Cmaj7", "Cmaj7", new int[] {-1, 3, 2, 0, 0, 0});
        String tab = renderer.render(voicing);
        assertTrue(tab.contains("E|--x--"));
        assertTrue(tab.contains("e|--0--"));
    }
}
