package com.sixstringcitizen.chordtabber.cli;

import com.sixstringcitizen.chordtabber.ChordResult;
import com.sixstringcitizen.chordtabber.ChordTabber;

public final class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: chord-tabber <chord-name> [chord-name...]");
            System.exit(1);
        }

        ChordTabber tabber = new ChordTabber();
        for (String chordName : args) {
            try {
                ChordResult result = tabber.describe(chordName);
                print(result);
            } catch (IllegalArgumentException ex) {
                System.err.println(chordName + ": " + ex.getMessage());
            }
            System.out.println();
        }
    }

    private static void print(ChordResult result) {
        System.out.println(result.chordName());
        System.out.println("Notes: " + String.join(" ", result.notes()));
        for (int i = 0; i < result.voicings().size(); i++) {
            System.out.println("[" + (i + 1) + "] " + result.voicings().get(i).name());
            System.out.println(result.tabs().get(i));
        }
    }
}
