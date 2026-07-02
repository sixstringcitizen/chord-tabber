package com.sixstringcitizen.chordtabber;

import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.parse.ChordParser;
import com.sixstringcitizen.chordtabber.render.TabRenderer;
import com.sixstringcitizen.chordtabber.theory.ChordTones;
import com.sixstringcitizen.chordtabber.theory.IntervalBuilder;
import com.sixstringcitizen.chordtabber.voicing.ChordVoicing;
import com.sixstringcitizen.chordtabber.voicing.DefaultVoicingProvider;
import com.sixstringcitizen.chordtabber.voicing.VoicingGenerator;
import com.sixstringcitizen.chordtabber.voicing.VoicingRanker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main entry point for chord lookup, voicing generation, and tab rendering.
 */
public final class ChordTabber {

    private final ChordParser chordParser;
    private final IntervalBuilder intervalBuilder;
    private final VoicingGenerator voicingGenerator;
    private final DefaultVoicingProvider defaultVoicingProvider;
    private final VoicingRanker voicingRanker;
    private final TabRenderer tabRenderer;

    public ChordTabber() {
        this(
                new ChordParser(),
                new IntervalBuilder(),
                new VoicingGenerator(),
                new DefaultVoicingProvider(),
                new VoicingRanker(),
                new TabRenderer());
    }

    ChordTabber(
            ChordParser chordParser,
            IntervalBuilder intervalBuilder,
            VoicingGenerator voicingGenerator,
            DefaultVoicingProvider defaultVoicingProvider,
            VoicingRanker voicingRanker,
            TabRenderer tabRenderer) {
        this.chordParser = chordParser;
        this.intervalBuilder = intervalBuilder;
        this.voicingGenerator = voicingGenerator;
        this.defaultVoicingProvider = defaultVoicingProvider;
        this.voicingRanker = voicingRanker;
        this.tabRenderer = tabRenderer;
    }

    public ChordResult describe(String chordName) {
        Objects.requireNonNull(chordName, "chordName");
        Chord chord = chordParser.parse(chordName);
        ChordTones chordTones = intervalBuilder.build(chord);
        List<String> noteNames = new ArrayList<>();
        List<Integer> intervals = chordTones.intervals();
        List<PitchClass> pitchClasses = chordTones.notes();
        for (int i = 0; i < pitchClasses.size(); i++) {
            noteNames.add(pitchClasses.get(i).spellFromInterval(chord.root(), intervals.get(i)));
        }
        List<ChordVoicing> defaults = defaultVoicingProvider.findDefaults(chord, chordTones);
        List<ChordVoicing> candidates = voicingGenerator.generate(chordTones);
        List<ChordVoicing> voicings = voicingRanker.rank(chord, candidates, defaults);
        List<String> tabs = tabRenderer.renderAll(voicings);
        return new ChordResult(chord.originalSymbol(), noteNames, voicings, tabs);
    }
}
