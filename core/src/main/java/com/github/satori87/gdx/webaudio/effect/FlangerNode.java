package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Applies a flanger effect using a short modulated delay with feedback.
 *
 * <p>Similar to chorus but with a shorter delay and a feedback path that creates
 * a more pronounced comb-filtering effect. The metallic, sweeping character is
 * controlled by the LFO rate, depth, base delay, and feedback amount.</p>
 */
public interface FlangerNode extends AudioNode {

    /** Returns the LFO rate in Hz. @return the LFO rate, default 0.2 */
    float getRate();
    /** Sets the LFO rate in Hz. @param rate the LFO rate */
    void setRate(float rate);

    /** Returns the modulation depth in milliseconds. @return the depth, default 2.0 */
    float getDepth();
    /** Sets the modulation depth in milliseconds. @param depth the depth */
    void setDepth(float depth);

    /** Returns the wet signal mix level. @return the wet level, default 0.5 */
    float getWet();
    /** Sets the wet signal mix level. @param wet the wet level */
    void setWet(float wet);

    /** Returns the dry signal mix level. @return the dry level, default 0.5 */
    float getDry();
    /** Sets the dry signal mix level. @param dry the dry level */
    void setDry(float dry);

    /** Returns the base delay time in milliseconds. @return the base delay, default 5.0 */
    float getDelay();
    /** Sets the base delay time in milliseconds. @param delay the base delay */
    void setDelay(float delay);

    /** Returns the feedback level. @return the feedback, default 0.5 */
    float getFeedback();
    /** Sets the feedback level. @param feedback the feedback level */
    void setFeedback(float feedback);
}
