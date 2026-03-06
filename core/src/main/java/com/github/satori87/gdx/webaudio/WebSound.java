package com.github.satori87.gdx.webaudio;

/**
 * A loaded sound effect that supports multiple concurrent playback instances.
 *
 * <p>Mirrors the libGDX {@code Sound} interface semantics for Web Audio.
 * Each {@link #play()} or {@link #loop()} call creates a new playback instance
 * identified by a unique {@code long} ID. Individual instances can be controlled
 * (volume, pitch, pan, pause, resume, stop) via their ID. Global operations
 * (no ID parameter) affect all currently active instances.</p>
 *
 * <p>Obtain instances via {@link WebAudioContext#loadSound}.</p>
 *
 * <p>By default, output is routed to the context's destination. Call
 * {@link #setOutput(AudioNode)} to route through a {@code SpatialAudioSource},
 * {@code SoundGroup}, or effect chain instead.</p>
 */
public interface WebSound {

    /**
     * Plays the sound at full volume. Returns a sound instance ID.
     *
     * @return the instance ID, or -1 if the sound is disposed
     */
    long play();

    /**
     * Plays the sound at the given volume.
     *
     * @param volume volume level (0.0 = silent, 1.0 = full)
     * @return the instance ID, or -1 if the sound is disposed
     */
    long play(float volume);

    /**
     * Plays the sound with the given volume, pitch, and pan.
     *
     * @param volume volume level (0.0 = silent, 1.0 = full)
     * @param pitch  pitch multiplier (1.0 = normal, 2.0 = octave up, 0.5 = octave down)
     * @param pan    stereo pan (-1.0 = full left, 0.0 = center, 1.0 = full right)
     * @return the instance ID, or -1 if the sound is disposed
     */
    long play(float volume, float pitch, float pan);

    /**
     * Plays the sound looped at full volume.
     *
     * @return the instance ID, or -1 if the sound is disposed
     */
    long loop();

    /**
     * Plays the sound looped at the given volume.
     *
     * @param volume volume level (0.0 = silent, 1.0 = full)
     * @return the instance ID, or -1 if the sound is disposed
     */
    long loop(float volume);

    /**
     * Plays the sound looped with the given volume, pitch, and pan.
     *
     * @param volume volume level (0.0 = silent, 1.0 = full)
     * @param pitch  pitch multiplier (1.0 = normal)
     * @param pan    stereo pan (-1.0 = full left, 0.0 = center, 1.0 = full right)
     * @return the instance ID, or -1 if the sound is disposed
     */
    long loop(float volume, float pitch, float pan);

    /** Stops all playing instances of this sound. */
    void stop();

    /**
     * Stops a specific playing instance.
     *
     * @param soundId the instance ID returned by {@link #play()} or {@link #loop()}
     */
    void stop(long soundId);

    /** Pauses all playing instances of this sound. */
    void pause();

    /**
     * Pauses a specific playing instance.
     *
     * @param soundId the instance ID
     */
    void pause(long soundId);

    /** Resumes all paused instances of this sound. */
    void resume();

    /**
     * Resumes a specific paused instance.
     *
     * @param soundId the instance ID
     */
    void resume(long soundId);

    /**
     * Sets whether a specific instance should loop.
     *
     * @param soundId  the instance ID
     * @param looping  true to loop, false for one-shot
     */
    void setLooping(long soundId, boolean looping);

    /**
     * Sets the pitch multiplier for a specific instance.
     *
     * @param soundId the instance ID
     * @param pitch   pitch multiplier (1.0 = normal)
     */
    void setPitch(long soundId, float pitch);

    /**
     * Sets the volume for a specific instance.
     *
     * @param soundId the instance ID
     * @param volume  volume level (0.0 = silent, 1.0 = full)
     */
    void setVolume(long soundId, float volume);

    /**
     * Sets the pan and volume for a specific instance.
     *
     * @param soundId the instance ID
     * @param pan     stereo pan (-1.0 = full left, 0.0 = center, 1.0 = full right)
     * @param volume  volume level (0.0 = silent, 1.0 = full)
     */
    void setPan(long soundId, float pan, float volume);

    /**
     * Sets the output destination for all future playback instances.
     * Use this to route sound through a {@code SpatialAudioSource}, {@code SoundGroup},
     * or effect chain. Defaults to the context's destination node.
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

    /** Releases all resources and stops all playing instances. */
    void dispose();

    /**
     * Callback for receiving a loaded {@link WebSound}.
     */
    interface LoadCallback {
        /**
         * Called when the sound has been loaded and is ready to play.
         *
         * @param sound the loaded sound
         */
        void onLoaded(WebSound sound);
    }
}
