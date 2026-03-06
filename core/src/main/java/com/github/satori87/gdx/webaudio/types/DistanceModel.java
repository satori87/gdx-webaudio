package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of distance attenuation models used by a {@code PannerNode}.
 * Maps to the Web Audio API {@code DistanceModelType} enumeration.
 * <p>
 * The distance model determines how the audio volume decreases as the source
 * moves further from the listener.
 *
 * @see PannerNode.distanceModel (Web Audio API)
 */
public enum DistanceModel {
    /** Linear distance attenuation: gain decreases linearly between refDistance and maxDistance. */
    LINEAR("linear"),
    /** Inverse distance attenuation: gain is inversely proportional to distance. This is the default. */
    INVERSE("inverse"),
    /** Exponential distance attenuation: gain decreases exponentially with distance. */
    EXPONENTIAL("exponential");

    private final String jsValue;

    DistanceModel(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this distance model.
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
     * @return the matching {@code DistanceModel}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static DistanceModel fromJsValue(String value) {
        for (DistanceModel model : values()) {
            if (model.jsValue.equals(value)) return model;
        }
        throw new IllegalArgumentException("Unknown DistanceModel: " + value);
    }
}
