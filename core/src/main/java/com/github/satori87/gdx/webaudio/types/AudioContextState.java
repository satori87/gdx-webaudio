package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of possible states for a {@code WebAudioContext}.
 * Maps to the Web Audio API {@code AudioContextState} enumeration.
 *
 * @see AudioContext.state (Web Audio API)
 */
public enum AudioContextState {
    /** The context is suspended and not processing audio. This is the initial state in many browsers. */
    SUSPENDED("suspended"),
    /** The context is running and actively processing audio. */
    RUNNING("running"),
    /** The context has been closed and can no longer be used. */
    CLOSED("closed");

    private final String jsValue;

    AudioContextState(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this context state.
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
     * @return the matching {@code AudioContextState}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static AudioContextState fromJsValue(String value) {
        for (AudioContextState state : values()) {
            if (state.jsValue.equals(value)) return state;
        }
        throw new IllegalArgumentException("Unknown AudioContextState: " + value);
    }
}
