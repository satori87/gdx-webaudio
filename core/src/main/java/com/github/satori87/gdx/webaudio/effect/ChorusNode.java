package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Applies a chorus effect by mixing the dry signal with a delayed copy modulated by an LFO.
 *
 * <p>The chorus effect creates a thicker, richer sound by slightly detuning and delaying
 * a copy of the input signal. Parameters control the LFO rate and depth, the base delay,
 * and the wet/dry mix levels.</p>
 */
public interface ChorusNode extends AudioNode {

    /**
     * Returns the LFO rate in Hz.
     *
     * @return the LFO rate, default 0.5
     */
    float getRate();

    /**
     * Sets the LFO rate in Hz.
     *
     * @param rate the LFO rate in Hz
     */
    void setRate(float rate);

    /**
     * Returns the modulation depth in milliseconds.
     *
     * @return the modulation depth, default 2.0
     */
    float getDepth();

    /**
     * Sets the modulation depth in milliseconds.
     *
     * @param depth the modulation depth in milliseconds
     */
    void setDepth(float depth);

    /**
     * Returns the wet (effected) signal mix level.
     *
     * @return the wet level, default 0.5
     */
    float getWet();

    /**
     * Sets the wet (effected) signal mix level.
     *
     * @param wet the wet level
     */
    void setWet(float wet);

    /**
     * Returns the dry (original) signal mix level.
     *
     * @return the dry level, default 0.5
     */
    float getDry();

    /**
     * Sets the dry (original) signal mix level.
     *
     * @param dry the dry level
     */
    void setDry(float dry);

    /**
     * Returns the base delay time in milliseconds.
     *
     * @return the base delay, default 25.0
     */
    float getDelay();

    /**
     * Sets the base delay time in milliseconds.
     *
     * @param delay the base delay in milliseconds
     */
    void setDelay(float delay);
}
