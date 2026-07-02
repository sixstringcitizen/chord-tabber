package com.sixstringcitizen.chordtabber.js;

import com.sixstringcitizen.chordtabber.api.ChordJson;
import org.teavm.jso.JSExport;

/**
 * TeaVM entry point for browser builds.
 */
public final class ChordTabberJs {

    private ChordTabberJs() {
    }

    @JSExport
    public static String describe(String chordName) {
        try {
            return ChordJson.describe(chordName);
        } catch (IllegalArgumentException ex) {
            return ChordJson.error(ex.getMessage());
        }
    }
}
