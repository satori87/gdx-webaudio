package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of channel count modes that determine how an {@code AudioNode}
 * computes its internal channel count.
 * Maps to the Web Audio API {@code ChannelCountMode} enumeration.
 *
 * @see AudioNode.channelCountMode (Web Audio API)
 */
public enum ChannelCountMode {
    /** The channel count equals the maximum number of channels of all input connections. */
    MAX("max"),
    /** Like {@link #MAX}, but clamped to the node's {@code channelCount} property. */
    CLAMPED_MAX("clamped-max"),
    /** The channel count is exactly the value of the node's {@code channelCount} property. */
    EXPLICIT("explicit");

    private final String jsValue;

    ChannelCountMode(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this channel count mode.
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
     * @return the matching {@code ChannelCountMode}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static ChannelCountMode fromJsValue(String value) {
        for (ChannelCountMode mode : values()) {
            if (mode.jsValue.equals(value)) return mode;
        }
        throw new IllegalArgumentException("Unknown ChannelCountMode: " + value);
    }
}
