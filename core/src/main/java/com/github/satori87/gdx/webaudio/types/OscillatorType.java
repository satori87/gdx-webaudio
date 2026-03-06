package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of waveform types for an {@code OscillatorNode}.
 * Maps to the Web Audio API {@code OscillatorType} enumeration.
 *
 * @see OscillatorNode.type (Web Audio API)
 */
public enum OscillatorType {
    /** A sine wave, the most fundamental waveform with no harmonics. */
    SINE("sine"),
    /** A square wave with odd harmonics that fall off at 1/n amplitude. */
    SQUARE("square"),
    /** A sawtooth wave containing all harmonics that fall off at 1/n amplitude. */
    SAWTOOTH("sawtooth"),
    /** A triangle wave with odd harmonics that fall off at 1/n squared amplitude. */
    TRIANGLE("triangle"),
    /** A custom waveform defined by a {@code PeriodicWave}. */
    CUSTOM("custom");

    private final String jsValue;

    OscillatorType(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this oscillator type.
     *
     * @return the JavaScript enum string
     */
    public String toJsValue() {
        return jsValue;
    }

    /**
     * Returns the enum constant matching the given JavaScript string value.
     *
     * @param value the JavaScript enum string
     * @return the matching {@code OscillatorType}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static OscillatorType fromJsValue(String value) {
        for (OscillatorType type : values()) {
            if (type.jsValue.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown OscillatorType: " + value);
    }
}
