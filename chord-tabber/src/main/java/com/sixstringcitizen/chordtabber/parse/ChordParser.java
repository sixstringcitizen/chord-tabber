package com.sixstringcitizen.chordtabber.parse;

import com.sixstringcitizen.chordtabber.model.AddedNote;
import com.sixstringcitizen.chordtabber.model.Alteration;
import com.sixstringcitizen.chordtabber.model.Chord;
import com.sixstringcitizen.chordtabber.model.Extension;
import com.sixstringcitizen.chordtabber.model.Omit;
import com.sixstringcitizen.chordtabber.model.PitchClass;
import com.sixstringcitizen.chordtabber.model.SeventhType;
import com.sixstringcitizen.chordtabber.model.Triad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Parses standard guitar chord symbols into a structured {@link Chord}.
 */
public final class ChordParser {

    private static final List<TokenHandler> HANDLERS = buildHandlers();

    public Chord parse(String chordName) {
        String original = chordName.trim();
        String normalized = ChordAliasNormalizer.normalize(original);

        String bassPart = null;
        String symbol = normalized;
        int slash = normalized.indexOf('/');
        if (slash >= 0) {
            symbol = normalized.substring(0, slash);
            bassPart = normalized.substring(slash + 1);
            if (bassPart.isBlank()) {
                throw new IllegalArgumentException("Missing bass note after slash");
            }
        }

        ParseCursor cursor = new ParseCursor(symbol);
        PitchClass root = cursor.readRoot();
        Chord.Builder builder = Chord.builder(root, original);

        while (!cursor.isAtEnd()) {
            if (!consumeNext(cursor, builder)) {
                throw new IllegalArgumentException(
                        "Unable to parse chord symbol near '" + cursor.remaining() + "' in " + original);
            }
        }

        if (bassPart != null) {
            builder.bass(PitchClass.parse(bassPart));
        }

        return builder.build();
    }

    private static boolean consumeNext(ParseCursor cursor, Chord.Builder builder) {
        String remaining = cursor.remaining();
        for (TokenHandler handler : HANDLERS) {
            String token = handler.token();
            boolean matches = token.length() == 1
                    ? remaining.startsWith(token)
                    : remaining.regionMatches(true, 0, token, 0, token.length());
            if (matches) {
                handler.apply(builder);
                cursor.advance(token.length());
                return true;
            }
        }
        return false;
    }

    private static List<TokenHandler> buildHandlers() {
        List<TokenHandler> handlers = new ArrayList<>();

        handlers.add(token("maj13", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("maj11", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("maj9", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("major13", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("major11", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("major9", b -> {
            b.seventh(SeventhType.MAJOR);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("major7", b -> b.seventh(SeventhType.MAJOR)));
        handlers.add(token("maj7", b -> b.seventh(SeventhType.MAJOR)));
        handlers.add(token("Maj7", b -> b.seventh(SeventhType.MAJOR)));
        handlers.add(token("M7", b -> b.seventh(SeventhType.MAJOR)));
        handlers.add(token("major", b -> b.triad(Triad.MAJOR)));
        handlers.add(token("maj", b -> b.triad(Triad.MAJOR)));
        handlers.add(token("M", b -> b.triad(Triad.MAJOR)));

        handlers.add(token("m7b5", b -> {
            b.triad(Triad.DIMINISHED);
            b.seventh(SeventhType.MINOR);
        }));
        handlers.add(token("min7b5", b -> {
            b.triad(Triad.DIMINISHED);
            b.seventh(SeventhType.MINOR);
        }));
        handlers.add(token("-7b5", b -> {
            b.triad(Triad.DIMINISHED);
            b.seventh(SeventhType.MINOR);
        }));

        handlers.add(token("dim7", b -> {
            b.triad(Triad.DIMINISHED);
            b.seventh(SeventhType.DIMINISHED);
        }));
        handlers.add(token("dim", b -> b.triad(Triad.DIMINISHED)));
        handlers.add(token("o7", b -> {
            b.triad(Triad.DIMINISHED);
            b.seventh(SeventhType.DIMINISHED);
        }));
        handlers.add(token("o", b -> b.triad(Triad.DIMINISHED)));

        handlers.add(token("aug", b -> b.triad(Triad.AUGMENTED)));
        handlers.add(token("+", b -> b.triad(Triad.AUGMENTED)));

        handlers.add(token("sus2", b -> b.triad(Triad.SUS2)));
        handlers.add(token("sus4", b -> b.triad(Triad.SUS4)));

        handlers.add(token("add13", b -> b.addAddedNote(AddedNote.ADD13)));
        handlers.add(token("add11", b -> b.addAddedNote(AddedNote.ADD11)));
        handlers.add(token("add9", b -> b.addAddedNote(AddedNote.ADD9)));
        handlers.add(token("add4", b -> b.addAddedNote(AddedNote.ADD4)));
        handlers.add(token("add2", b -> b.addAddedNote(AddedNote.ADD2)));

        handlers.add(token("minor13", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("minor11", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("minor9", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("minor7", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
        }));
        handlers.add(token("minor6", b -> {
            b.triad(Triad.MINOR);
            b.addExtension(Extension.SIX);
        }));
        handlers.add(token("minor", b -> b.triad(Triad.MINOR)));
        handlers.add(token("min13", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("min11", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("min9", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("min7", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
        }));
        handlers.add(token("min6", b -> {
            b.triad(Triad.MINOR);
            b.addExtension(Extension.SIX);
        }));
        handlers.add(token("min", b -> b.triad(Triad.MINOR)));
        handlers.add(token("m13", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("m11", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("m9", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("m7", b -> {
            b.triad(Triad.MINOR);
            b.seventh(SeventhType.MINOR);
        }));
        handlers.add(token("m6", b -> {
            b.triad(Triad.MINOR);
            b.addExtension(Extension.SIX);
        }));
        handlers.add(token("m", b -> b.triad(Triad.MINOR)));
        handlers.add(token("-", b -> b.triad(Triad.MINOR)));

        handlers.add(token("13", b -> {
            ensureDominantSeventh(b);
            b.addExtension(Extension.THIRTEEN);
        }));
        handlers.add(token("11", b -> {
            ensureDominantSeventh(b);
            b.addExtension(Extension.ELEVEN);
        }));
        handlers.add(token("9", b -> {
            ensureDominantSeventh(b);
            b.addExtension(Extension.NINE);
        }));
        handlers.add(token("6", b -> b.addExtension(Extension.SIX)));
        handlers.add(token("7", b -> b.seventh(SeventhType.DOMINANT)));

        handlers.add(token("5", b -> b.triad(Triad.POWER5)));

        handlers.add(token("no3", b -> b.addOmit(Omit.NO3)));
        handlers.add(token("no5", b -> b.addOmit(Omit.NO5)));

        handlers.add(token("#11", b -> b.addAlteration(Alteration.SHARP11)));
        handlers.add(token("b13", b -> b.addAlteration(Alteration.FLAT13)));
        handlers.add(token("#9", b -> b.addAlteration(Alteration.SHARP9)));
        handlers.add(token("b9", b -> b.addAlteration(Alteration.FLAT9)));
        handlers.add(token("#5", b -> b.addAlteration(Alteration.SHARP5)));
        handlers.add(token("b5", b -> b.addAlteration(Alteration.FLAT5)));

        handlers.sort(Comparator.comparingInt((TokenHandler h) -> h.token().length()).reversed());
        return handlers;
    }

    private static void ensureDominantSeventh(Chord.Builder builder) {
        if (!builder.hasSeventh()) {
            builder.seventh(SeventhType.DOMINANT);
        }
    }

    private static TokenHandler token(String token, java.util.function.Consumer<Chord.Builder> action) {
        return new TokenHandler(token, action);
    }

    private static final class TokenHandler {
        private final String token;
        private final java.util.function.Consumer<Chord.Builder> action;

        private TokenHandler(String token, java.util.function.Consumer<Chord.Builder> action) {
            this.token = token;
            this.action = action;
        }

        String token() {
            return token;
        }

        void apply(Chord.Builder builder) {
            action.accept(builder);
        }
    }

    private static final class ParseCursor {
        private final String source;
        private int index;

        private ParseCursor(String source) {
            this.source = source;
        }

        boolean isAtEnd() {
            return index >= source.length();
        }

        String remaining() {
            return source.substring(index);
        }

        void advance(int count) {
            index += count;
        }

        PitchClass readRoot() {
            if (isAtEnd()) {
                throw new IllegalArgumentException("Missing root note");
            }
            char letter = source.charAt(index);
            if (letter < 'A' || letter > 'G') {
                throw new IllegalArgumentException("Invalid root note: " + letter);
            }
            index++;
            String accidental = "";
            if (!isAtEnd()) {
                char next = source.charAt(index);
                if (next == '#' || next == 'b') {
                    accidental = String.valueOf(next);
                    index++;
                }
            }
            return PitchClass.parse(letter + accidental);
        }
    }
}
