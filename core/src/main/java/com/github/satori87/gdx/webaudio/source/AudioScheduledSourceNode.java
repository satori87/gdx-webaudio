package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Base interface for audio source nodes that can be scheduled to start and stop at precise times.
 * Corresponds to the Web Audio API {@code AudioScheduledSourceNode} interface.
 *
 * <p>Source nodes extending this interface produce audio output and support time-based
 * scheduling relative to the audio context's current time.</p>
 */
public interface AudioScheduledSourceNode extends AudioNode {

    /**
     * Starts playing the audio source immediately.
     */
    void start();

    /**
     * Starts playing the audio source at the specified time.
     *
     * @param when the audio context time at which to start playback, in seconds
     */
    void start(double when);

    /**
     * Stops the audio source immediately.
     */
    void stop();

    /**
     * Stops the audio source at the specified time.
     *
     * @param when the audio context time at which to stop playback, in seconds
     */
    void stop(double when);

    /**
     * Sets a callback to be invoked when the source node has stopped playing.
     *
     * @param listener the callback to invoke when playback ends, or {@code null} to clear
     */
    void setOnEnded(Runnable listener);
}
