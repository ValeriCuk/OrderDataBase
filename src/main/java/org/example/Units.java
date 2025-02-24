package org.example;

public enum Units {
    PIECE("piece"),
    KG("kg"),
    LITER("liter"),
    METER("m");

    private final String name;

    Units(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
