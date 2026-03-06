package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Provides dynamic range compression, reducing the volume of loud sounds and boosting quiet ones.
 * Corresponds to the Web Audio API {@code DynamicsCompressorNode} interface.
 *
 * <p>Compression is commonly used to prevent clipping and to even out the dynamic range of audio.
 * All threshold, knee, ratio, attack, and release parameters are automatable.</p>
 */
public interface DynamicsCompressorNode extends AudioNode {

    /**
     * Returns the decibel level above which compression begins.
     *
     * @return the threshold {@link AudioParam}, default value -24 dB
     */
    AudioParam getThreshold();

    /**
     * Returns the decibel range above the threshold over which compression transitions from no effect to full effect.
     *
     * @return the knee {@link AudioParam}, default value 30 dB
     */
    AudioParam getKnee();

    /**
     * Returns the amount of gain reduction applied (input dB / output dB) when the signal exceeds the threshold.
     *
     * @return the ratio {@link AudioParam}, default value 12
     */
    AudioParam getRatio();

    /**
     * Returns the time in seconds to reduce the gain by 10 dB once the signal exceeds the threshold.
     *
     * @return the attack {@link AudioParam}, default value 0.003 seconds
     */
    AudioParam getAttack();

    /**
     * Returns the time in seconds to increase the gain by 10 dB once the signal drops below the threshold.
     *
     * @return the release {@link AudioParam}, default value 0.25 seconds
     */
    AudioParam getRelease();

    /**
     * Returns the current amount of gain reduction being applied by the compressor, in dB.
     *
     * @return the current gain reduction in dB (typically a negative value)
     */
    float getReduction();
}
