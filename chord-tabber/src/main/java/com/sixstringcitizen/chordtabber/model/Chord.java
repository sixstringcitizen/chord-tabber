package com.sixstringcitizen.chordtabber.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Parsed representation of a chord symbol.
 */
public final class Chord {

    private final PitchClass root;
    private final PitchClass bass;
    private final Triad triad;
    private final SeventhType seventh;
    private final List<Extension> extensions;
    private final List<AddedNote> addedNotes;
    private final List<Alteration> alterations;
    private final List<Omit> omit;
    private final String originalSymbol;

    public Chord(
            PitchClass root,
            PitchClass bass,
            Triad triad,
            SeventhType seventh,
            List<Extension> extensions,
            List<AddedNote> addedNotes,
            List<Alteration> alterations,
            List<Omit> omit,
            String originalSymbol) {
        this.root = Objects.requireNonNull(root, "root");
        this.bass = bass;
        this.triad = Objects.requireNonNull(triad, "triad");
        this.seventh = seventh;
        this.extensions = List.copyOf(extensions);
        this.addedNotes = List.copyOf(addedNotes);
        this.alterations = List.copyOf(alterations);
        this.omit = List.copyOf(omit);
        this.originalSymbol = Objects.requireNonNull(originalSymbol, "originalSymbol");
    }

    public PitchClass root() {
        return root;
    }

    public Optional<PitchClass> bass() {
        return Optional.ofNullable(bass);
    }

    public Triad triad() {
        return triad;
    }

    public Optional<SeventhType> seventh() {
        return Optional.ofNullable(seventh);
    }

    public List<Extension> extensions() {
        return extensions;
    }

    public List<AddedNote> addedNotes() {
        return addedNotes;
    }

    public List<Alteration> alterations() {
        return alterations;
    }

    public List<Omit> omit() {
        return omit;
    }

    public String originalSymbol() {
        return originalSymbol;
    }

    public static Builder builder(PitchClass root, String originalSymbol) {
        return new Builder(root, originalSymbol);
    }

    public static final class Builder {
        private final PitchClass root;
        private final String originalSymbol;
        private PitchClass bass;
        private Triad triad = Triad.MAJOR;
        private SeventhType seventh;
        private final List<Extension> extensions = new ArrayList<>();
        private final List<AddedNote> addedNotes = new ArrayList<>();
        private final List<Alteration> alterations = new ArrayList<>();
        private final List<Omit> omit = new ArrayList<>();

        private Builder(PitchClass root, String originalSymbol) {
            this.root = root;
            this.originalSymbol = originalSymbol;
        }

        public Builder bass(PitchClass bass) {
            this.bass = bass;
            return this;
        }

        public Builder triad(Triad triad) {
            this.triad = triad;
            return this;
        }

        public Builder seventh(SeventhType seventh) {
            this.seventh = seventh;
            return this;
        }

        public Builder addExtension(Extension extension) {
            extensions.add(extension);
            return this;
        }

        public Builder addAddedNote(AddedNote addedNote) {
            addedNotes.add(addedNote);
            return this;
        }

        public Builder addAlteration(Alteration alteration) {
            alterations.add(alteration);
            return this;
        }

        public Builder addOmit(Omit omitNote) {
            omit.add(omitNote);
            return this;
        }

        public boolean hasSeventh() {
            return seventh != null;
        }

        public Chord build() {
            return new Chord(root, bass, triad, seventh, extensions, addedNotes, alterations, omit, originalSymbol);
        }
    }
}
