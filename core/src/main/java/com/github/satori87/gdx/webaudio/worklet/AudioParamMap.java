package com.github.satori87.gdx.webaudio.worklet;

import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * A read-only map of named {@link AudioParam} instances exposed by an {@link AudioWorkletNode}.
 * Corresponds to the Web Audio API {@code AudioParamMap} interface.
 *
 * <p>Each entry maps a parameter name (as defined by the worklet processor's
 * {@code parameterDescriptors}) to its corresponding {@link AudioParam}.</p>
 */
public interface AudioParamMap {

    /**
     * Returns the audio parameter with the given name.
     *
     * @param name the parameter name
     * @return the {@link AudioParam} for the given name, or {@code null} if not found
     */
    AudioParam get(String name);

    /**
     * Returns whether a parameter with the given name exists in this map.
     *
     * @param name the parameter name to check
     * @return {@code true} if the parameter exists
     */
    boolean has(String name);

    /**
     * Returns the number of parameters in this map.
     *
     * @return the parameter count
     */
    int size();

    /**
     * Returns the names of all parameters in this map.
     *
     * @return an array of parameter names
     */
    String[] keys();
}
