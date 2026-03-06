package com.github.satori87.gdx.webaudio;

/**
 * A loaded music track for long-form playback with pause, resume, and seek support.
 *
 * <p>Mirrors the libGDX {@code Music} interface semantics for Web Audio.
 * Unlike {@link WebSound}, only one playback instance exists at a time.
 * Supports looping, volume, panning, position seeking, and completion callbacks.</p>
 *
 * <p>Obtain instances via {@link WebAudioContext#loadMusic}.</p>
 *
 * <p>By default, output is routed to the context's destination. Call
 * {@link #setOutput(AudioNode)} to route through a {@code SoundGroup}
 * or effect chain instead.</p>
 */
public interface WebMusic {

    /**
     * Starts or resumes playback. If paused, resumes from the paused position.
     * If stopped, starts from the beginning (or from the position set by
     * {@link #setPosition(float)}).
     */
    void play();

    /** Pauses playback. Call {@link #play()} to resume from the paused position. */
    void pause();

    /** Stops playback and resets the position to the beginning. */
    void stop();

    /**
     * Returns whether this music is currently playing.
     *
     * @return true if playing (not paused or stopped)
     */
    boolean isPlaying();

    /**
     * Sets whether playback should loop when it reaches the end.
     *
     * @param isLooping true to loop, false for single play
     */
    void setLooping(boolean isLooping);

    /**
     * Returns whether this music is set to loop.
     *
     * @return true if looping
     */
    boolean isLooping();

    /**
     * Sets the playback volume.
     *
     * @param volume volume level (0.0 = silent, 1.0 = full)
     */
    void setVolume(float volume);

    /**
     * Returns the current playback volume.
     *
     * @return the volume level
     */
    float getVolume();

    /**
     * Sets the stereo pan and volume.
     *
     * @param pan    stereo pan (-1.0 = full left, 0.0 = center, 1.0 = full right)
     * @param volume volume level (0.0 = silent, 1.0 = full)
     */
    void setPan(float pan, float volume);

    /**
     * Returns the current playback position in seconds.
     *
     * @return the position in seconds, or 0 if stopped
     */
    float getPosition();

    /**
     * Seeks to the given position in seconds. If currently playing, playback
     * continues from the new position. If paused or stopped, the next
     * {@link #play()} will start from this position.
     *
     * @param position the position in seconds
     */
    void setPosition(float position);

    /**
     * Returns the total duration of the music in seconds.
     *
     * @return the duration in seconds
     */
    float getDuration();

    /**
     * Sets the output destination for playback. Use this to route music through
     * a {@code SoundGroup} or effect chain. Defaults to the context's destination node.
     *
     * @param destination the node to route audio to, or null for the default destination
     */
    void setOutput(AudioNode destination);

    /**
     * Returns the underlying audio buffer for advanced use cases.
     *
     * @return the decoded audio buffer
     */
    AudioBuffer getBuffer();

    /**
     * Registers a listener that is called when non-looping playback reaches the end.
     *
     * @param listener the completion listener, or null to remove
     */
    void setOnCompletionListener(OnCompletionListener listener);

    /** Releases all resources and stops playback. */
    void dispose();

    /**
     * Callback invoked when non-looping playback reaches the end of the track.
     */
    interface OnCompletionListener {
        /**
         * Called when playback completes naturally (not via {@link #stop()}).
         *
         * @param music the music instance that completed
         */
        void onCompletion(WebMusic music);
    }

    /**
     * Callback for receiving a loaded {@link WebMusic}.
     */
    interface LoadCallback {
        /**
         * Called when the music has been loaded and is ready to play.
         *
         * @param music the loaded music
         */
        void onLoaded(WebMusic music);
    }
}
