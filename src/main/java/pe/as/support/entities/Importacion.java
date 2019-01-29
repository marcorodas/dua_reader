package pe.as.support.entities;

public enum Importacion {
    DEFINITIVA(10);

    private final int value;

    Importacion(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
