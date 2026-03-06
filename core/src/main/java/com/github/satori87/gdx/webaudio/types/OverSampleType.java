package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of oversampling rates used by a {@code WaveShaperNode} to reduce aliasing artifacts.
 * Maps to the Web Audio API {@code OverSampleType} enumeration.
 * <p>
 * Higher oversampling improves quality at the cost of increased CPU usage.
 *
 * @see WaveShaperNode.oversample (Web Audio API)
 */
public enum OverSampleType {
    /** No oversampling is applied. */
    NONE("none"),
    /** 2x oversampling: the signal is upsampled to double the sample rate before shaping. */
    TWO_X("2x"),
    /** 4x oversampling: the signal is upsampled to quadruple the sample rate before shaping. */
    FOUR_X("4x");

    private final String jsValue;

    OverSampleType(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this oversample type.
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
     * @return the matching {@code OverSampleType}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static OverSampleType fromJsValue(String value) {
        for (OverSampleType type : values()) {
            if (type.jsValue.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown OverSampleType: " + value);
    }
}
