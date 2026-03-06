package com.github.satori87.gdx.webaudio;

/**
 * A mixing bus that groups audio sources with shared volume, pan, and fade controls.
 *
 * <p>Audio sources connect to the group's {@linkplain #getInput() input node}, and the
 * group's {@linkplain #getOutput() output node} connects to the destination or another
 * group. Volume and pan affect all sources routed through the group.</p>
 *
 * <p>{@link WebSound} and {@link WebMusic} instances can route through a group via
 * their {@code setOutput()} method:</p>
 * <pre>{@code
 * SoundGroup sfx = ctx.createSoundGroup();
 * sfx.getOutput().connect(ctx.getDestination());
 * sound.setOutput(sfx.getInput());
 * }</pre>
 */
public interface SoundGroup {

    /**
     * Sets the volume of this group.
     *
     * @param volume the volume level (0.0 = silent, 1.0 = full)
     */
    void setVolume(float volume);

    /**
     * Returns the current volume of this group.
     *
     * @return the volume level
     */
    float getVolume();

    /**
     * Sets the stereo pan of this group.
     *
     * @param pan the pan value (-1.0 = full left, 0.0 = center, 1.0 = full right)
     */
    void setPan(float pan);

    /**
     * Returns the current stereo pan of this group.
     *
     * @return the pan value
     */
    float getPan();

    /**
     * Fades the group volume in from 0 to 1 over the specified duration.
     *
     * @param milliseconds the fade duration in milliseconds
     */
    void fadeIn(float milliseconds);

    /**
     * Fades the group volume in from 0 to the specified target over the given duration.
     *
     * @param milliseconds  the fade duration in milliseconds
     * @param targetVolume  the target volume to reach
     */
    void fadeIn(float milliseconds, float targetVolume);

    /**
     * Fades the group volume out to 0 over the specified duration.
     *
     * @param milliseconds the fade duration in milliseconds
     */
    void fadeOut(float milliseconds);

    /**
     * Fades the group volume out to the specified target over the given duration.
     *
     * @param milliseconds  the fade duration in milliseconds
     * @param targetVolume  the target volume to reach
     */
    void fadeOut(float milliseconds, float targetVolume);

    /**
     * Returns the input node where audio sources should connect.
     *
     * @return the input {@link AudioNode}
     */
    AudioNode getInput();

    /**
     * Returns the output node that delivers the mixed audio signal.
     *
     * @return the output {@link AudioNode}
     */
    AudioNode getOutput();
}
