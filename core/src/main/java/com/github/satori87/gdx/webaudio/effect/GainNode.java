package com.github.satori87.gdx.webaudio.effect;

import com.github.satori87.gdx.webaudio.AudioNode;
import com.github.satori87.gdx.webaudio.AudioParam;

/**
 * Controls the volume (amplitude) of an audio signal.
 * Corresponds to the Web Audio API {@code GainNode} interface.
 *
 * <p>The gain value is applied as a multiplier to the incoming audio signal.
 * A value of 1.0 means no change, 0.0 means silence, and values greater than 1.0 amplify.</p>
 */
public interface GainNode extends AudioNode {

    /**
     * Returns the gain factor applied to the audio signal.
     *
     * @return the gain {@link AudioParam}, default value 1.0
     */
    AudioParam getGain();
}
