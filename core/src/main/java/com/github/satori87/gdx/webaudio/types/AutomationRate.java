package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of automation rates for an {@code AudioParam}, controlling how
 * frequently the parameter value is recalculated.
 * Maps to the Web Audio API {@code AutomationRate} enumeration.
 *
 * @see AudioParam.automationRate (Web Audio API)
 */
public enum AutomationRate {
    /** Audio-rate: the parameter value is recalculated for every sample frame (highest precision). */
    A_RATE("a-rate"),
    /** Control-rate: the parameter value is recalculated once per rendering quantum (128 frames). More efficient but less precise. */
    K_RATE("k-rate");

    private final String jsValue;

    AutomationRate(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this automation rate.
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
     * @return the matching {@code AutomationRate}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static AutomationRate fromJsValue(String value) {
        for (AutomationRate rate : values()) {
            if (rate.jsValue.equals(value)) return rate;
        }
        throw new IllegalArgumentException("Unknown AutomationRate: " + value);
    }
}
