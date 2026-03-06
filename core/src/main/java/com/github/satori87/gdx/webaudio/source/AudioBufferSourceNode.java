package com.github.satori87.gdx.webaudio.source;

import com.github.satori87.gdx.webaudio.AudioBuffer;
import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Plays audio data from an {@link AudioBuffer}.
 * Corresponds to the Web Audio API {@code AudioBufferSourceNode} interface.
 *
 * <p>This is a one-shot source node: once playback has started, the node cannot be restarted.
 * Create a new instance for each playback. Supports looping, playback rate control, and
 * detuning via automatable {@link AudioParam} properties.</p>
 *
 * <p>For simpler sound effect or music playback without manual node management, use
 * {@link com.github.satori87.gdx.webaudio.WebSound WebSound} or
 * {@link com.github.satori87.gdx.webaudio.WebMusic WebMusic} which handle one-shot
 * node creation and lifecycle automatically.</p>
 */
public interface AudioBufferSourceNode extends AudioScheduledSourceNode {

    /**
     * Returns the audio buffer assigned to this source node.
     *
     * @return the current {@link AudioBuffer}, or {@code null} if none is set
     */
    AudioBuffer getBuffer();

    /**
     * Assigns an audio buffer containing the audio data to play.
     *
     * @param buffer the {@link AudioBuffer} to play
     */
    void setBuffer(AudioBuffer buffer);

    /**
     * Returns the playback speed factor. A value of 1.0 plays at normal speed.
     *
     * @return the playback rate {@link AudioParam}, default value 1.0
     */
    AudioParam getPlaybackRate();

    /**
     * Returns the detuning of playback in cents.
     *
     * @return the detune {@link AudioParam}, default value 0
     */
    AudioParam getDetune();

    /**
     * Returns whether the audio buffer should loop when it reaches the end.
     *
     * @return {@code true} if looping is enabled
     */
    boolean isLoop();

    /**
     * Sets whether the audio buffer should loop when it reaches the end.
     *
     * @param loop {@code true} to enable looping, {@code false} to disable
     */
    void setLoop(boolean loop);

    /**
     * Returns the time offset in seconds where looping begins.
     *
     * @return the loop start time in seconds
     */
    double getLoopStart();

    /**
     * Sets the time offset in seconds where looping begins.
     *
     * @param loopStart the loop start time in seconds
     */
    void setLoopStart(double loopStart);

    /**
     * Returns the time offset in seconds where looping ends.
     *
     * @return the loop end time in seconds; 0 means loop to the end of the buffer
     */
    double getLoopEnd();

    /**
     * Sets the time offset in seconds where looping ends.
     *
     * @param loopEnd the loop end time in seconds; 0 means loop to the end of the buffer
     */
    void setLoopEnd(double loopEnd);

    /**
     * Starts playback at the specified time, beginning from the given offset into the buffer.
     *
     * @param when   the audio context time at which to start playback, in seconds
     * @param offset the position in the buffer to begin playback, in seconds
     */
    void start(double when, double offset);

    /**
     * Starts playback at the specified time, from the given offset, for the given duration.
     *
     * @param when     the audio context time at which to start playback, in seconds
     * @param offset   the position in the buffer to begin playback, in seconds
     * @param duration the length of audio to play, in seconds
     */
    void start(double when, double offset, double duration);
}
