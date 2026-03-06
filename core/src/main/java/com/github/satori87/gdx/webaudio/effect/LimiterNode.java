package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;

/**
 * Limits the audio signal to a maximum level, preventing clipping.
 *
 * <p>Implemented using a dynamics compressor with a hard knee and maximum ratio
 * to approximate a brickwall limiter. Provides simplified controls for ceiling,
 * input gain, attack, and release.</p>
 */
public interface LimiterNode extends AudioNode {

    /** Returns the maximum output level in dB. @return the ceiling, default -1.0 */
    float getCeiling();
    /** Sets the maximum output level in dB. @param ceilingDb the ceiling in dB */
    void setCeiling(float ceilingDb);

    /** Returns the input gain in dB. @return the input gain, default 0.0 */
    float getInputGain();
    /** Sets the input gain in dB applied before limiting. @param gainDb the input gain in dB */
    void setInputGain(float gainDb);

    /** Returns the attack time in milliseconds. @return the attack, default 1.0 */
    float getAttack();
    /** Sets the attack time in milliseconds. @param attackMs the attack in ms */
    void setAttack(float attackMs);

    /** Returns the release time in milliseconds. @return the release, default 100.0 */
    float getRelease();
    /** Sets the release time in milliseconds. @param releaseMs the release in ms */
    void setRelease(float releaseMs);
}
