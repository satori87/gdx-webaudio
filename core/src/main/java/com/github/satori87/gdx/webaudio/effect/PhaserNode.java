package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Applies a phaser effect using cascaded allpass filters with LFO-modulated frequencies.
 *
 * <p>The phaser creates a sweeping, notch-filtering effect by splitting the signal,
 * passing one copy through a series of allpass filters whose frequencies are modulated
 * by an LFO, and mixing it back with the dry signal. The number of stages, frequency
 * range, feedback, and wet/dry mix are all configurable.</p>
 */
public interface PhaserNode extends AudioNode {

    /** Returns the LFO rate in Hz. @return the rate, default 0.5 */
    float getRate();
    /** Sets the LFO rate in Hz. @param rate the rate */
    void setRate(float rate);

    /** Returns the modulation depth (0 to 1). @return the depth, default 0.5 */
    float getDepth();
    /** Sets the modulation depth (0 to 1). @param depth the depth */
    void setDepth(float depth);

    /** Returns the wet signal level. @return the wet level, default 0.5 */
    float getWet();
    /** Sets the wet signal level. @param wet the wet level */
    void setWet(float wet);

    /** Returns the dry signal level. @return the dry level, default 0.5 */
    float getDry();
    /** Sets the dry signal level. @param dry the dry level */
    void setDry(float dry);

    /** Returns the number of allpass filter stages. @return the stages, default 4 */
    int getStages();
    /** Sets the number of allpass filter stages. @param stages the stage count */
    void setStages(int stages);

    /** Returns the feedback level. @return the feedback, default 0.0 */
    float getFeedback();
    /** Sets the feedback level. @param feedback the feedback */
    void setFeedback(float feedback);

    /** Returns the minimum sweep frequency in Hz. @return the min frequency, default 200 */
    float getFrequencyRangeMin();
    /** Sets the minimum sweep frequency in Hz. @param min the min frequency */
    void setFrequencyRangeMin(float min);

    /** Returns the maximum sweep frequency in Hz. @return the max frequency, default 4000 */
    float getFrequencyRangeMax();
    /** Sets the maximum sweep frequency in Hz. @param max the max frequency */
    void setFrequencyRangeMax(float max);
}
