package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of noise types for a {@code NoiseNode}.
 */
public enum NoiseType {
    /** White noise with equal energy per frequency (flat spectrum). */
    WHITE("white"),
    /** Pink noise with equal energy per octave (1/f spectrum). */
    PINK("pink"),
    /** Brownian (red) noise with energy falling 6 dB per octave (1/f² spectrum). */
    BROWNIAN("brownian");

    private final String jsValue;

    NoiseType(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the string value corresponding to this noise type.
     *
     * @return the string identifier
     */
    public String toJsValue() {
        return jsValue;
    }

    /**
     * Returns the enum constant matching the given string value.
     *
     * @param value the string identifier
     * @return the matching {@code NoiseType}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static NoiseType fromJsValue(String value) {
        for (NoiseType type : values()) {
            if (type.jsValue.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown NoiseType: " + value);
    }
}
