package org.example.products;

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

    public static Units fromString(String text) {
        if (text != null) {
            for (Units unit : Units.values()) {
                if (text.equalsIgnoreCase(unit.name)) {
                    return unit;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant with text " + text);
    }
}
