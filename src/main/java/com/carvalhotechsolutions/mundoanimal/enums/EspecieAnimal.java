package com.carvalhotechsolutions.mundoanimal.enums;

public enum EspecieAnimal {
    CACHORRO("Cachorro"),
    GATO("Gato"),
    PEIXE("Peixe"),
    PASSARO("Pássaro"),
    COELHO("Coelho"),
    HAMSTER("Hamster"),
    CAVALO("Cavalo"),
    REPTIL("Réptil"),
    ANFIBIO("Anfíbio"),
    OUTRO("Outro");

    private final String displayName;

    EspecieAnimal(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

