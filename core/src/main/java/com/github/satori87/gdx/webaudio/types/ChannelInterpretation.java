package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of channel interpretation modes that determine how audio channels
 * are mapped when up-mixing or down-mixing between different channel layouts.
 * Maps to the Web Audio API {@code ChannelInterpretation} enumeration.
 *
 * @see AudioNode.channelInterpretation (Web Audio API)
 */
public enum ChannelInterpretation {
    /** Uses well-known speaker channel layouts (mono, stereo, quad, 5.1, etc.) for up/down-mixing. */
    SPEAKERS("speakers"),
    /** Channels are mapped by index without any speaker-layout-aware mixing. Extra channels are dropped; missing channels are filled with silence. */
    DISCRETE("discrete");

    private final String jsValue;

    ChannelInterpretation(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this channel interpretation.
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
     * @return the matching {@code ChannelInterpretation}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static ChannelInterpretation fromJsValue(String value) {
        for (ChannelInterpretation interp : values()) {
            if (interp.jsValue.equals(value)) return interp;
        }
        throw new IllegalArgumentException("Unknown ChannelInterpretation: " + value);
    }
}
