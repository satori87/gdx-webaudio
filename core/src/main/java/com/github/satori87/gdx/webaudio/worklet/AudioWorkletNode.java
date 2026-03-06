package com.github.satori87.gdx.webaudio.worklet;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Runs custom audio processing code defined by an AudioWorkletProcessor.
 * Corresponds to the Web Audio API {@code AudioWorkletNode} interface.
 *
 * <p>The worklet processor runs on a dedicated audio rendering thread, enabling
 * low-latency custom audio processing. Parameters exposed by the processor are
 * accessible through the {@link AudioParamMap} returned by {@link #getParameters()}.</p>
 */
public interface AudioWorkletNode extends AudioNode {

    /**
     * Returns the map of named audio parameters exposed by the underlying worklet processor.
     *
     * @return the {@link AudioParamMap} containing the processor's parameters
     */
    AudioParamMap getParameters();

    /**
     * Sets a callback to be invoked when an error occurs in the worklet processor.
     *
     * @param listener the callback to invoke on processor error, or {@code null} to clear
     */
    void setOnProcessorError(Runnable listener);
}
