package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of filter types for a {@code BiquadFilterNode}.
 * Maps to the Web Audio API {@code BiquadFilterType} enumeration.
 *
 * @see BiquadFilterNode.type (Web Audio API)
 */
public enum BiquadFilterType {
    /** A low-pass filter that attenuates frequencies above the cutoff frequency. */
    LOWPASS("lowpass"),
    /** A high-pass filter that attenuates frequencies below the cutoff frequency. */
    HIGHPASS("highpass"),
    /** A band-pass filter that attenuates frequencies outside a frequency band. */
    BANDPASS("bandpass"),
    /** A low-shelf filter that boosts or attenuates frequencies below the cutoff. */
    LOWSHELF("lowshelf"),
    /** A high-shelf filter that boosts or attenuates frequencies above the cutoff. */
    HIGHSHELF("highshelf"),
    /** A peaking (parametric) EQ filter that boosts or attenuates a band of frequencies. */
    PEAKING("peaking"),
    /** A notch (band-reject) filter that attenuates a narrow band of frequencies. */
    NOTCH("notch"),
    /** An all-pass filter that passes all frequencies but alters phase relationships. */
    ALLPASS("allpass");

    private final String jsValue;

    BiquadFilterType(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this filter type.
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
     * @return the matching {@code BiquadFilterType}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static BiquadFilterType fromJsValue(String value) {
        for (BiquadFilterType type : values()) {
            if (type.jsValue.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown BiquadFilterType: " + value);
    }
}
